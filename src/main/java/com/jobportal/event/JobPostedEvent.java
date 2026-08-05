package com.jobportal.event;

import org.springframework.context.ApplicationEvent;

import com.jobportal.entity.Job;
import com.jobportal.entity.User;

/**
 * Published by {@link com.jobportal.serviceImpl.JobServiceImpl} when a recruiter
 * successfully creates a new job posting.
 *
 * <p>Carrying the full {@link Job} entity is safe because this event is dispatched
 * synchronously within the same transaction — the entity is still managed (attached).
 * If this event is ever made {@code @Async}, switch to passing scalar IDs instead.</p>
 */
public class JobPostedEvent extends ApplicationEvent {

    private final User recruiterUser;
    private final Job job;

    public JobPostedEvent(Object source, User recruiterUser, Job job) {
        super(source);
        this.recruiterUser = recruiterUser;
        this.job = job;
    }

    public User getRecruiterUser() { return recruiterUser; }
    public Job getJob() { return job; }
}
