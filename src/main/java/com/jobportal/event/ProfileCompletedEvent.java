package com.jobportal.event;

import org.springframework.context.ApplicationEvent;

import com.jobportal.entity.User;

/**
 * Published by {@link com.jobportal.serviceImpl.ProfileServiceImpl}
 * when a profile completion check determines the profile is now complete.
 *
 * <p>The listener sends a {@code PROFILE_COMPLETED} celebration notification
 * to encourage engagement. The check is made after every major profile mutation
 * (header, skills, experience, education, certification updates).</p>
 *
 * <p>A "complete" profile is defined as having all of:
 * headline, about, at least 1 skill, at least 1 experience or education, and a resume.</p>
 */
public class ProfileCompletedEvent extends ApplicationEvent {

    private final User user;

    public ProfileCompletedEvent(Object source, User user) {
        super(source);
        this.user = user;
    }

    public User getUser() { return user; }
}
