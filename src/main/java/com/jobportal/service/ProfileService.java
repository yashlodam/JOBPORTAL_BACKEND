package com.jobportal.service;

import java.util.List;

import com.jobportal.dto.ProfileDTO;
import com.jobportal.exception.JobPortalException;

public interface ProfileService {

    // Get profile by profile id
    ProfileDTO getProfile(Long id) throws JobPortalException;

    // Get profile by user email
    ProfileDTO getProfileByEmail(String email) throws JobPortalException;

    // Update profile details
    ProfileDTO updateProfile(ProfileDTO profileDTO) throws JobPortalException;

    // Delete profile
    void deleteProfile(Long profileId) throws JobPortalException;

    // Check profile exists
    boolean existsById(Long profileId);

    // Get all profiles (Admin)
    List<ProfileDTO> getAllProfiles() throws JobPortalException;

	ProfileDTO upDateProfile(ProfileDTO profileDTO) throws JobPortalException;
}