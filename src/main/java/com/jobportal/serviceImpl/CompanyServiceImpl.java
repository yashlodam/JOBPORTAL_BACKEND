package com.jobportal.serviceImpl;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.jobportal.domain.AccountType;
import com.jobportal.domain.JobStatus;
import com.jobportal.dto.CompanyRequestDTO;
import com.jobportal.dto.CompanyResponseDTO;
import com.jobportal.dto.response.JobSummaryResponse;
import com.jobportal.entity.Company;
import com.jobportal.entity.Recruiter;
import com.jobportal.entity.User;
import com.jobportal.event.CompanyProfileUpdatedEvent;
import com.jobportal.exception.JobPortalException;
import com.jobportal.mapper.JobMapper;
import com.jobportal.repository.CompanyRepository;
import com.jobportal.repository.JobRepository;
import com.jobportal.repository.RecruiterRepository;
import com.jobportal.repository.UserRepository;
import com.jobportal.service.CompanyService;

/**
 * Company service implementation.
 *
 * <h3>Notifications (event-driven)</h3>
 * <ul>
 *   <li>{@link CompanyProfileUpdatedEvent} with {@code created=true} — fired after
 *       a recruiter successfully creates their company profile. The listener sends
 *       a {@code COMPANY_UPDATE} notification to the recruiter confirming setup.</li>
 *   <li>{@link CompanyProfileUpdatedEvent} with {@code created=false} — fired after
 *       a recruiter updates their company profile. The listener sends a
 *       {@code COMPANY_UPDATE} notification to the recruiter.</li>
 * </ul>
 */
@Service
public class CompanyServiceImpl implements CompanyService {

    private final UserRepository           userRepository;
    private final RecruiterRepository      recruiterRepository;
    private final CompanyRepository        companyRepository;
    private final JobRepository            jobRepository;
    private final JobMapper                jobMapper;
    private final String                   uploadBaseDir;
    private final ApplicationEventPublisher eventPublisher;

    public CompanyServiceImpl(
            UserRepository userRepository,
            RecruiterRepository recruiterRepository,
            CompanyRepository companyRepository,
            JobRepository jobRepository,
            JobMapper jobMapper,
            @Value("${file.upload.base-dir}") String uploadBaseDir,
            ApplicationEventPublisher eventPublisher) {
        this.userRepository  = userRepository;
        this.recruiterRepository = recruiterRepository;
        this.companyRepository   = companyRepository;
        this.jobRepository       = jobRepository;
        this.jobMapper           = jobMapper;
        this.uploadBaseDir       = uploadBaseDir;
        this.eventPublisher      = eventPublisher;
    }

    @Override
    @Transactional
    public CompanyResponseDTO createCompany(CompanyRequestDTO dto, String email)
            throws JobPortalException {
        User user = findUserByEmail(email);

        if (user.getAccountType() != AccountType.EMPLOYER) {
            throw JobPortalException.forbidden("Only employers can create a company.");
        }

        Recruiter recruiter = findRecruiterByUser(user);

        if (recruiter.getCompany() != null) {
            throw JobPortalException.conflict("You already belong to a company.");
        }

        Company company = new Company();
        mapDtoToCompany(dto, company);

        Company savedCompany = companyRepository.save(company);

        recruiter.setCompany(savedCompany);
        recruiterRepository.save(recruiter);

        // ── Publish event: notify recruiter their company profile is live ─────
        eventPublisher.publishEvent(new CompanyProfileUpdatedEvent(
                this, user, savedCompany, true));

        return toCompanyResponse(savedCompany);
    }

    @Override
    @Transactional(readOnly = true)
    public CompanyResponseDTO getMyCompany(String email) throws JobPortalException {
        Recruiter recruiter = findRecruiterByUser(findUserByEmail(email));
        if (recruiter.getCompany() == null) {
            throw JobPortalException.notFound("No company found for your account.");
        }
        return toCompanyResponse(recruiter.getCompany());
    }

    @Override
    @Transactional
    public CompanyResponseDTO updateCompany(CompanyRequestDTO dto, String email)
            throws JobPortalException {
        User user = findUserByEmail(email);
        Recruiter recruiter = findRecruiterByUser(user);
        Company company = getRecruiterCompany(recruiter);

        mapDtoToCompany(dto, company);
        Company updated = companyRepository.save(company);

        // ── Publish event: notify recruiter about the company update ──────────
        eventPublisher.publishEvent(new CompanyProfileUpdatedEvent(
                this, user, updated, false));

        return toCompanyResponse(updated);
    }

    @Override
    @Transactional
    public void deleteCompany(String email) throws JobPortalException {
        User user = findUserByEmail(email);
        Recruiter recruiter = findRecruiterByUser(user);
        Company company = getRecruiterCompany(recruiter);

        recruiter.setCompany(null);
        recruiterRepository.save(recruiter);

        companyRepository.delete(company);
    }

    @Override
    @Transactional(readOnly = true)
    public CompanyResponseDTO getCompanyById(Long companyId) throws JobPortalException {
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> JobPortalException.notFound(
                        "Company not found with id: " + companyId));
        return toCompanyResponse(company);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CompanyResponseDTO> getAllCompanies(Pageable pageable) {
        return companyRepository.findAll(pageable)
                .map(this::toCompanyResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CompanyResponseDTO> searchCompanies(String keyword, Pageable pageable) {
        return companyRepository.searchCompanies(keyword, pageable)
                .map(this::toCompanyResponse);
    }

    @Override
    @Transactional
    public CompanyResponseDTO uploadLogo(MultipartFile file, String email) throws Exception {
        Recruiter recruiter = findRecruiterByUser(findUserByEmail(email));
        Company company = getRecruiterCompany(recruiter);

        String fileName = saveFile(file, "logo");
        company.setLogo(fileName);
        Company updated = companyRepository.save(company);
        return toCompanyResponse(updated);
    }

    @Override
    @Transactional
    public CompanyResponseDTO uploadCoverImage(MultipartFile file, String email) throws Exception {
        Recruiter recruiter = findRecruiterByUser(findUserByEmail(email));
        Company company = getRecruiterCompany(recruiter);

        String fileName = saveFile(file, "cover");
        company.setCoverImage(fileName);
        Company updated = companyRepository.save(company);
        return toCompanyResponse(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<JobSummaryResponse> getMyCompanyJobs(String email, Pageable pageable)
            throws JobPortalException {
        Recruiter recruiter = findRecruiterByUser(findUserByEmail(email));
        Company company = getRecruiterCompany(recruiter);
        return jobRepository.findByCompanyIdAndStatus(company.getId(), JobStatus.OPEN, pageable)
                .map(jobMapper::toSummary);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<JobSummaryResponse> getCompanyJobs(Long companyId, Pageable pageable)
            throws JobPortalException {
        companyRepository.findById(companyId)
                .orElseThrow(() -> JobPortalException.notFound("Company not found"));
        return jobRepository
                .findByCompanyIdAndStatus(companyId, JobStatus.OPEN, pageable)
                .map(jobMapper::toSummary);
    }

    // ── Private Helpers ─────────────────────────────────────────────────────

    private User findUserByEmail(String email) throws JobPortalException {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> JobPortalException.notFound("User not found"));
    }

    private Recruiter findRecruiterByUser(User user) throws JobPortalException {
        return recruiterRepository.findByUser(user)
                .orElseThrow(() -> JobPortalException.notFound("Recruiter profile not found"));
    }

    private Company getRecruiterCompany(Recruiter recruiter) throws JobPortalException {
        if (recruiter.getCompany() == null) {
            throw JobPortalException.notFound("No company found for your account.");
        }
        return recruiter.getCompany();
    }

    private void mapDtoToCompany(CompanyRequestDTO dto, Company company) {
        if (dto.getCompanyName() != null) company.setCompanyName(dto.getCompanyName());
        if (dto.getWebsite() != null)     company.setWebsite(dto.getWebsite());
        if (dto.getIndustry() != null)    company.setIndustry(dto.getIndustry());
        if (dto.getCompanySize() != null) company.setCompanySize(dto.getCompanySize());
        if (dto.getHeadquarters() != null) company.setHeadquarters(dto.getHeadquarters());
        if (dto.getFoundedYear() != null) company.setFoundedYear(dto.getFoundedYear());
        if (dto.getEmail() != null)       company.setEmail(dto.getEmail());
        if (dto.getPhone() != null)       company.setPhone(dto.getPhone());
        if (dto.getDescription() != null) company.setDescription(dto.getDescription());
        if (dto.getMission() != null)     company.setMission(dto.getMission());
        if (dto.getBenefits() != null)    company.setBenefits(dto.getBenefits());
    }

    private String saveFile(MultipartFile file, String subDir) throws Exception {
        String dir = uploadBaseDir + "/" + subDir + "/";
        File directory = new File(dir);
        if (!directory.exists()) {
            directory.mkdirs();
        }
        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        Path path = Paths.get(dir + fileName);
        Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
        return subDir + "/" + fileName;
    }

    private CompanyResponseDTO toCompanyResponse(Company c) {
        CompanyResponseDTO dto = new CompanyResponseDTO();
        dto.setId(c.getId());
        dto.setCompanyName(c.getCompanyName());
        dto.setWebsite(c.getWebsite());
        dto.setLogo(c.getLogo());
        dto.setIndustry(c.getIndustry());
        dto.setCompanySize(c.getCompanySize());
        dto.setHeadquarters(c.getHeadquarters());
        dto.setFoundedYear(c.getFoundedYear());
        dto.setEmail(c.getEmail());
        dto.setPhone(c.getPhone());
        dto.setDescription(c.getDescription());
        dto.setMission(c.getMission());
        dto.setBenefits(c.getBenefits());
        dto.setTotalJobs(jobRepository.countByCompanyIdAndStatus(c.getId(), JobStatus.OPEN));
        dto.setTotalRecruiters(recruiterRepository.countByCompanyId(c.getId()));
        dto.setCreatedOn(c.getCreatedAt());
        dto.setUpdatedOn(c.getUpdatedAt());
        return dto;
    }
}
