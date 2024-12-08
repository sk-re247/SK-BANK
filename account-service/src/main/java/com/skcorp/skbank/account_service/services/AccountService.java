package com.skcorp.skbank.account_service.services;

import com.skcorp.skbank.account_service.client.models.AccountRequest;
import com.skcorp.skbank.account_service.client.models.AccountResponse;
import com.skcorp.skbank.account_service.client.models.AccountUploadProofRequest;

public interface AccountService {

    AccountResponse createAccount(AccountRequest accountRequest);

    void uploadAccountProof(AccountUploadProofRequest request);
}
