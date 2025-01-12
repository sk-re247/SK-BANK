package com.skcorp.skbank.skb_common.repositories.accounts;

import com.skcorp.skbank.skb_common.entities.accounts.AccountTransactionLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountTransactionLogRepository extends JpaRepository<AccountTransactionLog, Long> {
}
