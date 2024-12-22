package com.skcorp.skbank.account_service.common.configs;

import com.skcorp.skbank.account_service.common.utils.JwtUtil;
import com.skcorp.skbank.account_service.security.JwtAuthenticationFilter;
import com.skcorp.skbank.account_service.security.SKBankCustomerAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    private final SKBankCustomerAuthenticationFilter skBankCustomerAuthenticationFilter;

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    public SecurityConfig(
            @Lazy SKBankCustomerAuthenticationFilter skBankCustomerAuthenticationFilter,
            JwtAuthenticationFilter jwtAuthenticationFilter
            ) {
        this.skBankCustomerAuthenticationFilter = skBankCustomerAuthenticationFilter;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {

        httpSecurity
                .csrf()
                .disable()
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.POST, "/account").permitAll()
                        .anyRequest()
                        .authenticated()
                )
                .cors()
                .disable()
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        httpSecurity.addFilterBefore(skBankCustomerAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        httpSecurity.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return httpSecurity.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public SKBankCustomerAuthenticationFilter skBankCustomerAuthenticationFilter(AuthenticationManager authenticationManager, JwtUtil jwtUtil) {
        return new SKBankCustomerAuthenticationFilter(authenticationManager, jwtUtil);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
