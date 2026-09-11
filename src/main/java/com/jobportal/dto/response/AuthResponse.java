package com.jobportal.dto.response;

/**
 * Authentication response returned after a successful login.
 *
 * SECURITY NOTE: After migrating to HttpOnly cookie authentication,
 * the JWT is NO LONGER returned in the response body.
 * The token is set as an HttpOnly cookie by AuthController.
 * This response only contains safe, non-sensitive user information.
 */
public class AuthResponse {

    /** Human-readable status message (e.g. "Login successful"). */
    private String message;

    // ── Safe user data (previously obtained via a separate /users/me call) ──
    /** User's database ID. */
    private Long id;

    /** User's display name. */
    private String name;

    /** User's email address (also serves as login username). */
    private String email;

    /** Account type: APPLICANT, EMPLOYER, ADMIN. */
    private String accountType;

    // ── Constructors ────────────────────────────────────────────────────────

    public AuthResponse() {}

    /** Minimal constructor used after successful login. */
    public AuthResponse(String message, Long id, String name, String email, String accountType) {
        this.message = message;
        this.id = id;
        this.name = name;
        this.email = email;
        this.accountType = accountType;
    }

    // ── Getters & Setters ────────────────────────────────────────────────────

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getAccountType() { return accountType; }
    public void setAccountType(String accountType) { this.accountType = accountType; }
}
