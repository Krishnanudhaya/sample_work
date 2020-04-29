package com.workfoster.dto;

public class ApiResponse {

	private Boolean success;
	private String message;
	private Object payload;

	public ApiResponse(Boolean success, String message) {
		this.success = success;
		this.message = message;
	}

	public ApiResponse() {
		// TODO Auto-generated constructor stub
	}

	public Boolean getSuccess() {
		return success;
	}

	public void setSuccess(Boolean success) {
		this.success = success;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Object getPayload() {
		return payload;
	}

	public void setPayload(Object payload) {
		this.payload = payload;
	}
}
