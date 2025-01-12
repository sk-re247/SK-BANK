package com.skcorp.skbank.skb_common.repositories.accounts;

import com.skcorp.skbank.skb_common.entities.accounts.BranchNameAddressXref;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BranchNameAddressXrefRepository extends JpaRepository<BranchNameAddressXref, Long> {

    Optional<BranchNameAddressXref> findByBranchNameIgnoreCase(String branchName);
}
