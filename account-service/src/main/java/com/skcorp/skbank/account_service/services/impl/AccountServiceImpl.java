package com.skcorp.skbank.account_service.services.impl;

import com.skcorp.skbank.account_service.client.models.AccountPayload;
import com.skcorp.skbank.account_service.client.models.AccountRequest;
import com.skcorp.skbank.account_service.client.models.AccountResponse;
import com.skcorp.skbank.account_service.common.GlobalServiceHelper;
import com.skcorp.skbank.account_service.services.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        String accountNumber = null;
        try {
            accountNumber = globalServiceHelper.createNewAccount(accountRequest);
            payload.setAccountNumber(accountNumber);
            response.setAccountResponseData(payload);
        } catch (Exception e) {
            throw e;
        }
        return response;
    }

//    @Override
//    @Transactional(rollbackFor = Exception.class)
//    public void uploadAccountProof(AccountProofRequest request) {
//
//        try {
//            String accountNumber = request.getAccountNumber();
//            AccountLog accountLog = globalServiceHelper.fetchExistingAccountLog(accountNumber, "INITIATED");
//
//            byte[] form = Base64.getDecoder().decode(request.getForm());
//
//            Account account = accountLog.getAccount();
//            account.setIsActive(true);
//
//            globalServiceHelper.uploadProof(form, request.getType(), account);
//
//            AccountLog newUpdatedLog = new AccountLog();
//            newUpdatedLog.setAccount(account);
//            newUpdatedLog.setUpdatedAt(LocalDateTime.now());
//            newUpdatedLog.setStatus("ACTIVATED");
//
//            List<AccountLog> accountLogs = Arrays.asList(accountLog, newUpdatedLog);
//
//            globalServiceHelper.updateAccountLogs(accountLogs);
//        } catch (AccountServiceException serviceException) {
//            throw serviceException;
//        } catch (Exception exception) {
//            throw new AccountServiceException("Something went wrong");
//        }
//
//    }
}
