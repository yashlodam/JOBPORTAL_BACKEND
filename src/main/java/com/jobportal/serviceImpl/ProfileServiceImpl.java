package com.jobportal.serviceImpl;

import java.util.ArrayList;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jobportal.dto.AboutDto;
import com.jobportal.dto.EducationDto;
import com.jobportal.dto.ExperienceDto;
import com.jobportal.dto.HeaderDto;
import com.jobportal.dto.LinksDto;
import com.jobportal.dto.ProfileDTO;
import com.jobportal.dto.SkillDto;
import com.jobportal.dto.SkillsDto;
import com.jobportal.entity.Education;
import com.jobportal.entity.Experience;
import com.jobportal.entity.Profile;
import com.jobportal.entity.User;
import com.jobportal.exception.JobPortalException;
import com.jobportal.repository.EducationRepository;
import com.jobportal.repository.ExperienceRepository;
import com.jobportal.repository.ProfileRepository;
import com.jobportal.repository.UserRepository;
import com.jobportal.service.ProfileService;


@Service
public class ProfileServiceImpl implements ProfileService {
	
	
	@Autowired
	private ProfileRepository profileRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private ModelMapper mapper;
	
	@Autowired
	private ExperienceRepository experienceRepository;
	
	@Autowired
	private EducationRepository educationRepository;

	@Override
	public ProfileDTO getProfileByEmail(String email) throws JobPortalException {
		
	  Profile profile = profileRepository.findByEmail(email).orElseThrow(()-> new JobPortalException("Profile Not Found with given id"));
		
		return mapper.map(profile, ProfileDTO.class);
	}

	@Override
	public Profile getProfileForUser(String email) throws JobPortalException {
		
		User user = userRepository.findByEmail(email)
				.orElseThrow(()-> new JobPortalException("User Not Found"))
				;
		
		Profile profile = user.getProfile();
		
		return profile;
	}

	@Override
	public HeaderDto updateHeader(HeaderDto headetdto, Long id) throws JobPortalException {
		
		Profile p = profileRepository.findById(id)
				    .orElseThrow(()-> new JobPortalException("Profile Not Found with given id"));
		
		p.setName(headetdto.getName());
		p.setJobTitle(headetdto.getJobTitle());
		p.setCompany(headetdto.getCompany());
		p.setLocation(headetdto.getLocation());
		p.setAvailability(headetdto.getAvailability());
		p.setExperienceLevel(headetdto.getExperienceLevel());
		
		Profile px = profileRepository.save(p);
		
		return mapper.map(px, HeaderDto.class);
	}

	@Override
	public LinksDto updateLinks(LinksDto dto, Long id) throws JobPortalException {
		
		Profile p = profileRepository.findById(id)
				    .orElseThrow(()-> new JobPortalException("Profile Not Found with given id"));
		
		p.setLinkedinUrl(dto.getLinkedinUrl());
		p.setGithubUrl(dto.getGithubUrl());
		p.setPortfolioUrl(dto.getPortfolioUrl());
		
		Profile px = profileRepository.save(p);
		
		
		return  mapper.map(px,LinksDto.class);
	}

	@Override
	public AboutDto updateAbout(AboutDto dto, Long id) throws JobPortalException {
		
		Profile p = profileRepository.findById(id)
				   .orElseThrow(()-> new JobPortalException("Profile Not Found "));
		
		p.setAbout(dto.getAbout());
		
		Profile px = profileRepository.save(p);
		
		return mapper.map(px, AboutDto.class);
	}

	@Override
	public SkillsDto updateSkills(SkillsDto dto, Long id) throws JobPortalException {

	    Profile profile = profileRepository.findById(id)
	            .orElseThrow(() -> new JobPortalException("Profile not found"));

	    profile.setSkills(dto.getSkills());

	    Profile updatedProfile = profileRepository.save(profile);

	    SkillsDto response = new SkillsDto();
	    response.setSkills(updatedProfile.getSkills());

	    return response;
	}

	@Override
	public SkillDto addSkill(SkillDto dto, Long id) throws JobPortalException {

	    Profile profile = profileRepository.findById(id)
	            .orElseThrow(() -> new JobPortalException("Profile not found"));

	    List<String> skills = profile.getSkills();

	    // Initialize if null
	    if (skills == null) {
	        skills = new ArrayList<>();
	        profile.setSkills(skills);
	    }

	    // Prevent duplicate skills
	    if (!skills.contains(dto.getSkill())) {
	        skills.add(dto.getSkill());
	    }

	    profileRepository.save(profile);

	    return dto;
	}

	@Override
	public void removeSkill(String skill, Long id) throws JobPortalException {

	    Profile profile = profileRepository.findById(id)
	            .orElseThrow(() -> new JobPortalException("Profile not found"));

	    List<String> skills = profile.getSkills();

	    if (skills == null || !skills.remove(skill)) {
	        throw new JobPortalException("Skill not found");
	    }

	    profileRepository.save(profile);
	}

	@Override
	public ExperienceDto addExperience(ExperienceDto dto, Long id) throws JobPortalException {

	    Profile profile = profileRepository.findById(id)
	            .orElseThrow(() -> new JobPortalException("Profile not found"));

	    Experience experience = mapper.map(dto, Experience.class);

	    experience.setProfile(profile);

	    Experience savedExperience = experienceRepository.save(experience);
	    

	    return mapper.map(savedExperience, ExperienceDto.class);
	}

	@Override
	public ExperienceDto updateExperience(ExperienceDto dto, Long experienceId) throws JobPortalException {

	    Experience experience = experienceRepository.findById(experienceId)
	            .orElseThrow(() -> new JobPortalException("Experience not found"));

	    experience.setTitle(dto.getTitle());
	    experience.setCompany(dto.getCompany());
	    experience.setLocation(dto.getLocation());
	    experience.setStartDate(dto.getStartDate());
	    experience.setEndDate(dto.getEndDate());
	    experience.setWorking(dto.getWorking());
	    experience.setDescription(dto.getDescription());

	    Experience updatedExperience = experienceRepository.save(experience);

	    ExperienceDto response = new ExperienceDto();
	    response.setId(updatedExperience.getId());
	    response.setTitle(updatedExperience.getTitle());
	    response.setCompany(updatedExperience.getCompany());
	    response.setLocation(updatedExperience.getLocation());
	    response.setStartDate(updatedExperience.getStartDate());
	    response.setEndDate(updatedExperience.getEndDate());
	    response.setWorking(updatedExperience.getWorking());
	    response.setDescription(updatedExperience.getDescription());

	    return response;
	}

	@Override
	public void deleteExperience(Long experienceId) throws JobPortalException {

	    Experience experience = experienceRepository.findById(experienceId)
	            .orElseThrow(() -> new JobPortalException("Experience not found"));

	    experienceRepository.delete(experience);
	}

	@Override
	public EducationDto addEducation(EducationDto dto, Long id) throws JobPortalException {

	    Profile profile = profileRepository.findById(id)
	            .orElseThrow(() -> new JobPortalException("Profile not found"));

	    Education education = new Education();

	    education.setDegree(dto.getDegree());
	    education.setCollegeName(dto.getCollegeName());
	    education.setUniversity(dto.getUniversity());
	    education.setStartDate(dto.getStartDate());
	    education.setEndDate(dto.getEndDate());
	    education.setLocation(dto.getLocation());

	    education.setProfile(profile);

	    Education savedEducation = educationRepository.save(education);

	    EducationDto response = new EducationDto();
	    response.setId(savedEducation.getId());
	    response.setDegree(savedEducation.getDegree());
	    response.setCollegeName(savedEducation.getCollegeName());
	    response.setUniversity(savedEducation.getUniversity());
	    response.setStartDate(savedEducation.getStartDate());
	    response.setEndDate(savedEducation.getEndDate());
	    response.setLocation(savedEducation.getLocation());

	    return response;
	}

	@Override
	public void deleteEducation(Long educationId) throws JobPortalException {

	    Education education = educationRepository.findById(educationId)
	            .orElseThrow(() -> new JobPortalException("Education not found"));

	    educationRepository.delete(education);
	}

	@Override
	public List<EducationDto> getEducation(Long id) throws JobPortalException {

	    Profile profile = profileRepository.findById(id)
	            .orElseThrow(() -> new JobPortalException("Profile not found"));

	    List<Education> educations = educationRepository.findByProfile(profile);

	    return educations.stream()
	            .map(education -> mapper.map(education, EducationDto.class))
	            .toList();
	}

	@Override
	public List<ExperienceDto> getExperiences(Long id) throws JobPortalException {

	    Profile profile = profileRepository.findById(id)
	            .orElseThrow(() -> new JobPortalException("Profile not found"));

	    List<Experience> experiences = experienceRepository.findByProfile(profile);

	    return experiences.stream()
	            .map(experience -> mapper.map(experience, ExperienceDto.class))
	            .toList();
	}

}
