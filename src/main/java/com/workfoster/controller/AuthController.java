package com.workfoster.controller;

import java.net.URI;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.workfoster.dto.ApiResponse;
import com.workfoster.dto.JwtAuthenticationResponse;
import com.workfoster.model.LoginRequest;
import com.workfoster.model.Role;
import com.workfoster.model.RoleName;
import com.workfoster.model.SignUpRequest;
import com.workfoster.model.User;
import com.workfoster.repository.RoleRepo;
import com.workfoster.repository.UserRepo;
import com.workfoster.security.JwtTokenProvider;
import com.workfoster.service.SMSService;
import com.workfoster.util.AppException;
import com.workfoster.util.RSAUtil;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(value="*")
public class AuthController {

	@Autowired
	AuthenticationManager authenticationManager;
	
	@Autowired
	private SMSService smsService;

	@Autowired
	UserRepo userRepository;

	@Autowired
	RoleRepo roleRepository;

	@Autowired
	PasswordEncoder passwordEncoder;

	@Autowired
	JwtTokenProvider tokenProvider;

	@PostMapping("/signin")
	public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

		Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(loginRequest.getUsernameOrEmail(), loginRequest.getPassword()));

		SecurityContextHolder.getContext().setAuthentication(authentication);

		String jwt = tokenProvider.generateToken(authentication);
		 if(jwt!=null)
		 {
			 smsService.sendSMS(); 
		 }
		
		
		return ResponseEntity.ok(new JwtAuthenticationResponse(jwt));
	}

	

	@PostMapping("/otp/{otp}")
	public ResponseEntity<ApiResponse> checkOTP(@PathVariable Long otp) {
	 ApiResponse apiResponse= new ApiResponse();
	 if(otp == RSAUtil.otp)
		 apiResponse.setSuccess(true);
		
		return ResponseEntity.ok().body(apiResponse);
	
	}
	
	@PostMapping("/signup")
	public ResponseEntity<?> registerUser(@Valid @RequestBody SignUpRequest signUpRequest) throws NoSuchAlgorithmException {
		if (userRepository.existsByUsername(signUpRequest.getUsername())) {
			return new ResponseEntity(new ApiResponse(false, "Username is already taken!"), HttpStatus.BAD_REQUEST);
		}

		if (userRepository.existsByEmail(signUpRequest.getEmail())) {
			return new ResponseEntity(new ApiResponse(false, "Email Address already in use!"), HttpStatus.BAD_REQUEST);
		}

		// Creating user's account
		User user = new User(signUpRequest.getName(), signUpRequest.getUsername(), signUpRequest.getEmail(),
				signUpRequest.getPassword());

		user.setPassword(passwordEncoder.encode(user.getPassword()));

		Role userRole = roleRepository.findByName(RoleName.ROLE_USER)
				.orElseThrow(() -> new AppException("User Role not set."));

		user.setRoles(Collections.singleton(userRole));
		
		
		 KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
	        keyGen.initialize(2048);
	        KeyPair pair = keyGen.generateKeyPair();
	        user.setPrivateKey(pair.getPrivate().toString().getBytes());
	        user.setPublicKey(pair.getPublic().toString().getBytes());
	        
		User result = userRepository.save(user);

		URI location = ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/users/{username}")
				.buildAndExpand(result.getUsername()).toUri();

		return ResponseEntity.created(location).body(new ApiResponse(true, "User registered successfully"));
	}
}
