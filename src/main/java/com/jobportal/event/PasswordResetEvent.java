package com.jobportal.event;

import org.springframework.context.ApplicationEvent;

import com.jobportal.entity.User;

/**
 * Published by {@link com.jobportal.serviceImpl.UserServiceImpl}
 * after a user successfully resets their password via OTP verification.
 *
 * <p>The listener will create a {@code SECURITY} notification to alert the user
 * that their password was changed — a common security best-practice seen in
 * LinkedIn, GitHub, Google Account, etc.</p>
 */
public class PasswordResetEvent extends ApplicationEvent {

    private final User user;

    public PasswordResetEvent(Object source, User user) {
        super(source);
        this.user = user;
    }

    public User getUser() { return user; }
}
