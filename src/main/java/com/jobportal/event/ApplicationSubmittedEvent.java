package com.jobportal.event;

import org.springframework.context.ApplicationEvent;

import com.jobportal.entity.Job;
import com.jobportal.entity.User;

/**
 * Published by {@link com.jobportal.serviceImpl.JobApplicationServiceImpl} when a
 * candidate successfully submits a job application.
 *
 * <p>Carries all data needed by the listener to build the notification without any
 * additional DB queries.</p>
 */
public class ApplicationSubmittedEvent extends ApplicationEvent {

    private final User applicant;
    /** The recruiter who posted the job — pre-resolved to avoid lazy access in listener. */
    private final User recruiterUser;
    private final Job job;
    private final Long applicationId;

    public ApplicationSubmittedEvent(Object source,
                                     User applicant,
                                     User recruiterUser,
                                     Job job,
                                     Long applicationId) {
        super(source);
        this.applicant      = applicant;
        this.recruiterUser  = recruiterUser;
        this.job            = job;
        this.applicationId  = applicationId;
    }

    public User getApplicant()     { return applicant; }
    public User getRecruiterUser() { return recruiterUser; }
    public Job getJob()            { return job; }
    public Long getApplicationId() { return applicationId; }
}
