package com.jobportal.serviceImpl;



import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jobportal.dto.ProfileDTO;
import com.jobportal.entity.Certification;
import com.jobportal.entity.Education;
import com.jobportal.entity.Experience;
import com.jobportal.entity.Profile;
import com.jobportal.exception.JobPortalException;
import com.jobportal.repository.ProfileRepository;
import com.jobportal.service.ProfileService;

@Service
public class ProfileServiceImpl implements ProfileService {

    @Autowired
    private ProfileRepository profileRepository;
    
    @Autowired
    private ModelMapper mapper;

	@Override
	public ProfileDTO getProfile(Long id) throws JobPortalException {
		
		Profile p = profileRepository.findById(id).orElseThrow(()-> new JobPortalException("Profile Not Found"));
		
		
		return mapper.map(p, ProfileDTO.class);
	}

	@Override
	public ProfileDTO getProfileByEmail(String email) throws JobPortalException {
		
		Profile profile = profileRepository.findByEmail(email)
				          .orElseThrow(()-> new RuntimeException("Email does not exits"));
		
		return mapper.map(profile, ProfileDTO.class);
	}

	@Override
	public ProfileDTO updateProfile(ProfileDTO profileDTO) throws JobPortalException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void deleteProfile(Long profileId) throws JobPortalException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean existsById(Long profileId) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public List<ProfileDTO> getAllProfiles() throws JobPortalException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ProfileDTO upDateProfile(ProfileDTO profileDTO) throws JobPortalException {

	    Profile profile = profileRepository.findById(profileDTO.getId())
	            .orElseThrow(() -> new JobPortalException("Profile not found"));

	    // Basic Details
	    profile.setEmail(profileDTO.getEmail());
	    profile.setJobTitle(profileDTO.getJobTitle());
	    profile.setCompany(profileDTO.getCompany());
	    profile.setLocation(profileDTO.getLocation());
	    profile.setAbout(profileDTO.getAbout());
	    profile.setSkills(profileDTO.getSkills());

	    // Clear old child data
	    profile.getExperiences().clear();
	    profile.getEducations().clear();
	    profile.getCertifications().clear();

	    // Add Experiences
	    if (profileDTO.getExperiences() != null) {

	        for (Experience experience : profileDTO.getExperiences()) {

	            experience.setProfile(profile);
	            profile.getExperiences().add(experience);
	        }
	    }

	    // Add Educations
	    if (profileDTO.getEducations() != null) {

	        for (Education education : profileDTO.getEducations()) {

	            education.setProfile(profile);
	            profile.getEducations().add(education);
	        }
	    }

	    // Add Certifications
	    if (profileDTO.getCertifications() != null) {

	        for (Certification certification : profileDTO.getCertifications()) {

	            certification.setProfile(profile);
	            profile.getCertifications().add(certification);
	        }
	    }

	    Profile savedProfile = profileRepository.save(profile);

	    return mapper.map(savedProfile, ProfileDTO.class);
	}
    
    
     
   
}
