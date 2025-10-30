package com.kartikay.sales_order_management_api.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenUtil jwtUtil;
    private final UserDetailsService userDetailsService;

    public JwtAuthenticationFilter(JwtTokenUtil jwtUtil, UserDetailsService userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        try {
            String authHeader = request.getHeader("Authorization");

            // 1. Check if header is present and starts with Bearer
            if (!StringUtils.hasText(authHeader) || !authHeader.startsWith("Bearer ")) {
                filterChain.doFilter(request, response);
                return;
            }

            // 2. Extract token from header
            String token = authHeader.substring(7);

            // 3. Validate token
            if (!jwtUtil.validateToken(token)) {
                filterChain.doFilter(request, response);
                return;
            }

            // Extract username
            String username = jwtUtil.getUsernameFromToken(token);
            if (username == null) {
                filterChain.doFilter(request, response);
                return;
            }

            // Proceed only if context is not already authenticated
            if (SecurityContextHolder.getContext().getAuthentication() == null) {

                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                // Optional: extract roles if stored in token
                List<SimpleGrantedAuthority> authorities = jwtUtil.getRolesFromToken(token).stream()
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

                // Build authentication token
                UsernamePasswordAuthenticationToken authenticationToken =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                authorities.isEmpty() ? userDetails.getAuthorities() : authorities
                        );

                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // Set authentication context
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }

        } catch (Exception ex) {
            System.err.println("JWT authentication failed: " + ex.getMessage());
        }

        // Continue filter chain
        filterChain.doFilter(request, response);
    }
}
