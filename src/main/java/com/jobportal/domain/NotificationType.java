package com.jobportal.domain;

/**
 * Enum of all notification event types used across the job portal.
 * Used to drive message templates and frontend routing.
 */
public enum NotificationType {

    // ── Application Events ───────────────────────────────────────────────────
    /** Recruiter receives this when an applicant applies to their job. */
    APPLICATION_RECEIVED,

    /** Applicant receives this when a recruiter changes their application status. */
    APPLICATION_STATUS_UPDATED,

    /** Applicant receives this when their application is withdrawn (self-initiated confirmation). */
    APPLICATION_WITHDRAWN,

    // ── Job Events ───────────────────────────────────────────────────────────
    /** Applicant receives this when a job they saved is closed/filled. */
    SAVED_JOB_CLOSED,

    /** Applicant receives this when a job matching their profile is posted. */
    JOB_RECOMMENDATION,

    // ── Profile Events ───────────────────────────────────────────────────────
    /** User receives this when a recruiter views their profile. */
    PROFILE_VIEWED,

    // ── Company Events ───────────────────────────────────────────────────────
    /** Recruiter receives this when admin approves their company. */
    COMPANY_APPROVED,

    // ── System / Admin ───────────────────────────────────────────────────────
    /** Generic system announcement to all users. */
    SYSTEM_ANNOUNCEMENT,

    /** Account-related notifications (e.g. password changed, profile incomplete). */
    ACCOUNT_ALERT
}
