package com.jobportal.service;


import com.jobportal.dto.AboutDto;
import com.jobportal.dto.EducationDto;
import com.jobportal.dto.ExperienceDto;
import com.jobportal.dto.HeaderDto;
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
    
    
}
