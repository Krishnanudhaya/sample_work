package com.workfoster.service.serviceimpl;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Optional;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.workfoster.dto.ApiResponse;
import com.workfoster.model.DBFile;
import com.workfoster.model.User;
import com.workfoster.repository.DBFileRepository;
import com.workfoster.repository.UserRepo;
import com.workfoster.service.DBFileStorageService;
import com.workfoster.util.FileStorageException;
import com.workfoster.util.MyFileNotFoundException;
import com.workfoster.util.RSAUtil;

@Service
public class DBFileStorageServiceImpl implements DBFileStorageService {

	private static String publicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCgFGVfrY4jQSoZQWWygZ83roKXWD4YeT2x2p41dGkPixe73rT2IW04glagN2vgoZoHuOPqa5and6kAmK2ujmCHu6D1auJhE2tXP+yLkpSiYMQucDKmCsWMnW9XlC5K7OSL77TXXcfvTvyZcjObEz6LIBRzs6+FqpFbUO9SJEfh6wIDAQAB";

	@Autowired
	UserRepo userRepository;

	@Autowired
	private DBFileRepository dbFileRepository;

	public DBFile storeFile(MultipartFile file, Long userId) {
		// Normalize file name
		String fileName = StringUtils.cleanPath(file.getOriginalFilename());

		try {
			// Check if the file's name contains invalid characters
			if (fileName.contains("..")) {
				throw new FileStorageException("Sorry! Filename contains invalid path sequence " + fileName);
			}

			Optional<User> findById = userRepository.findById(userId);
			System.out.println("DB file save public key" + findById.get().getPublicKey().toString());
			
			DBFile dbFile = new DBFile(fileName, file.getContentType(),
					RSAUtil.encryptToDB(file.getBytes(), publicKey));

			dbFile.setUserId(userId);
			return dbFileRepository.save(dbFile);
		} catch (IOException | InvalidKeyException | BadPaddingException | IllegalBlockSizeException
				| NoSuchPaddingException | NoSuchAlgorithmException ex) {
			throw new FileStorageException("Could not store file " + fileName + ". Please try again!", ex);
		}
	}

	public DBFile getFile(String fileId) {
		return dbFileRepository.findById(fileId)
				.orElseThrow(() -> new MyFileNotFoundException("File not found with id " + fileId));
	}

	@Override
	public ApiResponse fileListByUserId(Long userId) {
		ApiResponse apiResponse = new ApiResponse();
		List<DBFile> byUserId = dbFileRepository.getByUserId(userId);
		apiResponse.setPayload(byUserId);
		return apiResponse;
	}
}
