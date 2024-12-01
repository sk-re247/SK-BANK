package com.skcorp.skbank.account_service.repositories;

import com.skcorp.skbank.account_service.entities.Account;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, Long> {

}
