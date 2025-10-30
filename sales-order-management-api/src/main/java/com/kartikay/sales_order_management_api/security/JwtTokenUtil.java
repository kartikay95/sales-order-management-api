package com.kartikay.sales_order_management_api.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class JwtTokenUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expirationMs}")
    private long jwtExpirationMs;

    private Key key;

    /**
     * Initializes the signing key after the bean is constructed.
     */
    @PostConstruct
    public void init() {
        if (secret == null || secret.length() < 32) {
            throw new IllegalArgumentException("JWT secret must be at least 32 characters long for HS256");
        }
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
    }

    /**
     * Generates a JWT token for the given username and roles.
     */
    public String generateToken(String username, Collection<String> roles) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", roles);

        Date now = new Date();
        Date expiry = new Date(now.getTime() + jwtExpirationMs);

        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(now)
                .setExpiration(expiry)
                .addClaims(claims)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Validates the JWT token and checks signature + expiration.
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException ex) {
            System.err.println("JWT expired: " + ex.getMessage());
        } catch (UnsupportedJwtException ex) {
            System.err.println("JWT unsupported: " + ex.getMessage());
        } catch (MalformedJwtException ex) {
            System.err.println("JWT malformed: " + ex.getMessage());
        } catch (SecurityException ex) {
            System.err.println("JWT signature invalid: " + ex.getMessage());
        } catch (IllegalArgumentException ex) {
            System.err.println("JWT claims string is empty: " + ex.getMessage());
        }
        return false;
    }

    /**
     * Extracts username (subject) from the JWT token.
     */
    public String getUsernameFromToken(String token) {
        return parseClaims(token).getSubject();
    }

    /**
     * Extracts roles list from the JWT token.
     */
    @SuppressWarnings("unchecked")
    public List<String> getRolesFromToken(String token) {
        Object roles = parseClaims(token).get("roles");
        if (roles instanceof List) {
            return ((List<?>) roles)
                    .stream()
                    .map(Object::toString)
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    /**
     * Helper to parse claims from token safely.
     */
    private Claims parseClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
