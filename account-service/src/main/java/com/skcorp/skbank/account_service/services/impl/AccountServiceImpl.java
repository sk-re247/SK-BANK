package com.skcorp.skbank.account_service.services.impl;

import com.skcorp.skbank.account_service.client.models.AccountAuthentication;
import com.skcorp.skbank.account_service.client.models.AccountPayload;
import com.skcorp.skbank.account_service.client.models.AccountProofTypeEnum;
import com.skcorp.skbank.account_service.client.models.AccountRequest;
import com.skcorp.skbank.account_service.client.models.AccountResponse;
import com.skcorp.skbank.account_service.client.models.AccountUploadProofRequest;
import com.skcorp.skbank.account_service.common.GlobalServiceHelper;
import com.skcorp.skbank.account_service.entities.Account;
import com.skcorp.skbank.account_service.entities.AccountLog;
import com.skcorp.skbank.account_service.exceptions.AccountServiceException;
import com.skcorp.skbank.account_service.services.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Base64;

@Service
public class AccountServiceImpl implements AccountService {

    private final GlobalServiceHelper globalServiceHelper;

    @Autowired
    public AccountServiceImpl(GlobalServiceHelper globalServiceHelper) {
        this.globalServiceHelper = globalServiceHelper;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AccountResponse createAccount(AccountRequest accountRequest) {
        AccountResponse response = new AccountResponse();
        AccountPayload payload = new AccountPayload();
        try {
            payload = globalServiceHelper.createNewAccount(accountRequest);
            response.setAccountResponseData(payload);
        } catch (Exception e) {
            throw e;
        }
        return response;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String uploadAccountProof(AccountUploadProofRequest request) {
        String generatedPassword = null;
        try {
            String accountNumber = request.getAccountNumber();
            byte[] file = Base64.getDecoder().decode(request.getFile());
            AccountProofTypeEnum proofTypeEnum = request.getProofType();

            AccountLog accountLog = globalServiceHelper.checkAccountHasValidLog(accountNumber, "INITIATED", false);

            globalServiceHelper.uploadProof(file, proofTypeEnum, accountLog.getAccount());

            Account account = accountLog.getAccount();
            account.setIsActive(true);

            globalServiceHelper.updateAccountLog(account, "ACTIVATED");

            // generate random account password and store it in DB
            generatedPassword = globalServiceHelper.updateAccountSecurity(account, 10);
        } catch (Exception exception) {
            throw new AccountServiceException(exception.getMessage());
        }

        return generatedPassword;
    }

    @Override
    @Transactional(rollbackFor = { Exception.class})
    public void updateAccountSecurity(AccountAuthentication accountAuthentication) {

        Account account = globalServiceHelper.fetchAccountByAccountNumber(accountAuthentication.getAccountNumber());
        String password = accountAuthentication.getPassword();

        if (password.length() >= 10 && password.length() <= 15) {
            globalServiceHelper.updateAccountSecurity(account, password);
        } else {
            throw new AccountServiceException("Password length must between 10 to 15");
        }
    }

}
