package com.jobportal.dto;


public class ResponseDTO {

	String message;

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public ResponseDTO(String message) {
		super();
		this.message = message;
	}
	
	public ResponseDTO() {
		
	}
}
