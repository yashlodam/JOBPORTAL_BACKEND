package com.jobportal.event;

import org.springframework.context.ApplicationEvent;

import com.jobportal.domain.ApplicationStatus;
import com.jobportal.entity.JobApplication;

/**
 * Published by {@link com.jobportal.serviceImpl.JobApplicationServiceImpl} when a
 * recruiter changes the status of an application (shortlisted, rejected, etc.).
 *
 * <p>The full {@link JobApplication} entity is carried because: the listener needs
 * the applicant user and the job title, both of which are already loaded in the
 * calling service's transaction. The listener runs synchronously within the same
 * transaction via {@code @TransactionalEventListener(AFTER_COMMIT)} —
 * access to lazy associations is safe.</p>
 */
public class ApplicationStatusChangedEvent extends ApplicationEvent {

    private final JobApplication application;
    private final ApplicationStatus newStatus;

    public ApplicationStatusChangedEvent(Object source,
                                         JobApplication application,
                                         ApplicationStatus newStatus) {
        super(source);
        this.application = application;
        this.newStatus   = newStatus;
    }

    public JobApplication getApplication() { return application; }
    public ApplicationStatus getNewStatus()  { return newStatus; }
}
