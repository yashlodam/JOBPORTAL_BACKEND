package com.jobportal.serviceImpl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jobportal.domain.AccountType;
import com.jobportal.domain.NotificationType;
import com.jobportal.dto.request.JobApplicationRequest;
import com.jobportal.dto.request.UpdateApplicationStatusRequest;
import com.jobportal.dto.response.JobApplicationResponse;
import com.jobportal.entity.Job;
import com.jobportal.entity.JobApplication;
import com.jobportal.entity.Recruiter;
import com.jobportal.entity.Resume;
import com.jobportal.entity.User;
import com.jobportal.exception.JobPortalException;
import com.jobportal.repository.JobApplicationRepository;
import com.jobportal.repository.JobRepository;
import com.jobportal.repository.RecruiterRepository;
import com.jobportal.repository.ResumeRepository;
import com.jobportal.repository.UserRepository;
import com.jobportal.service.JobApplicationService;
import com.jobportal.service.NotificationService;

@Service
public class JobApplicationServiceImpl implements JobApplicationService {

    private final JobApplicationRepository applicationRepository;
    private final JobRepository jobRepository;
    private final UserRepository userRepository;
    private final RecruiterRepository recruiterRepository;
    private final ResumeRepository resumeRepository;
    private final NotificationService notificationService;

    public JobApplicationServiceImpl(
            JobApplicationRepository applicationRepository,
            JobRepository jobRepository,
            UserRepository userRepository,
            RecruiterRepository recruiterRepository,
            ResumeRepository resumeRepository,
            NotificationService notificationService) {
        this.applicationRepository = applicationRepository;
        this.jobRepository = jobRepository;
        this.userRepository = userRepository;
        this.recruiterRepository = recruiterRepository;
        this.resumeRepository = resumeRepository;
        this.notificationService = notificationService;
    }

    @Override
    @Transactional
    public JobApplicationResponse applyToJob(Long jobId, JobApplicationRequest request, String email)
            throws JobPortalException {
        User applicant = findUserByEmail(email);

        if (applicant.getAccountType() != AccountType.APPLICANT) {
            throw JobPortalException.forbidden("Only applicants can apply for jobs.");
        }

        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> JobPortalException.notFound("Job not found with id: " + jobId));

        if (applicationRepository.existsByApplicantIdAndJobId(applicant.getId(), jobId)) {
            throw JobPortalException.conflict("You have already applied for this job.");
        }

        JobApplication application = new JobApplication();
        application.setJob(job);
        application.setApplicant(applicant);
        application.setCoverLetter(request.getCoverLetter());

        // Attach resume if provided
        if (request.getResumeId() != null) {
            Resume resume = resumeRepository.findById(request.getResumeId())
                    .orElseThrow(() -> JobPortalException.notFound("Resume not found"));
            if (!resume.getProfile().getUser().getId().equals(applicant.getId())) {
                throw JobPortalException.forbidden("This resume does not belong to you.");
            }
            application.setResume(resume);
        }

        // Increment applicant count
        job.setTotalApplicants(job.getTotalApplicants() + 1);
        jobRepository.save(job);

        JobApplication saved = applicationRepository.save(application);

        // ── Notify the recruiter who posted the job ──────────────────────────
        User recruiterUser = job.getRecruiter().getUser();
        notificationService.send(
                recruiterUser,
                NotificationType.APPLICATION_RECEIVED,
                "New Application Received",
                applicant.getName() + " has applied for \"" + job.getJobTitle() + "\".",
                saved.getId(),
                "APPLICATION"
        );

        return toResponse(saved);
    }

    @Override
    @Transactional
    public void withdrawApplication(Long applicationId, String email) throws JobPortalException {
        User applicant = findUserByEmail(email);
        JobApplication application = findApplicationById(applicationId);

        if (!application.getApplicant().getId().equals(applicant.getId())) {
            throw JobPortalException.forbidden("You are not authorized to withdraw this application.");
        }

        Job job = application.getJob();
        if (job.getTotalApplicants() > 0) {
            job.setTotalApplicants(job.getTotalApplicants() - 1);
            jobRepository.save(job);
        }

        applicationRepository.delete(application);

        // ── Notify the recruiter about the withdrawal ────────────────────────
        User recruiterUser = job.getRecruiter().getUser();
        notificationService.send(
                recruiterUser,
                NotificationType.APPLICATION_WITHDRAWN,
                "Application Withdrawn",
                applicant.getName() + " has withdrawn their application for \""
                        + job.getJobTitle() + "\".",
                applicationId,
                "APPLICATION"
        );
    }

    @Override
    @Transactional(readOnly = true)
    public Page<JobApplicationResponse> getMyApplications(String email, Pageable pageable)
            throws JobPortalException {
        User applicant = findUserByEmail(email);
        return applicationRepository.findByApplicantId(applicant.getId(), pageable)
                .map(this::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<JobApplicationResponse> getJobApplications(Long jobId, String email,
            Pageable pageable) throws JobPortalException {
        User user = findUserByEmail(email);
        Recruiter recruiter = recruiterRepository.findByUser(user)
                .orElseThrow(() -> JobPortalException.forbidden(
                        "Only recruiters can view job applications."));

        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> JobPortalException.notFound("Job not found"));

        if (!job.getRecruiter().getId().equals(recruiter.getId())) {
            throw JobPortalException.forbidden("You can only view applications for your own jobs.");
        }

        return applicationRepository.findByJobId(jobId, pageable).map(this::toResponse);
    }

    @Override
    @Transactional
    public JobApplicationResponse updateApplicationStatus(Long applicationId,
            UpdateApplicationStatusRequest request, String email) throws JobPortalException {
        User user = findUserByEmail(email);
        Recruiter recruiter = recruiterRepository.findByUser(user)
                .orElseThrow(() -> JobPortalException.forbidden(
                        "Only recruiters can update application status."));

        JobApplication application = findApplicationById(applicationId);

        if (!application.getJob().getRecruiter().getId().equals(recruiter.getId())) {
            throw JobPortalException.forbidden(
                    "You are not authorized to update this application.");
        }

        application.setStatus(request.getStatus());
        JobApplication updated = applicationRepository.save(application);

        // ── Notify the applicant about the status change ─────────────────────
        User applicant = application.getApplicant();
        String jobTitle = application.getJob().getJobTitle();
        String statusLabel = request.getStatus().name();

        notificationService.send(
                applicant,
                NotificationType.APPLICATION_STATUS_UPDATED,
                "Application Status Updated",
                "Your application for \"" + jobTitle + "\" has been updated to: "
                        + formatStatus(statusLabel) + ".",
                applicationId,
                "APPLICATION"
        );

        return toResponse(updated);
    }

    // ── Private Helpers ─────────────────────────────────────────────────────

    private User findUserByEmail(String email) throws JobPortalException {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> JobPortalException.notFound("User not found"));
    }

    private JobApplication findApplicationById(Long id) throws JobPortalException {
        return applicationRepository.findById(id)
                .orElseThrow(() -> JobPortalException.notFound(
                        "Application not found with id: " + id));
    }

    /**
     * Converts enum constant to a human-readable label.
     * E.g. "APPLICATION_STATUS_UPDATED" → "Application Status Updated"
     */
    private String formatStatus(String status) {
        return status.charAt(0)
                + status.substring(1).toLowerCase().replace('_', ' ');
    }

    private JobApplicationResponse toResponse(JobApplication app) {
        JobApplicationResponse dto = new JobApplicationResponse();
        dto.setId(app.getId());
        dto.setStatus(app.getStatus());
        dto.setCoverLetter(app.getCoverLetter());
        dto.setAppliedAt(app.getCreatedAt());
        dto.setUpdatedAt(app.getUpdatedAt());

        if (app.getJob() != null) {
            dto.setJobId(app.getJob().getId());
            dto.setJobTitle(app.getJob().getJobTitle());
            if (app.getJob().getCompany() != null) {
                dto.setCompanyName(app.getJob().getCompany().getCompanyName());
                dto.setCompanyLogo(app.getJob().getCompany().getLogo());
            }
        }
        if (app.getApplicant() != null) {
            dto.setApplicantId(app.getApplicant().getId());
            dto.setApplicantName(app.getApplicant().getName());
            dto.setApplicantEmail(app.getApplicant().getEmail());
        }
        if (app.getResume() != null) {
            dto.setResumeUrl(app.getResume().getResumeUrl());
        }
        return dto;
    }
}
