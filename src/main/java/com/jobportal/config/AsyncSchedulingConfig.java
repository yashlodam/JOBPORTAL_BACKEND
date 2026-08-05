package com.jobportal.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Enables Spring's task scheduling and async execution infrastructure.
 *
 * <ul>
 *   <li>{@code @EnableScheduling} — activates {@code @Scheduled} on
 *       {@link com.jobportal.scheduler.NotificationScheduler}.</li>
 *   <li>{@code @EnableAsync} — enables {@code @Async} for event listeners
 *       when they are promoted to async processing (e.g. via Kafka/RabbitMQ)
 *       in a future iteration.</li>
 * </ul>
 *
 * <p>Kept in a separate class so it does not modify the existing {@code AppConfig}
 * or {@code SecurityConfig}.</p>
 */
@Configuration
@EnableScheduling
@EnableAsync
public class AsyncSchedulingConfig {
    // Spring Boot auto-configures a default TaskScheduler thread pool.
    // Override the threadPoolTaskScheduler bean here if you need custom
    // pool size, thread name prefix, or error handlers.
}
