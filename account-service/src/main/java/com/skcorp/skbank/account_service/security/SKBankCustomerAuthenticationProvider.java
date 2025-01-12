package com.skcorp.skbank.account_service.security;

import com.skcorp.skbank.account_service.common.GlobalServiceHelper;
import com.skcorp.skbank.account_service.exceptions.AccountServiceException;
import com.skcorp.skbank.skb_common.entities.accounts.AccountSecurity;
import com.skcorp.skbank.skb_common.repositories.accounts.AccountSecurityRepository;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class SKBankCustomerAuthenticationProvider implements AuthenticationProvider {

    private final PasswordEncoder passwordEncoder;

    private final AccountSecurityRepository accountSecurityRepository;

    private final GlobalServiceHelper globalServiceHelper;

    public SKBankCustomerAuthenticationProvider(
            PasswordEncoder passwordEncoder,
            AccountSecurityRepository accountSecurityRepository,
            GlobalServiceHelper globalServiceHelper) {
        this.passwordEncoder = passwordEncoder;
        this.accountSecurityRepository = accountSecurityRepository;
        this.globalServiceHelper = globalServiceHelper;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String accountNumber = authentication.getName();
        String password = authentication.getCredentials().toString();
        boolean isSuccess = false;

        AccountSecurity accountSecurity = accountSecurityRepository.findByAccount_AccountNumber(accountNumber);

        if (accountSecurity != null && StringUtils.hasLength(accountSecurity.getPassword())) {

            String existingEncryptedPassword = accountSecurity.getPassword();
            isSuccess = passwordEncoder.matches(password, existingEncryptedPassword);
        } else {
            throw new AccountServiceException("Account Does not exist");
        }

        if (isSuccess) {
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(accountNumber, password);
            globalServiceHelper.addAccountLoginEntry(accountSecurity.getAccount());
            authenticationToken.setDetails(accountSecurity);
            return authenticationToken;
        } else {
            throw new BadCredentialsException("Invalid password or expired");
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
