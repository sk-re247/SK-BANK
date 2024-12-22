package com.skcorp.skbank.account_service.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skcorp.skbank.account_service.client.models.AccountLoginResponse;
import com.skcorp.skbank.account_service.client.models.JwtPayload;
import com.skcorp.skbank.account_service.common.utils.JwtUtil;
import com.skcorp.skbank.account_service.entities.Account;
import com.skcorp.skbank.account_service.entities.AccountSecurity;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

@Component
public class SKBankCustomerAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final JwtUtil jwtUtil;

    public SKBankCustomerAuthenticationFilter(
            AuthenticationManager authenticationManager,
            JwtUtil jwtUtil) {
        super("/account/login");
        this.jwtUtil = jwtUtil;
        setAuthenticationManager(authenticationManager);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException, ServletException {

        Map<String, String> credentials = objectMapper.readValue(request.getInputStream(), Map.class);
        String accountNumber = credentials.get("accountNumber");
        String password = credentials.get("password");

        UsernamePasswordAuthenticationToken passwordAuthenticationToken = new UsernamePasswordAuthenticationToken(accountNumber, password);

        return getAuthenticationManager().authenticate(passwordAuthenticationToken);
    }

    @Override
    public void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {




        String message = "Customer authentication was successful";
        AccountLoginResponse loginResponse = new AccountLoginResponse();

        AccountSecurity accountSecurity = (AccountSecurity) authResult.getDetails();
        Account account = accountSecurity.getAccount();

        JwtPayload payload = new JwtPayload();
        payload.setMobileNumber(account.getCustomer().getMobileNumber());
        payload.setLastName(account.getCustomer().getLastName());
        payload.setAccountNumber(account.getAccountNumber());
        payload.setCustId(account.getCustomer().getId());
        payload.setAccountType(account.getType());
        String token = jwtUtil.generateToken(payload);


        loginResponse.setAccessToken(token);
        loginResponse.setMessage(message);

        ObjectMapper objectMapper = new ObjectMapper();
        String jsonResponse = objectMapper.writeValueAsString(loginResponse);

        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write(jsonResponse);
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write("{\"message\": \"account number and password does not match\"}");
    }
}
