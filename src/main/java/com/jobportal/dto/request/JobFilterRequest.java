package com.jobportal.dto.request;

import java.util.List;

import com.jobportal.domain.ExperienceLevel;
import com.jobportal.domain.JobType;
import com.jobportal.domain.WorkingMode;

public class JobFilterRequest {

    // ----------------------------------------------------
    // Keyword Search
    // ----------------------------------------------------

    private String keyword;

    // ----------------------------------------------------
    // Category
    // ----------------------------------------------------

    private String category;

    // ----------------------------------------------------
    // Location
    // ----------------------------------------------------

    private String city;
    private String state;
    private String country;

    // ----------------------------------------------------
    // Employment
    // ----------------------------------------------------

    private JobType jobType;
    private WorkingMode workingMode;
    private ExperienceLevel experienceLevel;

    // ----------------------------------------------------
    // Experience Range
    // ----------------------------------------------------

    private Integer minimumExperience;
    private Integer maximumExperience;

    // ----------------------------------------------------
    // Salary
    // ----------------------------------------------------

    private Long minimumSalary;
    private Long maximumSalary;

    // ----------------------------------------------------
    // Skills
    // ----------------------------------------------------

    private List<String> skills;

    // ----------------------------------------------------
    // Qualification
    // ----------------------------------------------------

    private String qualification;

    // ----------------------------------------------------
    // Metadata
    // ----------------------------------------------------

    private Boolean featured;
    private Boolean urgentHiring;
    private Boolean easyApply;

    public JobFilterRequest() {
    }

    // ----------------------------------------------------
    // Getters & Setters
    // ----------------------------------------------------

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
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

    public JobType getJobType() {
        return jobType;
    }

    public void setJobType(JobType jobType) {
        this.jobType = jobType;
    }

    public WorkingMode getWorkingMode() {
        return workingMode;
    }

    public void setWorkingMode(WorkingMode workingMode) {
        this.workingMode = workingMode;
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

    public List<String> getSkills() {
        return skills;
    }

    public void setSkills(List<String> skills) {
        this.skills = skills;
    }

    public String getQualification() {
        return qualification;
    }

    public void setQualification(String qualification) {
        this.qualification = qualification;
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
}