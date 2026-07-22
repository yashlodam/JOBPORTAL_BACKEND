package com.jobportal.entity;

import java.util.List;

import com.jobportal.dto.Certification;
import com.jobportal.dto.Education;
import com.jobportal.dto.Experience;
import com.jobportal.dto.ProfileDTO;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "profiles")
public class Profile {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private String email;
	private String jobTitle;
	private String company;
	private String location;
	private String about;
	private List<String> skills;
	private List<Experience> experiences;
	private List<Certification> certifications;
	private List<Education> educations;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getEmail() {
		return email;
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
	public Profile( String email, String jobTitle, String company, String location, String about,
			List<String> skills, List<Experience> experiences, List<Certification> certifications,
			List<Education> educations) {
		super();
		
		this.email = email;
		this.jobTitle = jobTitle;
		this.company = company;
		this.location = location;
		this.about = about;
		this.skills = skills;
		this.experiences = experiences;
		this.certifications = certifications;
		this.educations = educations;
	}
	
	public Profile() {
		
	}
	
	public ProfileDTO toDTO() {
		return new ProfileDTO(this.email,this.jobTitle,this.company,this.location,this.about,this.skills,this.experiences,this.certifications,this.educations);
	}
	
	
	
}
