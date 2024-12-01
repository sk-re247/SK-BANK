package com.skcorp.skbank.account_service.services;


import com.skcorp.skbank.account_service.common.dtos.AccountRequest;
import com.skcorp.skbank.account_service.common.dtos.AccountResponse;

public interface AccountService {

    AccountResponse createAccount(AccountRequest accountRequest);
}
