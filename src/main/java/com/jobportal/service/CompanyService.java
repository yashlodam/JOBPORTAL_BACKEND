package com.jobportal.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.jobportal.dto.CompanyRequestDTO;
import com.jobportal.dto.CompanyResponseDTO;
import com.jobportal.dto.JobResponseDTO;
import com.jobportal.exception.JobPortalException;

public interface CompanyService {


	CompanyResponseDTO createCompany(CompanyRequestDTO dto, String email)
	        throws JobPortalException;

	CompanyResponseDTO getMyCompany(String email)
	        throws JobPortalException;

	CompanyResponseDTO updateCompany(CompanyRequestDTO dto, String email)
	        throws JobPortalException;

	void deleteCompany(String email)
	        throws JobPortalException;

	CompanyResponseDTO getCompanyById(Long companyId)
	        throws JobPortalException;

	List<CompanyResponseDTO> getAllCompanies();

	List<CompanyResponseDTO> searchCompanies(String keyword);

	CompanyResponseDTO uploadCoverImage(MultipartFile file, String email);

	
	CompanyResponseDTO uploadLogo(MultipartFile file, String email);

	List<JobResponseDTO> getMyCompanyJobs(String email) throws JobPortalException;
}
