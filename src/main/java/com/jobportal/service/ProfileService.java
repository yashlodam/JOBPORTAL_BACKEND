package com.jobportal.service;


import java.util.List;

import org.jspecify.annotations.Nullable;
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
import com.jobportal.entity.Profile;
import com.jobportal.exception.JobPortalException;

public interface ProfileService {

	ProfileDTO getProfileByEmail(String email) throws JobPortalException;
    Profile getProfileForUser(String email) throws JobPortalException;
	
    HeaderDto updateHeader(HeaderDto headetdto,Long id) throws JobPortalException;
	
	LinksDto updateLinks(LinksDto dto, Long id) throws JobPortalException;
	
	AboutDto updateAbout(AboutDto dto, Long id) throws JobPortalException;
	
	SkillsDto updateSkills(SkillsDto dto, Long id) throws JobPortalException;
	
	SkillDto addSkill(SkillDto dto, Long id) throws JobPortalException;
	void removeSkill(String skill, Long id) throws JobPortalException;
	ExperienceDto addExperience(ExperienceDto dto, Long id) throws JobPortalException;
	
	ExperienceDto updateExperience(ExperienceDto dto, Long experienceId) throws JobPortalException;
	void deleteExperience(Long experienceId) throws JobPortalException;
	EducationDto addEducation(EducationDto dto, Long id) throws JobPortalException;
	void deleteEducation(Long educationId) throws JobPortalException;
	List<EducationDto> getEducation(Long id) throws JobPortalException;
	List<ExperienceDto> getExperiences(Long id) throws JobPortalException;
	CertificationDto addCertification(CertificationDto dto, Long id) throws JobPortalException;

	CertificationDto updateCertification(CertificationDto dto, Long certificationId) throws JobPortalException;

	void deleteCertification(Long certificationId) throws JobPortalException;

	List<CertificationDto> getCertifications(Long id) throws JobPortalException;
	LanguageDto addLanguage(LanguageDto dto, Long id) throws JobPortalException;
	void removeLanguage(String language, Long id) throws JobPortalException;
	List<String> getLanguages(Long id) throws JobPortalException;
	String updateProfileImage(MultipartFile file, Long profileId) throws Exception;
	String updateBannerImage(MultipartFile file, Long profileId) throws Exception;
	
	
	
    
}
