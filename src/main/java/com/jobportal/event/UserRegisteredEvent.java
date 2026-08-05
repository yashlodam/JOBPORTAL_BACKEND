package com.jobportal.event;

import org.springframework.context.ApplicationEvent;

import com.jobportal.entity.User;

/**
 * Published by {@link com.jobportal.serviceImpl.UserServiceImpl}
 * after a new user successfully completes registration.
 *
 * <p>Carries the saved {@link User} entity — it is fully flushed by the time
 * the listener runs ({@code AFTER_COMMIT}), so accessing {@code user.getName()}
 * or {@code user.getAccountType()} is safe.</p>
 */
public class UserRegisteredEvent extends ApplicationEvent {

    private final User user;

    public UserRegisteredEvent(Object source, User user) {
        super(source);
        this.user = user;
    }

    public User getUser() { return user; }
}
