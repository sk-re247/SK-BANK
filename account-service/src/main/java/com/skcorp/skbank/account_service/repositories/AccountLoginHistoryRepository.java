package com.skcorp.skbank.account_service.repositories;

import com.skcorp.skbank.account_service.entities.AccountLoginHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountLoginHistoryRepository extends JpaRepository<AccountLoginHistory, Long> {

}
