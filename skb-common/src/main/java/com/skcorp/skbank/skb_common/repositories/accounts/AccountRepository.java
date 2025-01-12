package com.skcorp.skbank.skb_common.repositories.accounts;

import com.skcorp.skbank.skb_common.entities.accounts.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {

    Optional<Account> findByAccountNumber(String accountNumber);

}
