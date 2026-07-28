package com.jobportal.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jobportal.config.JwtProvider;
import com.jobportal.dto.LoginDTO;
import com.jobportal.dto.ResetPasswordRequest;
import com.jobportal.dto.ResponseDTO;
import com.jobportal.dto.UserDTO;
import com.jobportal.dto.VerifyOtpRequest;
import com.jobportal.exception.JobPortalException;
import com.jobportal.response.AuthResponse;
import com.jobportal.service.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/users")
@Validated
public class UserController {

	@Autowired
	private UserService userService;
	
	@Autowired
	private JwtProvider jwtProvider;
	
	@Autowired
	private AuthenticationManager authenticationManager;
	
	@PostMapping("/register")
	public ResponseEntity<UserDTO> registerUser(@RequestBody @Valid UserDTO userDTO) throws JobPortalException {

	    UserDTO savedUser = userService.registerUser(userDTO);

	    return ResponseEntity.status(HttpStatus.CREATED).body(savedUser);
	}
	
	
	@PostMapping("/login")
	public ResponseEntity<AuthResponse> loginUser(@RequestBody LoginDTO loginDTO) {

	    Authentication authentication = authenticationManager.authenticate(
	            new UsernamePasswordAuthenticationToken(
	                    loginDTO.getEmail(),
	                    loginDTO.getPassword()));

	    SecurityContextHolder.getContext().setAuthentication(authentication);

	    String token = JwtProvider.generateToken(authentication);

	    AuthResponse response = new AuthResponse();
	    response.setToken(token);
	    response.setMessage("Login Successful");

	    return ResponseEntity.ok(response);
	}
	
	@PostMapping("/sendOtp/{email}")
	public ResponseEntity<ResponseDTO> SendOtp(@PathVariable String email) throws Exception {

		   userService.sendOtp(email);

	    return new ResponseEntity<>(new ResponseDTO("OTP sent successfully"),HttpStatus.OK);
	} 
	
	@PostMapping("/verify-otp")
	public ResponseEntity<String> verifyOtp(@RequestBody VerifyOtpRequest request)
	        throws Exception {

	    userService.verifyOtp(request.getEmail(), request.getOtp());

	    return ResponseEntity.ok("OTP verified successfully.");
	}
	
	@PostMapping("/reset-password")
	public ResponseEntity<String> resetPassword(
	        @RequestBody ResetPasswordRequest request) throws Exception {

	    userService.resetPassword(
	            request.getEmail(),
	            request.getNewPassword());

	    return ResponseEntity.ok("Password reset successfully.");
	}
	
	
	
	
	
}
