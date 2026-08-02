package com.jobportal.exception;

import java.time.LocalDateTime;

/**
 * Standardized error response returned by the global exception handler.
 */
public class ErrorResponse {

    private String message;
    private int status;
    private String error;
    private LocalDateTime timestamp;

    public ErrorResponse(String message, int status, String error, LocalDateTime timestamp) {
        this.message = message;
        this.status = status;
        this.error = error;
        this.timestamp = timestamp;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
