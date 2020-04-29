package com.workfoster.service;
import org.springframework.web.multipart.MultipartFile;

import com.workfoster.dto.ApiResponse;
import com.workfoster.model.DBFile;

public interface DBFileStorageService {
	
	 public DBFile storeFile(MultipartFile file,Long userId);
	 public DBFile getFile(String fileId);
	public ApiResponse fileListByUserId(Long userId);

}