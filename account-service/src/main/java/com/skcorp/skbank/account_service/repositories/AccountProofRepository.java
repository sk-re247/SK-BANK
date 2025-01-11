package com.skcorp.skbank.account_service.repositories;

import com.skcorp.skbank.account_service.entities.AccountProof;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccountProofRepository extends JpaRepository<AccountProof, Long> {

    Optional<AccountProof> findByAccount_AccountNumber(String accountNumber);

}
