package com.skcorp.skbank.skb_common.repositories.accounts;

import com.skcorp.skbank.skb_common.entities.accounts.AccountLoginHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountLoginHistoryRepository extends JpaRepository<AccountLoginHistory, Long> {

}
