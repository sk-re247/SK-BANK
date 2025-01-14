package com.skcorp.skbank.skb_common.repositories.accounts;

import com.skcorp.skbank.skb_common.entities.accounts.AccountLog;
import com.skcorp.skbank.skb_common.entities.accounts.projections.AbstractAccountNumber;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface AccountLogRepository extends JpaRepository<AccountLog, Long> {


    @Query(value = "SELECT a.account_number as accountNumber FROM account_log al " +
            "INNER JOIN account a ON a.id = al.account_id " +
            "ORDER BY al.updated_at DESC LIMIT 1", nativeQuery = true)
    AbstractAccountNumber findLastCreatedAccountNumber();

    // Optional<AccountLog> findByStatusIgnoreCaseAndAccountAccountNumber(String status, String accountNumber);

    Optional<AccountLog> findByStatusIgnoreCaseAndAccountAccountNumberAndAccountIsActive(String status, String accountNumber, boolean isActive);
}
