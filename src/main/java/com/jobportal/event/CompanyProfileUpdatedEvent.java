package com.jobportal.event;

import org.springframework.context.ApplicationEvent;

import com.jobportal.entity.Company;
import com.jobportal.entity.User;

/**
 * Published by {@link com.jobportal.serviceImpl.CompanyServiceImpl}
 * when a recruiter creates or updates their company profile.
 *
 * <p>The boolean {@code created} flag lets the listener differentiate between
 * a "company created" notification and a "company updated" notification
 * without requiring two separate event classes.</p>
 */
public class CompanyProfileUpdatedEvent extends ApplicationEvent {

    private final User recruiterUser;
    private final Company company;
    private final boolean created;

    public CompanyProfileUpdatedEvent(Object source,
                                      User recruiterUser,
                                      Company company,
                                      boolean created) {
        super(source);
        this.recruiterUser = recruiterUser;
        this.company       = company;
        this.created       = created;
    }

    public User getRecruiterUser() { return recruiterUser; }
    public Company getCompany()    { return company; }
    /** @return {@code true} if the company was just created; {@code false} if updated. */
    public boolean isCreated()     { return created; }
}
