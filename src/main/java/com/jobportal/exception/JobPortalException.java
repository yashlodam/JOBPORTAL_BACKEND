package com.jobportal.exception;

import org.springframework.http.HttpStatus;

/**
 * Domain exception with HTTP status awareness.
 * Use factory methods for semantic, meaningful exceptions.
 */
public class JobPortalException extends RuntimeException {

    private final HttpStatus httpStatus;

    public JobPortalException(String message, HttpStatus httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
    }

   
	public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    // ── Factory Methods ──────────────────────────────────────────

    public static JobPortalException notFound(String message) {
        return new JobPortalException(message, HttpStatus.NOT_FOUND);
    }

    public static JobPortalException conflict(String message) {
        return new JobPortalException(message, HttpStatus.CONFLICT);
    }

    public static JobPortalException forbidden(String message) {
        return new JobPortalException(message, HttpStatus.FORBIDDEN);
    }

    public static JobPortalException badRequest(String message) {
        return new JobPortalException(message, HttpStatus.BAD_REQUEST);
    }

    public static JobPortalException unauthorized(String message) {
        return new JobPortalException(message, HttpStatus.UNAUTHORIZED);
    }

    public static JobPortalException internalError(String message) {
        return new JobPortalException(message, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
