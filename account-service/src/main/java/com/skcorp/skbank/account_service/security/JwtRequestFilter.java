package com.skcorp.skbank.account_service.security;

import com.skcorp.skbank.account_service.common.utils.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    @Autowired
    public JwtRequestFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String authorizationHeader = request.getHeader("Authorization");

        String jwt = null;
        String accountNumber = null;

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwt = authorizationHeader.substring(7);
            try {
                accountNumber = jwtUtil.extractAccountNumber(jwt); // Extract account number from token
            } catch (Exception e) {
                String errorMessageObject = "{\"success\": false, \"message\": \"Invalid JWT format\" " + e.getMessage() + " }";
                // Log and respond if the token is invalid or malformed
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                response.getWriter().write(errorMessageObject);
                return; // Prevent further processing
            }
        }

        // Validate JWT and set security context if valid
        if (accountNumber != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            if (jwtUtil.validateToken(jwt)) {
                // If valid, allow request to proceed to controller
                filterChain.doFilter(request, response);
                return;
            } else {
                String errorMessageObject = "{\"success\": false, \"message\": \"JWT validation failed\" }";
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                response.getWriter().write(errorMessageObject);
                return; // Prevent further processing
            }
        }

        // Continue filter chain for unauthenticated requests that are allowed
        filterChain.doFilter(request, response);
    }
}