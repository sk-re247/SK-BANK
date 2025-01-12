package com.skcorp.skbank.skb_common.repositories.accounts;

import com.skcorp.skbank.skb_common.entities.accounts.AccountBalanceXref;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountBalanceXrefRepository extends JpaRepository<AccountBalanceXref, Long> {
}
