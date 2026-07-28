package com.jobportal.controller;

import java.util.List;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.jobportal.dto.AboutDto;
import com.jobportal.dto.CertificationDto;
import com.jobportal.dto.EducationDto;
import com.jobportal.dto.ExperienceDto;
import com.jobportal.dto.HeaderDto;
import com.jobportal.dto.LanguageDto;
import com.jobportal.dto.LinksDto;
import com.jobportal.dto.ProfileDTO;
import com.jobportal.dto.SkillDto;
import com.jobportal.dto.SkillsDto;
import com.jobportal.dto.UserDTO;
import com.jobportal.entity.Profile;
import com.jobportal.exception.JobPortalException;
import com.jobportal.service.ProfileService;
import com.jobportal.service.UserService;

@RestController
@RequestMapping("/profile")
public class ProfileController {

	@Autowired
	public ProfileService profileService;
	
	@Autowired
	public UserService userService;
	
	@GetMapping()
	public ResponseEntity<UserDTO> getUserProfile(
	        @RequestHeader("Authorization") String jwt)
	        throws JobPortalException {

	    return ResponseEntity.ok(userService.getUserProfile(jwt));
	}
	
	
	@GetMapping("/{email}")
	public ResponseEntity<ProfileDTO> getProfileByEmail(@PathVariable String email) throws JobPortalException{
		
		ProfileDTO dto = profileService.getProfileByEmail(email);
		
		return new ResponseEntity<>(dto,HttpStatus.OK);
		
	}
	
	
	
	@PutMapping("/profile-image/{id}")
	public ResponseEntity<String> updateProfileImage(
	        @RequestParam("file") MultipartFile file,
	        @PathVariable Long id) throws Exception {

	    return ResponseEntity.ok(profileService.updateProfileImage(file, id));
	}
	
	
	@PutMapping("/banner-image/{id}")
	public ResponseEntity<String> updateBannerImage(
	        @RequestParam("file") MultipartFile file,
	        @PathVariable Long id) throws Exception {

	    return ResponseEntity.ok(profileService.updateBannerImage(file, id));
	}
	
	@GetMapping("/user/{email}")
	public ResponseEntity<Profile> getProfileForUser(@PathVariable String email) throws JobPortalException{
		
		return new ResponseEntity<>(profileService.getProfileForUser(email),HttpStatus.OK);
	}
	
	@PutMapping("/header/{id}")
	public ResponseEntity<HeaderDto> updateHeader(@RequestBody HeaderDto dto , @PathVariable Long id) throws JobPortalException{
		
		
		
		return new ResponseEntity<>(profileService.updateHeader(dto, id),HttpStatus.OK);
	}
	
	@PutMapping("/links/{id}")
	public ResponseEntity<LinksDto> updateLinks(
	        @RequestBody LinksDto dto,
	        @PathVariable Long id) throws JobPortalException {

	    return ResponseEntity.ok(profileService.updateLinks(dto, id));
	}
	
	
	@PutMapping("/about/{id}")
	public ResponseEntity<AboutDto> updateAbout(
	        @RequestBody AboutDto dto,
	        @PathVariable Long id) throws JobPortalException {

	    return ResponseEntity.ok(profileService.updateAbout(dto, id));
	}
	
	@PutMapping("/skills/{id}")
	public ResponseEntity<SkillsDto> updateSkills(
	        @RequestBody SkillsDto dto,
	        @PathVariable Long id) throws JobPortalException {

	    return ResponseEntity.ok(profileService.updateSkills(dto, id));
	}
	
	
	@PostMapping("/skill/{id}")
	public ResponseEntity<SkillDto> addSkill(
	        @RequestBody SkillDto dto,
	        @PathVariable Long id) throws JobPortalException {

	    return ResponseEntity.status(HttpStatus.CREATED)
	            .body(profileService.addSkill(dto, id));
	}
	
	@DeleteMapping("/skills/{id}")
	public ResponseEntity<String> removeSkill(
	        @RequestParam String skill,
	        @PathVariable Long id) throws JobPortalException {

	    profileService.removeSkill(skill, id);

	    return ResponseEntity.ok("Skill removed successfully.");
	}
	
	
	@PostMapping("/experience/{id}")
	public ResponseEntity<ExperienceDto> addExperience(
	        @RequestBody ExperienceDto dto,
	        @PathVariable Long id) throws JobPortalException {

	    return new ResponseEntity<>(
	            profileService.addExperience(dto, id),
	            HttpStatus.CREATED
	    );
	}
	
	
	@PutMapping("/experience/{experienceId}")
	public ResponseEntity<ExperienceDto> updateExperience(
	        @RequestBody ExperienceDto dto,
	        @PathVariable Long experienceId) throws JobPortalException {

	    return ResponseEntity.ok(
	            profileService.updateExperience(dto, experienceId)
	    );
	}
	
	@GetMapping("/experience/{id}")
	public ResponseEntity<List<ExperienceDto>> getExperiences(
	        @PathVariable Long id) throws JobPortalException {

	    return ResponseEntity.ok(
	            profileService.getExperiences(id));
	}
	
	@DeleteMapping("/experience/{experienceId}")
	public ResponseEntity<String> deleteExperience(
	        @PathVariable Long experienceId) throws JobPortalException {

	    profileService.deleteExperience(experienceId);

	    return ResponseEntity.ok("Experience deleted successfully.");
	}
	
	@PostMapping("/education/{id}")
	public ResponseEntity<EducationDto> addEducation(
	        @RequestBody EducationDto dto,
	        @PathVariable Long id) throws JobPortalException {

	    return new ResponseEntity<>(
	            profileService.addEducation(dto, id),
	            HttpStatus.CREATED);
	}
	
	
	@DeleteMapping("/education/{educationId}")
	public ResponseEntity<String> deleteEducation(
	        @PathVariable Long educationId) throws JobPortalException {

	    profileService.deleteEducation(educationId);

	    return ResponseEntity.ok("Education deleted successfully.");
	}
	
	
	@GetMapping("/education/{id}")
	public ResponseEntity<List<EducationDto>> getEducation(
	        @PathVariable Long id) throws JobPortalException {

	    return ResponseEntity.ok(
	            profileService.getEducation(id));
	}
	
	
	@PostMapping("/certification/{id}")
	public ResponseEntity<CertificationDto> addCertification(
	        @RequestBody CertificationDto dto,
	        @PathVariable Long id) throws JobPortalException {

	    return new ResponseEntity<>(
	            profileService.addCertification(dto, id),
	            HttpStatus.CREATED);
	}
	
	
	@PutMapping("/certification/{certificationId}")
	public ResponseEntity<CertificationDto> updateCertification(
	        @RequestBody CertificationDto dto,
	        @PathVariable Long certificationId) throws JobPortalException {

	    return ResponseEntity.ok(
	            profileService.updateCertification(dto, certificationId));
	}
	
	
	@DeleteMapping("/certification/{certificationId}")
	public ResponseEntity<String> deleteCertification(
	        @PathVariable Long certificationId) throws JobPortalException {

	    profileService.deleteCertification(certificationId);

	    return ResponseEntity.ok("Certification deleted successfully.");
	}
	
	
	@GetMapping("/certification/{id}")
	public ResponseEntity<List<CertificationDto>> getCertifications(
	        @PathVariable Long id) throws JobPortalException {

	    return ResponseEntity.ok(
	            profileService.getCertifications(id));
	}
	
	
	
	
	@PostMapping("/languages/{id}")
	public ResponseEntity<LanguageDto> addLanguage(
	        @RequestBody LanguageDto dto,
	        @PathVariable Long id) throws JobPortalException {

	    return new ResponseEntity<>(
	            profileService.addLanguage(dto, id),
	            HttpStatus.CREATED);
	}
	
	@DeleteMapping("/languages/{id}")
	public ResponseEntity<String> removeLanguage(
	        @RequestParam String language,
	        @PathVariable Long id) throws JobPortalException {

	    profileService.removeLanguage(language, id);

	    return ResponseEntity.ok("Language removed successfully.");
	}
	
	
	@GetMapping("/languages/{id}")
	public ResponseEntity<List<String>> getLanguages(
	        @PathVariable Long id) throws JobPortalException {

	    return ResponseEntity.ok(
	            profileService.getLanguages(id));
	}
}
