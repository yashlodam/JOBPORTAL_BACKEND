package com.jobportal.dto;

import java.util.List;

import com.jobportal.entity.Certification;
import com.jobportal.entity.Education;
import com.jobportal.entity.Experience;


public class ProfileDTO {

	private Long id;
	private String email;
	private String jobTitle;
	private String company;
	private String location;
	private String about;
	private String profileImage;
	private String bannerImage;
	private List<String> skills;
	private List<Experience> experiences;
	private List<Certification> certifications;
	private List<Education> educations;
	public String getEmail() {
		return email;
		
	}
	
	public String getProfileImage() {
		return profileImage;
	}

	public void setProfileImage(String profileImage) {
		this.profileImage = profileImage;
	}

	public String getBannerImage() {
		return bannerImage;
	}

	public void setBannerImage(String bannerImage) {
		this.bannerImage = bannerImage;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setEmail(String email) {
		this.email = email;
	}
	public String getJobTitle() {
		return jobTitle;
	}
	public void setJobTitle(String jobTitle) {
		this.jobTitle = jobTitle;
	}
	public String getCompany() {
		return company;
	}
	public void setCompany(String company) {
		this.company = company;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public String getAbout() {
		return about;
	}
	public void setAbout(String about) {
		this.about = about;
	}
	public List<String> getSkills() {
		return skills;
	}
	public void setSkills(List<String> skills) {
		this.skills = skills;
	}
	public List<Experience> getExperiences() {
		return experiences;
	}
	public void setExperiences(List<Experience> experiences) {
		this.experiences = experiences;
	}
	public List<Certification> getCertifications() {
		return certifications;
	}
	public void setCertifications(List<Certification> certifications) {
		this.certifications = certifications;
	}
	public List<Education> getEducations() {
		return educations;
	}
	public void setEducations(List<Education> educations) {
		this.educations = educations;
	}
		
	

	public ProfileDTO(Long id, String email, String jobTitle, String company, String location, String about,
			String profileImage, String bannerImage, List<String> skills, List<Experience> experiences,
			List<Certification> certifications, List<Education> educations) {
		super();
		this.id = id;
		this.email = email;
		this.jobTitle = jobTitle;
		this.company = company;
		this.location = location;
		this.about = about;
		this.profileImage = profileImage;
		this.bannerImage = bannerImage;
		this.skills = skills;
		this.experiences = experiences;
		this.certifications = certifications;
		this.educations = educations;
	}

	public ProfileDTO() {
		
	}
	
	
}
