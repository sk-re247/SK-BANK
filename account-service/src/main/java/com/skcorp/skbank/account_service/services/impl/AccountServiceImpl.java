package com.skcorp.skbank.account_service.services.impl;

import com.skcorp.skbank.account_service.common.GlobalServiceHelper;
import com.skcorp.skbank.account_service.common.dtos.AccountRequest;
import com.skcorp.skbank.account_service.common.dtos.AccountResponse;
import com.skcorp.skbank.account_service.common.dtos.Address;
import com.skcorp.skbank.account_service.exceptions.AccountServiceException;
import com.skcorp.skbank.account_service.services.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class AccountServiceImpl implements AccountService {

    private final GlobalServiceHelper globalServiceHelper;

    @Autowired
    public AccountServiceImpl(GlobalServiceHelper globalServiceHelper) {
        this.globalServiceHelper = globalServiceHelper;
    }


    @Override
    public AccountResponse createAccount(AccountRequest accountRequest) {
        AccountResponse response = new AccountResponse();
        String accountNumber = null;
        String errorMessage = "The * must not be a Empty or Nul";
        try {

            if (!StringUtils.hasLength(accountRequest.getLastName())) {
                throw new AccountServiceException("INVALID_LAST_NAME", errorMessage.replace("*", "last name"));
            }

            if (!StringUtils.hasLength(accountRequest.getGender())) {
                throw new AccountServiceException("INVALID_GENDER", errorMessage.replace("*", "gender"));
            }

            if (!StringUtils.hasLength(accountRequest.getBranchName())) {
                throw new AccountServiceException("INVALID_BRANCH_NAME", errorMessage.replace("*", "branch name"));
            }

            if (!StringUtils.hasLength(accountRequest.getAccountType())) {
                throw new AccountServiceException("INVALID_ACCOUNT_TYPE", errorMessage.replace("*", "account type"));
            }

            globalServiceHelper.validateMobileNumber(accountRequest.getMobileNumber());

            if (StringUtils.hasLength(accountRequest.getEmail())) {
                globalServiceHelper.validateEmailAddress(accountRequest.getEmail());
            }

            Address customerAddress = accountRequest.getCustomerAddress();

            if (!StringUtils.hasLength(customerAddress.getDoorNo()) || !StringUtils.hasLength(customerAddress.getAddressLineOne()) ||
                    !StringUtils.hasLength(customerAddress.getCity()) || !StringUtils.hasLength(customerAddress.getState()) ||
                    !StringUtils.hasLength(customerAddress.getUsage()) || customerAddress.getPinCode() == null) {
                throw new AccountServiceException("INVALID_ADDRESS", errorMessage.replace("*", "customer address"));
            }

            accountNumber = globalServiceHelper.createNewAccount(accountRequest);
            response.setAccountNumber(accountNumber);

        } catch (AccountServiceException serviceException) {
            throw serviceException;
        } catch (Exception exception) {
            throw new AccountServiceException(exception.getMessage());
        }

        return response;
    }
}
