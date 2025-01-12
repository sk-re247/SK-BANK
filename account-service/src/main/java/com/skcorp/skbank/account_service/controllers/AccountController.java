package com.skcorp.skbank.account_service.controllers;

import com.skcorp.skbank.account_service.client.api.SkBankAccountsApi;
import com.skcorp.skbank.account_service.client.models.AccountAuthentication;
import com.skcorp.skbank.account_service.client.models.AccountDetailsResponse;
import com.skcorp.skbank.account_service.client.models.AccountRequest;
import com.skcorp.skbank.account_service.client.models.AccountResponse;
import com.skcorp.skbank.account_service.client.models.AccountSecurityResponse;
import com.skcorp.skbank.account_service.client.models.AccountUploadProofRequest;
import com.skcorp.skbank.account_service.client.models.AccountUploadProofResponse;
import com.skcorp.skbank.account_service.services.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
    public ResponseEntity<AccountDetailsResponse> fetchAccountDetails(@RequestParam("accountNumber") String accountNumber) {

        AccountDetailsResponse response = accountService.fetchAccountDetails(accountNumber);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Override
    public ResponseEntity<AccountUploadProofResponse> uploadAccountProof(AccountUploadProofRequest request) {

        AccountUploadProofResponse response = new AccountUploadProofResponse();

        String generatedPassword = accountService.uploadAccountProof(request);

        response.setMessage("Your proof has been uploaded and account activated successfully");
        response.setGeneratedPassword(generatedPassword);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Override
    public ResponseEntity<AccountSecurityResponse> updateAccountSecurity(AccountAuthentication accountAuthentication) {

        AccountSecurityResponse response = new AccountSecurityResponse();

        accountService.updateAccountSecurity(accountAuthentication);

        response.setMessage("Customer password has been updated successfully");
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping(path = "/account/holder-name")
    public ResponseEntity<String> fetchAccountHolderName(@RequestParam String accountNumber, @RequestParam String mobileNumber) {

        String cardHolderName = accountService.fetchAccountHolderName(accountNumber, mobileNumber);

        return ResponseEntity.status(HttpStatus.OK).body(cardHolderName);
    }
}
