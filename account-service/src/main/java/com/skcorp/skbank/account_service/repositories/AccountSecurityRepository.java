package com.skcorp.skbank.account_service.repositories;

import com.skcorp.skbank.account_service.entities.AccountSecurity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountSecurityRepository extends JpaRepository<AccountSecurity, Long> {

    AccountSecurity findByAccount_AccountNumber(String accountNumber);
}
