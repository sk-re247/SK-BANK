package com.skcorp.skbank.account_service.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {

        int statusCode = HttpServletResponse.SC_UNAUTHORIZED;

        if (exception instanceof AuthenticationServiceException)
            statusCode = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
        else if (exception instanceof BadCredentialsException && exception.getMessage().equals("Invalid JWT"))
            statusCode = HttpServletResponse.SC_BAD_REQUEST;

        response.setStatus(statusCode);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        String message = statusCode == 500 ? "{\"message\": \"Internal Server error\"}" : "{\"message\": \"Bad Request\"}";
        response.getWriter().write(message);
    }
}
