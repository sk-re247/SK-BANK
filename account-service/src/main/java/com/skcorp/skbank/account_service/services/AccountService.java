package com.skcorp.skbank.account_service.services;

import com.skcorp.skbank.account_service.client.models.AccountRequest;
import com.skcorp.skbank.account_service.client.models.AccountResponse;

public interface AccountService {

    AccountResponse createAccount(AccountRequest accountRequest);
}
