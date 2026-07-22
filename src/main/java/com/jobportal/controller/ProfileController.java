package com.jobportal.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jobportal.dto.ProfileDTO;
import com.jobportal.exception.JobPortalException;
import com.jobportal.service.ProfileService;

@RestController
@RequestMapping("/profile")
public class ProfileController {

	@Autowired
	public ProfileService profileService;
	
	
	@GetMapping("/{email}")
	public ResponseEntity<ProfileDTO> getProfileByEmail(@PathVariable String email) throws JobPortalException{
		
		ProfileDTO dto = profileService.getProfileByEmail(email);
		
		return new ResponseEntity<>(dto,HttpStatus.OK);
		
	}
	
	@GetMapping("/get/{id}")
	public ResponseEntity<ProfileDTO> getProfile(@PathVariable Long id) throws JobPortalException{
		
		return new ResponseEntity<>(profileService.getProfile(id),HttpStatus.OK);
	}
	
	
	
	@PutMapping("/update")
	public ResponseEntity<ProfileDTO> upDateProfile(@RequestBody ProfileDTO profileDTO) throws JobPortalException{
		
		return new ResponseEntity<>(profileService.upDateProfile(profileDTO),HttpStatus.OK);
	}
}
