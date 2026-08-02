package com.jobportal.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
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

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtProvider jwtProvider;
    private final UserDetailsService userDetailsService;
    private final List<String> allowedOrigins;

    public SecurityConfig(
            JwtProvider jwtProvider,
            UserDetailsService userDetailsService,
            @Value("${cors.allowed-origins:http://localhost:5173,http://localhost:3000}") String originsProperty) {
        this.jwtProvider = jwtProvider;
        this.userDetailsService = userDetailsService;
        this.allowedOrigins = List.of(originsProperty.split(","));
    }

    @Bean
    JwtTokenValidator jwtTokenValidator() {
        return new JwtTokenValidator(jwtProvider, userDetailsService);
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(allowedOrigins);
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setExposedHeaders(List.of("Authorization"));
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
            .csrf(csrf -> csrf.disable())
            .cors(Customizer.withDefaults())
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth

                // ── Public Authentication Endpoints ──
                .requestMatchers(
                        "/api/auth/register",
                        "/api/auth/login",
                        "/api/auth/send-otp/**",
                        "/api/auth/verify-otp",
                        "/api/auth/reset-password"
                ).permitAll()

                // ── Public File Access ──
                .requestMatchers("/uploads/**").permitAll()

                // ── Public Job Browsing ──
                .requestMatchers(HttpMethod.GET,
                        "/api/jobs",
                        "/api/jobs/{jobId}",
                        "/api/jobs/search",
                        "/api/jobs/latest",
                        "/api/jobs/featured",
                        "/api/jobs/{jobId}/similar",
                        "/api/jobs/category/{category}",
                        "/api/jobs/company/{companyId}"
                ).permitAll()

                // POST endpoints that are public (filter + view-count)
                .requestMatchers(HttpMethod.POST,
                        "/api/jobs/filter",
                        "/api/jobs/{jobId}/view"
                ).permitAll()

                // ── Public Company Browsing ──
                .requestMatchers(HttpMethod.GET,
                        "/api/companies",
                        "/api/companies/{companyId}",
                        "/api/companies/search"
                ).permitAll()

                // ── Public Profile View ──
                .requestMatchers(HttpMethod.GET, "/api/profile/{email}").permitAll()

                // ── Swagger / OpenAPI ──
                .requestMatchers(
                        "/v3/api-docs/**",
                        "/swagger-ui/**",
                        "/swagger-ui.html"
                ).permitAll()

                // ── All Other APIs Require Authentication ──
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtTokenValidator(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}