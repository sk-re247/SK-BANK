package com.skcorp.skbank.skb_common.repositories.accounts;

import com.skcorp.skbank.skb_common.entities.accounts.AccountProof;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccountProofRepository extends JpaRepository<AccountProof, Long> {

    Optional<AccountProof> findByAccount_AccountNumber(String accountNumber);

}
