package com.skcorp.skbank.account_service.repositories;

import com.skcorp.skbank.account_service.entities.projections.AbstractAccountNumber;
import com.skcorp.skbank.account_service.entities.AccountLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface AccountLogRepository extends JpaRepository<AccountLog, Long> {


    @Query(value = "SELECT a.account_number as accountNumber FROM account_log al " +
            "INNER JOIN account a ON a.id = al.account_id " +
            "ORDER BY al.updated_at DESC LIMIT 1", nativeQuery = true)
    AbstractAccountNumber findLastCreatedAccountNumber();

    Optional<AccountLog> findByStatusIgnoreCaseAndAccountAccountNumber(String status, String accountNumber);
}
