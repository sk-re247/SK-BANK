package com.skcorp.skbank.account_service.repositories;

import com.skcorp.skbank.account_service.entities.AccountBalanceXref;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountBalanceXrefRepository extends JpaRepository<AccountBalanceXref, Long> {
}
