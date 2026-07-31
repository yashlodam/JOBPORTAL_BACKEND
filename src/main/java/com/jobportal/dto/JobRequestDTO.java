package com.jobportal.dto;

import java.time.LocalDate;
import java.util.List;

import com.jobportal.domain.ExperienceLevel;
import com.jobportal.domain.JobType;
import com.jobportal.domain.WorkingMode;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class JobRequestDTO {

    @NotBlank(message = "Job title is required")
    @Size(max = 150)
    private String jobTitle;

    @NotBlank(message = "Category is required")
    private String category;

    @NotBlank(message = "Description is required")
    @Size(max = 5000)
    private String description;

    @Size(max = 3000)
    private String responsibilities;

    @Size(max = 3000)
    private String requirements;

    @Size(max = 3000)
    private String aboutRole;

    @Size(max = 3000)
    private String benefits;

    @NotBlank
    private String city;

    @NotBlank
    private String state;

    @NotBlank
    private String country;

    @NotNull
    private WorkingMode workingMode;

    @NotNull
    private JobType jobType;

    @NotNull
    private ExperienceLevel experienceLevel;

    @Min(0)
    private Integer minimumExperience;

    @Min(0)
    private Integer maximumExperience;

    @Min(0)
    private Long minimumSalary;

    @Min(0)
    private Long maximumSalary;

    @NotBlank
    private String currency;

    @Min(1)
    private Integer vacancies;

    @NotNull
    private List<String> skillsRequired;

    private List<String> preferredSkills;

    private String qualification;

    private LocalDate applicationDeadline;

    @Min(1)
    private Integer numberOfInterviewRounds;

    private Boolean featured = false;

    private Boolean urgentHiring = false;

    private Boolean easyApply = true;

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

	public JobRequestDTO(@NotBlank(message = "Job title is required") @Size(max = 150) String jobTitle,
			@NotBlank(message = "Category is required") String category,
			@NotBlank(message = "Description is required") @Size(max = 5000) String description,
			@Size(max = 3000) String responsibilities, @Size(max = 3000) String requirements,
			@Size(max = 3000) String aboutRole, @Size(max = 3000) String benefits, @NotBlank String city,
			@NotBlank String state, @NotBlank String country, @NotNull WorkingMode workingMode,
			@NotNull JobType jobType, @NotNull ExperienceLevel experienceLevel, @Min(0) Integer minimumExperience,
			@Min(0) Integer maximumExperience, @Min(0) Long minimumSalary, @Min(0) Long maximumSalary,
			@NotBlank String currency, @Min(1) Integer vacancies, @NotNull List<String> skillsRequired,
			List<String> preferredSkills, String qualification, LocalDate applicationDeadline,
			@Min(1) Integer numberOfInterviewRounds, Boolean featured, Boolean urgentHiring, Boolean easyApply) {
		super();
		this.jobTitle = jobTitle;
		this.category = category;
		this.description = description;
		this.responsibilities = responsibilities;
		this.requirements = requirements;
		this.aboutRole = aboutRole;
		this.benefits = benefits;
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
		this.featured = featured;
		this.urgentHiring = urgentHiring;
		this.easyApply = easyApply;
	}

    // Getters, Setters, Constructors
    
    
    
    
    
    
}