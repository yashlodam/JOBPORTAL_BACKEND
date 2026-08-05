package com.jobportal.serviceImpl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.jobportal.dto.request.ResumeUpdateRequest;
import com.jobportal.dto.response.ResumeResponse;
import com.jobportal.entity.Profile;
import com.jobportal.entity.Resume;
import com.jobportal.exception.JobPortalException;
import com.jobportal.repository.ProfileRepository;
import com.jobportal.repository.ResumeRepository;
import com.jobportal.service.ResumeService;
import com.jobportal.utility.FileStorageService;

/**
 * Service implementation for managing applicant resumes.
 * Supports multi-resume uploads, default resume toggling, and metadata management.
 */
@Service
public class ResumeServiceImpl implements ResumeService {

    private final ResumeRepository resumeRepository;
    private final ProfileRepository profileRepository;
    private final FileStorageService fileStorageService;

    public ResumeServiceImpl(
            ResumeRepository resumeRepository,
            ProfileRepository profileRepository,
            FileStorageService fileStorageService) {
        this.resumeRepository = resumeRepository;
        this.profileRepository = profileRepository;
        this.fileStorageService = fileStorageService;
    }

    @Override
    @Transactional
    public ResumeResponse uploadResume(MultipartFile file, String resumeName, Boolean isDefault, String email)
            throws Exception {
        if (file == null || file.isEmpty()) {
            throw JobPortalException.badRequest("Please select a valid file to upload.");
        }

        Profile profile = findProfileByEmail(email);

        long existingCount = resumeRepository.countByProfileId(profile.getId());
        boolean shouldBeDefault = Boolean.TRUE.equals(isDefault) || existingCount == 0;

        if (shouldBeDefault && existingCount > 0) {
            resumeRepository.unsetDefaultResumesForProfile(profile.getId());
        }

        String path = fileStorageService.store(file, "resume");

        String name = (resumeName != null && !resumeName.isBlank())
                ? resumeName.trim()
                : file.getOriginalFilename();

        Resume resume = new Resume();
        resume.setProfile(profile);
        resume.setResumeName(name);
        resume.setFileName(file.getOriginalFilename());
        resume.setResumeUrl(path);
        resume.setFileSizeBytes(file.getSize());
        resume.setContentType(file.getContentType());
        resume.setIsDefault(shouldBeDefault);

        Resume saved = resumeRepository.save(resume);
        return toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ResumeResponse> getMyResumes(String email) throws JobPortalException {
        Profile profile = findProfileByEmail(email);
        return resumeRepository.findByProfileIdOrderByIsDefaultDescCreatedAtDesc(profile.getId())
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ResumeResponse getResumeById(Long id, String email) throws JobPortalException {
        Profile profile = findProfileByEmail(email);
        Resume resume = findResumeByIdAndValidateOwnership(id, profile.getId());
        return toResponse(resume);
    }

    @Override
    @Transactional
    public ResumeResponse updateResume(Long id, ResumeUpdateRequest request, String email)
            throws JobPortalException {
        Profile profile = findProfileByEmail(email);
        Resume resume = findResumeByIdAndValidateOwnership(id, profile.getId());

        if (request.getResumeName() != null && !request.getResumeName().isBlank()) {
            resume.setResumeName(request.getResumeName().trim());
        }

        if (Boolean.TRUE.equals(request.getIsDefault()) && !Boolean.TRUE.equals(resume.getIsDefault())) {
            resumeRepository.unsetDefaultResumesForProfile(profile.getId());
            resume.setIsDefault(true);
        }

        Resume updated = resumeRepository.save(resume);
        return toResponse(updated);
    }

    @Override
    @Transactional
    public void deleteResume(Long id, String email) throws JobPortalException {
        Profile profile = findProfileByEmail(email);
        Resume resume = findResumeByIdAndValidateOwnership(id, profile.getId());

        boolean wasDefault = Boolean.TRUE.equals(resume.getIsDefault());

        fileStorageService.delete(resume.getResumeUrl());
        resumeRepository.delete(resume);
        resumeRepository.flush();

        if (wasDefault) {
            List<Resume> remaining = resumeRepository.findByProfileIdOrderByIsDefaultDescCreatedAtDesc(profile.getId());
            if (!remaining.isEmpty()) {
                Resume newDefault = remaining.get(0);
                newDefault.setIsDefault(true);
                resumeRepository.save(newDefault);
            }
        }
    }

    @Override
    @Transactional
    public ResumeResponse setDefaultResume(Long id, String email) throws JobPortalException {
        Profile profile = findProfileByEmail(email);
        Resume resume = findResumeByIdAndValidateOwnership(id, profile.getId());

        if (!Boolean.TRUE.equals(resume.getIsDefault())) {
            resumeRepository.unsetDefaultResumesForProfile(profile.getId());
            resume.setIsDefault(true);
            resume = resumeRepository.save(resume);
        }

        return toResponse(resume);
    }

    @Override
    public ResumeResponse toResponse(Resume resume) {
        if (resume == null) return null;
        ResumeResponse response = new ResumeResponse();
        response.setId(resume.getId());
        response.setResumeName(resume.getResumeName());
        response.setFileName(resume.getFileName());
        response.setFileUrl(resume.getResumeUrl());
        response.setFileSize(resume.getFileSizeBytes());
        response.setContentType(resume.getContentType());
        response.setIsDefault(resume.getIsDefault());
        response.setCreatedAt(resume.getCreatedAt());
        response.setUpdatedAt(resume.getUpdatedAt());
        return response;
    }

    // ── Private Helpers ───────────────────────────────────────────────────────

    private Profile findProfileByEmail(String email) throws JobPortalException {
        return profileRepository.findByUserEmail(email)
                .orElseThrow(() -> JobPortalException.notFound("Profile not found for email: " + email));
    }

    private Resume findResumeByIdAndValidateOwnership(Long resumeId, Long profileId)
            throws JobPortalException {
        Resume resume = resumeRepository.findById(resumeId)
                .orElseThrow(() -> JobPortalException.notFound("Resume not found with id: " + resumeId));

        if (!resume.getProfile().getId().equals(profileId)) {
            throw JobPortalException.forbidden("You are not authorized to access this resume.");
        }
        return resume;
    }
}
