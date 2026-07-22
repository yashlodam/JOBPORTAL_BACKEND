package com.jobportal.dto;

import java.time.LocalDateTime;

public class Education {

	private String degree;
	private String collegeName;
	private String university;
	private LocalDateTime startYear;
	private LocalDateTime endYear;
	private String location;
	public String getDegree() {
		return degree;
	}
	public void setDegree(String degree) {
		this.degree = degree;
	}
	public String getCollegeName() {
		return collegeName;
	}
	public void setCollegeName(String collegeName) {
		this.collegeName = collegeName;
	}
	public String getUniversity() {
		return university;
	}
	public void setUniversity(String university) {
		this.university = university;
	}
	public LocalDateTime getStartYear() {
		return startYear;
	}
	public void setStartYear(LocalDateTime startYear) {
		this.startYear = startYear;
	}
	public LocalDateTime getEndYear() {
		return endYear;
	}
	public void setEndYear(LocalDateTime endYear) {
		this.endYear = endYear;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public Education(String degree, String collegeName, String university, LocalDateTime startYear,
			LocalDateTime endYear, String location) {
		super();
		this.degree = degree;
		this.collegeName = collegeName;
		this.university = university;
		this.startYear = startYear;
		this.endYear = endYear;
		this.location = location;
	}
	
	
	public Education() {
		
	}
	
	
}
