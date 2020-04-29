package com.workfoster.service.serviceimpl;

import java.util.Random;

import org.springframework.stereotype.Service;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.workfoster.service.SMSService;
import com.workfoster.util.RSAUtil;

@Service
public class SMSServiceTwilio implements SMSService {
	// Find your Account Sid and Token at twilio.com/console
	public static final String ACCOUNT_SID = "AC9708854320d4ba71e0d927311034d3ad";
	
	public static final String AUTH_TOKEN = "faed4dc2bdca9db2c47c419f54554baa";

	@Override
	public Message sendSMS() {
		
		Random rand = new Random();
		System.out.printf("otp"+"%04d%n", rand.nextInt(10000));
		RSAUtil.otp=rand.nextInt(10000);
		
		Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
		Message message = Message.creator(new com.twilio.type.PhoneNumber("+918489485254"), // The phone number you are
		new com.twilio.type.PhoneNumber("+12564821378"), // The Twilio phone number
				"Your OTP is "+RSAUtil.otp).create();
		System.out.println("message send successfully");
        System.out.println(message.getBody()); // will be empty string
   
	
		return message;
	}
}