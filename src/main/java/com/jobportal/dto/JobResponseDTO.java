package com.jobportal.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.jobportal.domain.ExperienceLevel;
import com.jobportal.domain.JobStatus;
import com.jobportal.domain.JobType;
import com.jobportal.domain.WorkingMode;

public class JobResponseDTO {

    private Long id;

    private String jobTitle;

    private String category;

    private String description;

    private String responsibilities;

    private String requirements;

    private String aboutRole;

    private String benefits;

    // Company Details
    private Long companyId;

    private String companyName;

    private String companyLogo;

    // Recruiter Details
    private Long recruiterId;

    private String recruiterName;

    // Location
    private String city;

    private String state;

    private String country;

    // Employment
    private WorkingMode workingMode;

    private JobType jobType;

    private ExperienceLevel experienceLevel;

    private Integer minimumExperience;

    private Integer maximumExperience;

    // Salary
    private Long minimumSalary;

    private Long maximumSalary;

    private String currency;

    // Vacancy
    private Integer vacancies;

    // Skills
    private List<String> skillsRequired;

    private List<String> preferredSkills;

    // Education
    private String qualification;

    // Hiring
    private LocalDate applicationDeadline;

    private Integer numberOfInterviewRounds;

    private JobStatus status;

    // Analytics
    private Integer totalApplicants;

    private Integer totalViews;

    private Integer totalBookmarks;

    // Flags
    private Boolean featured;

    private Boolean urgentHiring;

    private Boolean easyApply;

    // Dates
    private LocalDateTime postedOn;

    private LocalDateTime updatedOn;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getJobTitle() {
		return jobTitle;
	}

	public void setJobTitle(String jobTitle) {
		this.jobTitle = jobTitle;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getResponsibilities() {
		return responsibilities;
	}

	public void setResponsibilities(String responsibilities) {
		this.responsibilities = responsibilities;
	}

	public String getRequirements() {
		return requirements;
	}

	public void setRequirements(String requirements) {
		this.requirements = requirements;
	}

	public String getAboutRole() {
		return aboutRole;
	}

	public void setAboutRole(String aboutRole) {
		this.aboutRole = aboutRole;
	}

	public String getBenefits() {
		return benefits;
	}

	public void setBenefits(String benefits) {
		this.benefits = benefits;
	}

	public Long getCompanyId() {
		return companyId;
	}

	public void setCompanyId(Long companyId) {
		this.companyId = companyId;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public String getCompanyLogo() {
		return companyLogo;
	}

	public void setCompanyLogo(String companyLogo) {
		this.companyLogo = companyLogo;
	}

	public Long getRecruiterId() {
		return recruiterId;
	}

	public void setRecruiterId(Long recruiterId) {
		this.recruiterId = recruiterId;
	}

	public String getRecruiterName() {
		return recruiterName;
	}

	public void setRecruiterName(String recruiterName) {
		this.recruiterName = recruiterName;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public WorkingMode getWorkingMode() {
		return workingMode;
	}

	public void setWorkingMode(WorkingMode workingMode) {
		this.workingMode = workingMode;
	}

	public JobType getJobType() {
		return jobType;
	}

	public void setJobType(JobType jobType) {
		this.jobType = jobType;
	}

	public ExperienceLevel getExperienceLevel() {
		return experienceLevel;
	}

	public void setExperienceLevel(ExperienceLevel experienceLevel) {
		this.experienceLevel = experienceLevel;
	}

	public Integer getMinimumExperience() {
		return minimumExperience;
	}

	public void setMinimumExperience(Integer minimumExperience) {
		this.minimumExperience = minimumExperience;
	}

	public Integer getMaximumExperience() {
		return maximumExperience;
	}

	public void setMaximumExperience(Integer maximumExperience) {
		this.maximumExperience = maximumExperience;
	}

	public Long getMinimumSalary() {
		return minimumSalary;
	}

	public void setMinimumSalary(Long minimumSalary) {
		this.minimumSalary = minimumSalary;
	}

	public Long getMaximumSalary() {
		return maximumSalary;
	}

	public void setMaximumSalary(Long maximumSalary) {
		this.maximumSalary = maximumSalary;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public Integer getVacancies() {
		return vacancies;
	}

	public void setVacancies(Integer vacancies) {
		this.vacancies = vacancies;
	}

	public List<String> getSkillsRequired() {
		return skillsRequired;
	}

	public void setSkillsRequired(List<String> skillsRequired) {
		this.skillsRequired = skillsRequired;
	}

	public List<String> getPreferredSkills() {
		return preferredSkills;
	}

	public void setPreferredSkills(List<String> preferredSkills) {
		this.preferredSkills = preferredSkills;
	}

	public String getQualification() {
		return qualification;
	}

	public void setQualification(String qualification) {
		this.qualification = qualification;
	}

	public LocalDate getApplicationDeadline() {
		return applicationDeadline;
	}

	public void setApplicationDeadline(LocalDate applicationDeadline) {
		this.applicationDeadline = applicationDeadline;
	}

	public Integer getNumberOfInterviewRounds() {
		return numberOfInterviewRounds;
	}

	public void setNumberOfInterviewRounds(Integer numberOfInterviewRounds) {
		this.numberOfInterviewRounds = numberOfInterviewRounds;
	}

	public JobStatus getStatus() {
		return status;
	}

	public void setStatus(JobStatus status) {
		this.status = status;
	}

	public Integer getTotalApplicants() {
		return totalApplicants;
	}

	public void setTotalApplicants(Integer totalApplicants) {
		this.totalApplicants = totalApplicants;
	}

	public Integer getTotalViews() {
		return totalViews;
	}

	public void setTotalViews(Integer totalViews) {
		this.totalViews = totalViews;
	}

	public Integer getTotalBookmarks() {
		return totalBookmarks;
	}

	public void setTotalBookmarks(Integer totalBookmarks) {
		this.totalBookmarks = totalBookmarks;
	}

	public Boolean getFeatured() {
		return featured;
	}

	public void setFeatured(Boolean featured) {
		this.featured = featured;
	}

	public Boolean getUrgentHiring() {
		return urgentHiring;
	}

	public void setUrgentHiring(Boolean urgentHiring) {
		this.urgentHiring = urgentHiring;
	}

	public Boolean getEasyApply() {
		return easyApply;
	}

	public void setEasyApply(Boolean easyApply) {
		this.easyApply = easyApply;
	}

	public LocalDateTime getPostedOn() {
		return postedOn;
	}

	public void setPostedOn(LocalDateTime postedOn) {
		this.postedOn = postedOn;
	}

	public LocalDateTime getUpdatedOn() {
		return updatedOn;
	}

	public void setUpdatedOn(LocalDateTime updatedOn) {
		this.updatedOn = updatedOn;
	}

	public JobResponseDTO(Long id, String jobTitle, String category, String description, String responsibilities,
			String requirements, String aboutRole, String benefits, Long companyId, String companyName,
			String companyLogo, Long recruiterId, String recruiterName, String city, String state, String country,
			WorkingMode workingMode, JobType jobType, ExperienceLevel experienceLevel, Integer minimumExperience,
			Integer maximumExperience, Long minimumSalary, Long maximumSalary, String currency, Integer vacancies,
			List<String> skillsRequired, List<String> preferredSkills, String qualification,
			LocalDate applicationDeadline, Integer numberOfInterviewRounds, JobStatus status, Integer totalApplicants,
			Integer totalViews, Integer totalBookmarks, Boolean featured, Boolean urgentHiring, Boolean easyApply,
			LocalDateTime postedOn, LocalDateTime updatedOn) {
		super();
		this.id = id;
		this.jobTitle = jobTitle;
		this.category = category;
		this.description = description;
		this.responsibilities = responsibilities;
		this.requirements = requirements;
		this.aboutRole = aboutRole;
		this.benefits = benefits;
		this.companyId = companyId;
		this.companyName = companyName;
		this.companyLogo = companyLogo;
		this.recruiterId = recruiterId;
		this.recruiterName = recruiterName;
		this.city = city;
		this.state = state;
		this.country = country;
		this.workingMode = workingMode;
		this.jobType = jobType;
		this.experienceLevel = experienceLevel;
		this.minimumExperience = minimumExperience;
		this.maximumExperience = maximumExperience;
		this.minimumSalary = minimumSalary;
		this.maximumSalary = maximumSalary;
		this.currency = currency;
		this.vacancies = vacancies;
		this.skillsRequired = skillsRequired;
		this.preferredSkills = preferredSkills;
		this.qualification = qualification;
		this.applicationDeadline = applicationDeadline;
		this.numberOfInterviewRounds = numberOfInterviewRounds;
		this.status = status;
		this.totalApplicants = totalApplicants;
		this.totalViews = totalViews;
		this.totalBookmarks = totalBookmarks;
		this.featured = featured;
		this.urgentHiring = urgentHiring;
		this.easyApply = easyApply;
		this.postedOn = postedOn;
		this.updatedOn = updatedOn;
	}

	public JobResponseDTO() {
	}
    // Getters, Setters, Constructors
    
    
}