package com.skcorp.skbank.account_service.controllers;

import com.skcorp.skbank.account_service.client.api.SkBankAccountsApi;
import com.skcorp.skbank.account_service.client.models.AccountRequest;
import com.skcorp.skbank.account_service.client.models.AccountResponse;
import com.skcorp.skbank.account_service.client.models.AccountUploadProofRequest;
import com.skcorp.skbank.account_service.client.models.AccountUploadProofResponse;
import com.skcorp.skbank.account_service.services.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AccountController implements SkBankAccountsApi {

    private final AccountService accountService;

    @Autowired
    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }


    @Override
    public ResponseEntity<AccountResponse> createNewAccount(AccountRequest request) {

        AccountResponse response = accountService.createAccount(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Override
    public ResponseEntity<AccountUploadProofResponse> uploadAccountProof(AccountUploadProofRequest request) {

        AccountUploadProofResponse response = new AccountUploadProofResponse();

        accountService.uploadAccountProof(request);

        response.setMessage("Your proof has been uploaded and account activated successfully");

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
