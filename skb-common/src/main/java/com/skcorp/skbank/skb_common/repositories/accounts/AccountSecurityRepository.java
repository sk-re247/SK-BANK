package com.skcorp.skbank.skb_common.repositories.accounts;

import com.skcorp.skbank.skb_common.entities.accounts.AccountSecurity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountSecurityRepository extends JpaRepository<AccountSecurity, Long> {

    AccountSecurity findByAccount_AccountNumber(String accountNumber);
}
