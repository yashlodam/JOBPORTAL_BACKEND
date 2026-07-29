package com.jobportal.serviceImpl;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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
import com.jobportal.entity.Certification;
import com.jobportal.entity.Education;
import com.jobportal.entity.Experience;
import com.jobportal.entity.Profile;
import com.jobportal.entity.User;
import com.jobportal.exception.JobPortalException;
import com.jobportal.repository.CertificationRepository;
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
	private CertificationRepository certificationRepository;
	
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
	            .orElseThrow(() -> new JobPortalException("Profile Not Found with given id"));

	    if (headetdto.getName() != null &&
	            !headetdto.getName().trim().isEmpty()) {
	        p.setName(headetdto.getName().trim());
	    }

	    if (headetdto.getJobTitle() != null &&
	            !headetdto.getJobTitle().trim().isEmpty()) {
	        p.setJobTitle(headetdto.getJobTitle().trim());
	    }

	    if (headetdto.getCompany() != null &&
	            !headetdto.getCompany().trim().isEmpty()) {
	        p.setCompany(headetdto.getCompany().trim());
	    }

	    if (headetdto.getLocation() != null &&
	            !headetdto.getLocation().trim().isEmpty()) {
	        p.setLocation(headetdto.getLocation().trim());
	    }

	    if (headetdto.getAvailability() != null) {
	        p.setAvailability(headetdto.getAvailability());
	    }

	    if (headetdto.getExperienceLevel() != null) {
	        p.setExperienceLevel(headetdto.getExperienceLevel());
	    }

	    Profile px = profileRepository.save(p);

	    return mapper.map(px, HeaderDto.class);
	}

	@Override
	public LinksDto updateLinks(LinksDto dto, Long id) throws JobPortalException {

	    Profile p = profileRepository.findById(id)
	            .orElseThrow(() -> new JobPortalException("Profile Not Found with given id"));

	    if (dto.getLinkedinUrl() != null &&
	            !dto.getLinkedinUrl().trim().isEmpty()) {

	        p.setLinkedinUrl(dto.getLinkedinUrl().trim());
	    }

	    if (dto.getGithubUrl() != null &&
	            !dto.getGithubUrl().trim().isEmpty()) {

	        p.setGithubUrl(dto.getGithubUrl().trim());
	    }

	    if (dto.getPortfolioUrl() != null &&
	            !dto.getPortfolioUrl().trim().isEmpty()) {

	        p.setPortfolioUrl(dto.getPortfolioUrl().trim());
	    }

	    Profile px = profileRepository.save(p);

	    return mapper.map(px, LinksDto.class);
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

		System.out.println("Received Skill = " + dto.getSkill());
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

	    if (dto.getTitle() != null && !dto.getTitle().trim().isEmpty()) {
	        experience.setTitle(dto.getTitle().trim());
	    }

	    if (dto.getCompany() != null && !dto.getCompany().trim().isEmpty()) {
	        experience.setCompany(dto.getCompany().trim());
	    }

	    if (dto.getLocation() != null && !dto.getLocation().trim().isEmpty()) {
	        experience.setLocation(dto.getLocation().trim());
	    }

	    if (dto.getStartDate() != null) {
	        experience.setStartDate(dto.getStartDate());
	    }

	    if (dto.getEndDate() != null) {
	        experience.setEndDate(dto.getEndDate());
	    }

	    if (dto.getWorking() != null) {
	        experience.setWorking(dto.getWorking());
	    }

	    if (dto.getDescription() != null && !dto.getDescription().trim().isEmpty()) {
	        experience.setDescription(dto.getDescription().trim());
	    }

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

	@Override
	public CertificationDto addCertification(CertificationDto dto, Long id) throws JobPortalException {

	    Profile profile = profileRepository.findById(id)
	            .orElseThrow(() -> new JobPortalException("Profile not found"));

	    Certification certification = new Certification();

	    certification.setTitle(dto.getTitle());
	    certification.setIssuer(dto.getIssuer());
	    certification.setIssueDate(dto.getIssueDate());
	    certification.setCertificateId(dto.getCertificateId());

	    certification.setProfile(profile);

	    Certification savedCertification = certificationRepository.save(certification);

	    CertificationDto response = new CertificationDto();
	    response.setId(savedCertification.getId());
	    response.setTitle(savedCertification.getTitle());
	    response.setIssuer(savedCertification.getIssuer());
	    response.setIssueDate(savedCertification.getIssueDate());
	    response.setCertificateId(savedCertification.getCertificateId());

	    return response;
	}

	@Override
	public CertificationDto updateCertification(CertificationDto dto, Long certificationId)
	        throws JobPortalException {

	    Certification certification = certificationRepository.findById(certificationId)
	            .orElseThrow(() -> new JobPortalException("Certification not found"));

	    if (dto.getTitle() != null && !dto.getTitle().trim().isEmpty()) {
	        certification.setTitle(dto.getTitle().trim());
	    }

	    if (dto.getIssuer() != null && !dto.getIssuer().trim().isEmpty()) {
	        certification.setIssuer(dto.getIssuer().trim());
	    }

	    if (dto.getIssueDate() != null) {
	        certification.setIssueDate(dto.getIssueDate());
	    }

	    if (dto.getCertificateId() != null && !dto.getCertificateId().trim().isEmpty()) {
	        certification.setCertificateId(dto.getCertificateId().trim());
	    }

	    Certification updatedCertification = certificationRepository.save(certification);

	    CertificationDto response = new CertificationDto();
	    response.setId(updatedCertification.getId());
	    response.setTitle(updatedCertification.getTitle());
	    response.setIssuer(updatedCertification.getIssuer());
	    response.setIssueDate(updatedCertification.getIssueDate());
	    response.setCertificateId(updatedCertification.getCertificateId());

	    return response;
	}
	
	@Override
	public void deleteCertification(Long certificationId) throws JobPortalException {

	    Certification certification = certificationRepository.findById(certificationId)
	            .orElseThrow(() -> new JobPortalException("Certification not found"));

	    certificationRepository.delete(certification);
	}

	@Override
	public List<CertificationDto> getCertifications(Long id) throws JobPortalException {

	    Profile profile = profileRepository.findById(id)
	            .orElseThrow(() -> new JobPortalException("Profile not found"));

	    List<Certification> certifications = certificationRepository.findByProfile(profile);

	    List<CertificationDto> response = new ArrayList<>();

	    for (Certification certification : certifications) {

	        CertificationDto dto = new CertificationDto();

	        dto.setId(certification.getId());
	        dto.setTitle(certification.getTitle());
	        dto.setIssuer(certification.getIssuer());
	        dto.setIssueDate(certification.getIssueDate());
	        dto.setCertificateId(certification.getCertificateId());

	        response.add(dto);
	    }

	    return response;
	}
	
	
	@Override
	public LanguageDto addLanguage(LanguageDto dto, Long id) throws JobPortalException {

	    Profile profile = profileRepository.findById(id)
	            .orElseThrow(() -> new JobPortalException("Profile not found"));

	    List<String> languages = profile.getLanguages();

	    if (languages == null) {
	        languages = new ArrayList<>();
	        profile.setLanguages(languages);
	    }

	    if (!languages.contains(dto.getLanguage())) {
	        languages.add(dto.getLanguage());
	    }

	    profileRepository.save(profile);

	    return dto;
	}
	
	
	
	
	@Override
	public void removeLanguage(String language, Long id) throws JobPortalException {

	    Profile profile = profileRepository.findById(id)
	            .orElseThrow(() -> new JobPortalException("Profile not found"));

	    List<String> languages = profile.getLanguages();

	    if (languages == null || !languages.remove(language)) {
	        throw new JobPortalException("Language not found");
	    }

	    profileRepository.save(profile);
	}
	
	
	
	@Override
	public List<String> getLanguages(Long id) throws JobPortalException {

	    Profile profile = profileRepository.findById(id)
	            .orElseThrow(() -> new JobPortalException("Profile not found"));

	    return profile.getLanguages();
	}

	@Override
	public String updateProfileImage(MultipartFile file, Long profileId) throws Exception {

	    Profile profile = profileRepository.findById(profileId)
	            .orElseThrow(() -> new JobPortalException("Profile not found"));

	    String uploadDir = "uploads/profile/";

	    File directory = new File(uploadDir);

	    if (!directory.exists()) {
	        directory.mkdirs();
	    }

	    String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();

	    Path path = Paths.get(uploadDir + fileName);
	    
	    System.out.println("Saved at: " + path.toAbsolutePath());

	    Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

	    profile.setProfileImage(fileName);

	    profileRepository.save(profile);

	    return fileName;
	}

	@Override
	public String updateBannerImage(MultipartFile file, Long profileId) throws Exception {

	    Profile profile = profileRepository.findById(profileId)
	            .orElseThrow(() -> new JobPortalException("Profile not found"));

	    String uploadDir = "uploads/banner/";

	    File directory = new File(uploadDir);

	    if (!directory.exists()) {
	        directory.mkdirs();
	    }

	    String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();

	    Path path = Paths.get(uploadDir + fileName);

	    Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

	    profile.setBannerImage(fileName);

	    profileRepository.save(profile);

	    return fileName;
	}

}
