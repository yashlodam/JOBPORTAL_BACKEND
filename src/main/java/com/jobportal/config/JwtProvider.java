package com.jobportal.config;

import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

import javax.crypto.SecretKey;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtProvider {

    private static final SecretKey KEY =
            Keys.hmacShaKeyFor(
                    JWT_CONSTANT.SECRET_KEY.getBytes(StandardCharsets.UTF_8));

    private static final long EXPIRATION_TIME = 24 * 60 * 60 * 1000L; // 24 Hours

    public static String generateToken(Authentication authentication) {

        Collection<? extends GrantedAuthority> authorities =
                authentication.getAuthorities();

        String roles = populateAuthorities(authorities);

        return Jwts.builder()
                .subject(authentication.getName())              // User Email
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .claim("email", authentication.getName())
                .claim("authorities", roles)
                .signWith(KEY)
                .compact();
    }

    public static String getEmailFromJwtToken(String jwt) {

        if (jwt != null && jwt.startsWith("Bearer ")) {
            jwt = jwt.substring(7);
        }

        Claims claims = Jwts.parser()
                .verifyWith(KEY)
                .build()
                .parseSignedClaims(jwt)
                .getPayload();

        return claims.getSubject();
    }

    private static String populateAuthorities(
            Collection<? extends GrantedAuthority> authorities) {

        return authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));
    }
}