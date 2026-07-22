package com.jobportal.service;

import com.jobportal.dto.LoginDTO;
import com.jobportal.dto.UserDTO;
import com.jobportal.exception.JobPortalException;

public interface UserService {

	UserDTO registerUser(UserDTO userDTO);
	UserDTO loginUser(LoginDTO loginDTO) throws JobPortalException;
	Boolean sendOtp(String email) throws Exception;
	Boolean verifyOtp(String email, String otp);
	Boolean resetPassword(String email, String newPassword) throws JobPortalException;
	
}
