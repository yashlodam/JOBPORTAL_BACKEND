package com.jobportal.serviceImpl;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.jobportal.dto.AccountType;
import com.jobportal.dto.CompanyRequestDTO;
import com.jobportal.dto.CompanyResponseDTO;
import com.jobportal.dto.JobResponseDTO;
import com.jobportal.entity.Company;
import com.jobportal.entity.Job;
import com.jobportal.entity.Recruiter;
import com.jobportal.entity.User;
import com.jobportal.exception.JobPortalException;
import com.jobportal.repository.CompanyRepository;
import com.jobportal.repository.JobRepository;
import com.jobportal.repository.RecuriterRepository;
import com.jobportal.repository.UserRepository;
import com.jobportal.service.CompanyService;

@Service
public class CompanyServiceImpl implements CompanyService {
	
	@Autowired
	private ModelMapper mapper;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private RecuriterRepository recruiterRepository;
	
	
	@Autowired
	private CompanyRepository companyRepository;
	
	@Autowired
	private JobRepository jobRepository;

	@Override
	public CompanyResponseDTO createCompany(CompanyRequestDTO companyRequestDTO, String email)
	        throws JobPortalException {

	    // Find logged-in user
	    User user = userRepository.findByEmail(email)
	            .orElseThrow(() ->
	                    new JobPortalException("User not found"));

	    // Check account type
	    if (user.getAccountType() != AccountType.EMPLOYER) {
	        throw new JobPortalException("Only employers can create a company.");
	    }

	    // Find recruiter
	    Recruiter recruiter = recruiterRepository.findByUser(user)
	            .orElseThrow(() ->
	                    new JobPortalException("Recruiter profile not found"));

	    // Check if recruiter already has a company
	    if (recruiter.getCompany() != null) {
	        throw new JobPortalException("Recruiter already belongs to a company.");
	    }

	    // Create company
	    Company company = new Company();
	    company.setCompanyName(companyRequestDTO.getCompanyName());
	    company.setWebsite(companyRequestDTO.getWebsite());
	    company.setLogo(companyRequestDTO.getLogo());
	    company.setIndustry(companyRequestDTO.getIndustry());
	    company.setCompanySize(companyRequestDTO.getCompanySize());
	    company.setHeadquarters(companyRequestDTO.getHeadquarters());
	    company.setFoundedYear(companyRequestDTO.getFoundedYear());
	    company.setEmail(companyRequestDTO.getEmail());
	    company.setPhone(companyRequestDTO.getPhone());
	    company.setDescription(companyRequestDTO.getDescription());
	    company.setMission(companyRequestDTO.getMission());
	    company.setBenefits(companyRequestDTO.getBenefits());

	    // Save company
	    Company savedCompany = companyRepository.save(company);

	    // Link recruiter to company
	    recruiter.setCompany(savedCompany);
	    recruiterRepository.save(recruiter);

	    // Prepare response
	    CompanyResponseDTO response = new CompanyResponseDTO();
	    response.setId(savedCompany.getId());
	    response.setCompanyName(savedCompany.getCompanyName());
	    response.setWebsite(savedCompany.getWebsite());
	    response.setLogo(savedCompany.getLogo());
	    response.setIndustry(savedCompany.getIndustry());
	    response.setCompanySize(savedCompany.getCompanySize());
	    response.setHeadquarters(savedCompany.getHeadquarters());
	    response.setFoundedYear(savedCompany.getFoundedYear());
	    response.setEmail(savedCompany.getEmail());
	    response.setPhone(savedCompany.getPhone());
	    response.setDescription(savedCompany.getDescription());
	    response.setMission(savedCompany.getMission());
	    response.setBenefits(savedCompany.getBenefits());
	    response.setCreatedOn(savedCompany.getCreatedOn());
	    response.setUpdatedOn(savedCompany.getUpdatedOn());

	    return response;
	}

	@Override
	public CompanyResponseDTO getMyCompany(String email) throws JobPortalException {

	    User user = userRepository.findByEmail(email)
	            .orElseThrow(() -> new JobPortalException("User not found"));

	    Recruiter recruiter = recruiterRepository.findByUser(user)
	            .orElseThrow(() -> new JobPortalException("Recruiter not found"));

	    Company company = recruiter.getCompany();

	    if (company == null) {
	        throw new JobPortalException("No company assigned to recruiter.");
	    }

	    return mapper.map(company, CompanyResponseDTO.class);
	}

	@Override
	public CompanyResponseDTO updateCompany(CompanyRequestDTO dto, String email) throws JobPortalException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void deleteCompany(String email) throws JobPortalException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public CompanyResponseDTO getCompanyById(Long companyId) throws JobPortalException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<CompanyResponseDTO> getAllCompanies() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<CompanyResponseDTO> searchCompanies(String keyword) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CompanyResponseDTO uploadLogo(MultipartFile file, String email) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public CompanyResponseDTO uploadCoverImage(MultipartFile file, String email) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<JobResponseDTO> getMyCompanyJobs(String email) throws JobPortalException {

	    User user = userRepository.findByEmail(email)
	            .orElseThrow(() -> new JobPortalException("User not found"));

	    Recruiter recruiter = recruiterRepository.findByUser(user)
	            .orElseThrow(() -> new JobPortalException("Recruiter profile not found"));

	    Company company = recruiter.getCompany();

	    if (company == null) {
	        throw new JobPortalException("Recruiter is not associated with any company.");
	    }

	    List<Job> jobs = jobRepository.findByCompany(company);

	    return jobs.stream()
	            .map(job -> mapper.map(job, JobResponseDTO.class))
	            .toList();
	}
}
