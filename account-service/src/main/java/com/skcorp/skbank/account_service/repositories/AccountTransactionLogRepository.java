package com.skcorp.skbank.account_service.repositories;

import com.skcorp.skbank.account_service.entities.AccountTransactionLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountTransactionLogRepository extends JpaRepository<AccountTransactionLog, Long> {
}
