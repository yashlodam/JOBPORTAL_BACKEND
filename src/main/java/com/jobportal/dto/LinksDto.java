package com.jobportal.dto;

public class LinksDto {

    private String linkedinUrl;

    private String githubUrl;

    private String portfolioUrl;
    
    
    public LinksDto() {
    	
    }

	public LinksDto(String linkedinUrl, String githubUrl, String portfolioUrl) {
		super();
		this.linkedinUrl = linkedinUrl;
		this.githubUrl = githubUrl;
		this.portfolioUrl = portfolioUrl;
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
    
    

}
