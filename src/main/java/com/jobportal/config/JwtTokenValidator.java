package com.jobportal.config;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import javax.crypto.SecretKey;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class JwtTokenValidator extends OncePerRequestFilter {

    private static final SecretKey KEY =
            Keys.hmacShaKeyFor(
                    JWT_CONSTANT.SECRET_KEY.getBytes(StandardCharsets.UTF_8));

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        String jwt = request.getHeader(JWT_CONSTANT.JWT_HEADER);

        if (jwt != null && jwt.startsWith("Bearer ")) {

            try {

                jwt = jwt.substring(7);

                Claims claims = Jwts.parser()
                        .verifyWith(KEY)
                        .build()
                        .parseSignedClaims(jwt)
                        .getPayload();

                String email = claims.getSubject();

                // If you didn't set subject while generating the token,
                // uncomment the line below instead.
                // String email = claims.get("email", String.class);

                String authorities =
                        claims.get("authorities", String.class);

                List<GrantedAuthority> grantedAuthorities =
                        AuthorityUtils.commaSeparatedStringToAuthorityList(authorities);

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                email,
                                null,
                                grantedAuthorities);

                SecurityContextHolder.getContext().setAuthentication(authentication);

            } catch (Exception e) {

                SecurityContextHolder.clearContext();

                throw new BadCredentialsException("Invalid JWT Token");
            }
        }

        filterChain.doFilter(request, response);
    }
}