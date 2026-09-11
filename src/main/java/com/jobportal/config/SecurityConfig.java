package com.jobportal.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtProvider jwtProvider;
    private final UserDetailsService userDetailsService;
    private final CookieProperties cookieProperties;

    @Value("${app.cors.allowed-origins:http://localhost:5173,http://localhost:3000,http://127.0.0.1:5173,http://localhost:5174,https://*.vercel.app,https://job-portal-frontend-rho-nine.vercel.app}")
    private String allowedOrigins;

    public SecurityConfig(
            JwtProvider jwtProvider,
            UserDetailsService userDetailsService,
            CookieProperties cookieProperties) {
        this.jwtProvider = jwtProvider;
        this.userDetailsService = userDetailsService;
        this.cookieProperties = cookieProperties;
    }

    @Bean
    JwtTokenValidator jwtTokenValidator() {
        return new JwtTokenValidator(jwtProvider, userDetailsService, cookieProperties);
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        List<String> origins = Arrays.stream(allowedOrigins.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList();
        
        configuration.setAllowedOriginPatterns(origins);
        configuration.setAllowedMethods(
                List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS", "HEAD"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setExposedHeaders(List.of("Content-Type", "Set-Cookie", "Authorization"));
        // Required for cookies to be sent cross-origin (withCredentials: true on frontend)
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // REST APIs with SameSite=Lax HttpOnly cookies are protected against cross-origin CSRF.
            .csrf(csrf -> csrf.disable())
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                // ── Public Preflight Requests ──
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                // ── Health Check & Root (Render & Platform Monitors) ──
                .requestMatchers("/", "/health", "/api/health", "/actuator/health").permitAll()

                // ── Public Authentication Endpoints ──
                .requestMatchers(
                        "/api/auth/register",
                        "/api/auth/login",
                        "/api/auth/logout",
                        "/api/auth/send-otp/**",
                        "/api/auth/verify-otp",
                        "/api/auth/reset-password"
                ).permitAll()

                // ── Auth restore (cookie-authenticated) ──
                .requestMatchers(HttpMethod.GET, "/api/auth/me").authenticated()

                // ── Public File Access ──
                .requestMatchers("/uploads/**").permitAll()

                // ── Public Job Browsing (read-only) ──
                .requestMatchers(HttpMethod.GET,
                        "/api/jobs",
                        "/api/jobs/{jobId}",
                        "/api/jobs/search",
                        "/api/jobs/latest",
                        "/api/jobs/featured",
                        "/api/jobs/{jobId}/similar",
                        "/api/jobs/category/{category}",
                        "/api/jobs/categories",
                        "/api/jobs/work-modes",
                        "/api/jobs/suggestions",
                        "/api/jobs/facets",
                        "/api/jobs/company/jobs/{companyId}"
                ).permitAll()

                // POST endpoints that are public (filter + view-count)
                .requestMatchers(HttpMethod.POST,
                        "/api/jobs/filter",
                        "/api/jobs/{jobId}/view"
                ).permitAll()

                // ── Public Company Browsing (read-only) ──
                .requestMatchers(HttpMethod.GET,
                        "/api/companies",
                        "/api/companies/{companyId}",
                        "/api/companies/{companyId}/jobs",
                        "/api/companies/search"
                ).permitAll()

                // ── Public Profile View ──
                .requestMatchers(HttpMethod.GET, "/api/profile/{email}").permitAll()

                // ── AI Career Copilot (Public & Authenticated) ──
                .requestMatchers("/api/ai/copilot/**").permitAll()

                // ── WebSocket Handshake (STOMP over SockJS) ──
                .requestMatchers("/ws/**").permitAll()

                // ── Swagger / OpenAPI ──
                .requestMatchers(
                        "/v3/api-docs/**",
                        "/swagger-ui/**",
                        "/swagger-ui.html"
                ).permitAll()

                // ── Recruiter APIs — authenticated ──
                .requestMatchers("/api/recruiter/**").authenticated()

                // ── Admin APIs — ADMIN role required ──
                .requestMatchers("/api/admin/**", "/admin/**").hasAuthority("ADMIN")

                // ── Users & Talent APIs ──
                .requestMatchers("/api/users/**", "/users/**").authenticated()
                .requestMatchers("/api/talent/**").authenticated()

                // ── Candidate APIs ──
                .requestMatchers("/api/candidate/**").authenticated()

                // ── AI APIs ──
                .requestMatchers("/api/resumes/*/analyze").authenticated()
                .requestMatchers("/api/resume-analysis/**").authenticated()
                .requestMatchers("/api/resume-builder/**").authenticated()
                .requestMatchers("/api/interviews/**").authenticated()

                // ── Recommendation APIs (Applicant feed) ──
                .requestMatchers("/api/recommendations/**").authenticated()

                // ── All other requests require authentication ──
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtTokenValidator(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}