package com.jobportal.entity;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.jobportal.dto.Availability;
import com.jobportal.dto.ExperienceLevel;

import jakarta.persistence.CascadeType;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "profiles")
public class Profile {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	// User
	private String email;

	// Header
	private String name;
	private String jobTitle;          // Current Role
	private String company;
	private String location;
	 @Enumerated(EnumType.STRING)
	    private Availability availability;

	    @Enumerated(EnumType.STRING)
	    private ExperienceLevel experienceLevel;
	private String about;

	// Images
	private String profileImage;
	private String bannerImage;

	// Social Links
	private String linkedinUrl;
	private String githubUrl;
	private String portfolioUrl;

	// Skills
	@ElementCollection
	private List<String> skills = new ArrayList<>();

	// Languages
	@ElementCollection
	private List<String> languages = new ArrayList<>();

	// Experience
	@OneToMany(
	    mappedBy = "profile",
	    cascade = CascadeType.ALL,
	    orphanRemoval = true,
	    fetch = FetchType.LAZY
	)
	@JsonManagedReference
	private List<Experience> experiences = new ArrayList<>();

	// Education
	@OneToMany(
	    mappedBy = "profile",
	    cascade = CascadeType.ALL,
	    orphanRemoval = true,
	    fetch = FetchType.LAZY
	)
	@JsonManagedReference
	private List<Education> educations = new ArrayList<>();

	// Certifications
	@OneToMany(
	    mappedBy = "profile",
	    cascade = CascadeType.ALL,
	    orphanRemoval = true,
	    fetch = FetchType.LAZY
	)
	@JsonManagedReference
	private List<Certification> certifications = new ArrayList<>();

	

	@OneToOne(mappedBy = "profile")
	@JsonIgnore
	private User user;
    
    
    
    public Profile(Long id, String email, String name, String jobTitle, String company, String location,
			Availability availability, ExperienceLevel experienceLevel, String about, String profileImage,
			String bannerImage, String linkedinUrl, String githubUrl, String portfolioUrl, List<String> skills,
			List<String> languages, List<Experience> experiences, List<Education> educations,
			List<Certification> certifications, User user) {
		super();
		this.id = id;
		this.email = email;
		this.name = name;
		this.jobTitle = jobTitle;
		this.company = company;
		this.location = location;
		this.availability = availability;
		this.experienceLevel = experienceLevel;
		this.about = about;
		this.profileImage = profileImage;
		this.bannerImage = bannerImage;
		this.linkedinUrl = linkedinUrl;
		this.githubUrl = githubUrl;
		this.portfolioUrl = portfolioUrl;
		this.skills = skills;
		this.languages = languages;
		this.experiences = experiences;
		this.educations = educations;
		this.certifications = certifications;
		this.user = user;
	}

	public Availability getAvailability() {
		return availability;
	}

	public void setAvailability(Availability availability) {
		this.availability = availability;
	}

	public ExperienceLevel getExperienceLevel() {
		return experienceLevel;
	}

	public void setExperienceLevel(ExperienceLevel experienceLevel) {
		this.experienceLevel = experienceLevel;
	}

	public Profile() {
    	
    }

	// Helper Methods

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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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

	public String getLinkedinUrl() {
		return linkedinUrl;
	}

	public void setLinkedinUrl(String linkedinUrl) {
		this.linkedinUrl = linkedinUrl;
	}

	public String getGithubUrl() {
		return githubUrl;
	}

	public void setGithubUrl(String githubUrl) {
		this.githubUrl = githubUrl;
	}

	public String getPortfolioUrl() {
		return portfolioUrl;
	}

	public void setPortfolioUrl(String portfolioUrl) {
		this.portfolioUrl = portfolioUrl;
	}

	public List<String> getSkills() {
		return skills;
	}

	public void setSkills(List<String> skills) {
		this.skills = skills;
	}

	public List<String> getLanguages() {
		return languages;
	}

	public void setLanguages(List<String> languages) {
		this.languages = languages;
	}

	public List<Experience> getExperiences() {
		return experiences;
	}

	public void setExperiences(List<Experience> experiences) {
		this.experiences = experiences;
	}

	public List<Education> getEducations() {
		return educations;
	}

	public void setEducations(List<Education> educations) {
		this.educations = educations;
	}

	public List<Certification> getCertifications() {
		return certifications;
	}

	public void setCertifications(List<Certification> certifications) {
		this.certifications = certifications;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public void addExperience(Experience experience) {
        experiences.add(experience);
        experience.setProfile(this);
    }

    public void removeExperience(Experience experience) {
        experiences.remove(experience);
        experience.setProfile(null);
    }

    public void addEducation(Education education) {
        educations.add(education);
        education.setProfile(this);
    }

    public void removeEducation(Education education) {
        educations.remove(education);
        education.setProfile(null);
    }

    public void addCertification(Certification certification) {
        certifications.add(certification);
        certification.setProfile(this);
    }

    public void removeCertification(Certification certification) {
        certifications.remove(certification);
        certification.setProfile(null);
    }
}