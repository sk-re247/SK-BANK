package com.skcorp.skbank.account_service.repositories;

import com.skcorp.skbank.account_service.entities.BranchNameAddressXref;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BranchNameAddressXrefRepository extends JpaRepository<BranchNameAddressXref, Long> {

    Optional<BranchNameAddressXref> findByBranchNameIgnoreCase(String branchName);
}
