package com.skcorp.skbank.card_service.security;

import com.skcorp.skbank.card_service.common.utils.JwtUtil;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    private final CustomAuthenticationFailureHandler customAuthenticationFailureHandler;

    public JwtAuthenticationFilter(
            JwtUtil jwtUtil,
            CustomAuthenticationFailureHandler customAuthenticationFailureHandler) {
        this.jwtUtil = jwtUtil;
        this.customAuthenticationFailureHandler = customAuthenticationFailureHandler;
    }


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        try {

            String authorizationToken = request.getHeader("Authorization");

            if (authorizationToken == null || !StringUtils.hasLength(authorizationToken) || !authorizationToken.startsWith("Bearer ")) {
                filterChain.doFilter(request, response);
                return;
            }

            PreAuthenticatedAuthenticationToken authentication = getAuthentication(authorizationToken.replace("Bearer ", "").trim());
            SecurityContextHolder.getContext().setAuthentication(authentication);
            filterChain.doFilter(request, response);

        } catch (BadCredentialsException badCredentialsException) {
            SecurityContextHolder.clearContext();
            this.customAuthenticationFailureHandler.onAuthenticationFailure(request, response, badCredentialsException);
        } catch (AuthenticationException exception) {
            this.customAuthenticationFailureHandler.onAuthenticationFailure(request, response, exception);
        }
    }

    private PreAuthenticatedAuthenticationToken getAuthentication(String token) {

        try {
            String accountNumber = jwtUtil.extractAccountNumber(token);
            if (accountNumber != null && jwtUtil.validateToken(token)) {
                return new PreAuthenticatedAuthenticationToken(accountNumber, token, null);
            }
            throw new BadCredentialsException("Invalid JWT");
        } catch (BadCredentialsException badCredentialsException) {
            throw badCredentialsException;
        } catch (MalformedJwtException exception) {
            throw exception;
        }
    }
}