package com.jobportal.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

/**
 * Lightweight health check controller for cloud platforms (e.g. Render, Railway, AWS).
 * Returns 200 OK without requiring database access or JWT authentication.
 */
@RestController
public class HealthController {

    private final Instant startTime = Instant.now();

    @GetMapping({"/", "/health", "/api/health", "/actuator/health"})
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> response = new HashMap<>();
        response.put("name", "JobPortal AI Backend API");
        response.put("status", "UP");
        response.put("timestamp", Instant.now().toString());
        response.put("uptime", (Instant.now().toEpochMilli() - startTime.toEpochMilli()) / 1000 + "s");
        response.put("docs", "/swagger-ui.html");
        return ResponseEntity.ok(response);
    }
}
