package com.skcorp.skbank.account_service.common;

import com.skcorp.skbank.account_service.client.models.AccountRequest;
import com.skcorp.skbank.account_service.client.models.AccountTypeEnum;
import com.skcorp.skbank.account_service.client.models.AddressUsageEnum;
import com.skcorp.skbank.account_service.client.models.GenderEnum;
import com.skcorp.skbank.account_service.common.dtos.AccountNumberCustomerId;
import com.skcorp.skbank.account_service.entities.projections.AbstractAccountNumber;
import com.skcorp.skbank.account_service.entities.Account;
import com.skcorp.skbank.account_service.entities.AccountLog;
import com.skcorp.skbank.account_service.entities.AccountProof;
import com.skcorp.skbank.account_service.entities.Address;
import com.skcorp.skbank.account_service.entities.BranchNameAddressXref;
import com.skcorp.skbank.account_service.entities.Customer;
import com.skcorp.skbank.account_service.exceptions.AccountServiceException;
import com.skcorp.skbank.account_service.repositories.AccountLogRepository;
import com.skcorp.skbank.account_service.repositories.AccountProofRepository;
import com.skcorp.skbank.account_service.repositories.AccountRepository;
import com.skcorp.skbank.account_service.repositories.BranchNameAddressXrefRepository;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class GlobalServiceHelper {

    private final BranchNameAddressXrefRepository branchNameAddressXrefRepository;
    private final AccountRepository accountRepository;
    private final AccountLogRepository accountLogRepository;

    private final AccountProofRepository accountProofRepository;

    public GlobalServiceHelper(
            BranchNameAddressXrefRepository branchNameAddressXrefRepository,
            AccountRepository accountRepository,
            AccountLogRepository accountLogRepository,
            AccountProofRepository accountProofRepository) {
        this.branchNameAddressXrefRepository = branchNameAddressXrefRepository;
        this.accountRepository = accountRepository;
        this.accountLogRepository = accountLogRepository;
        this.accountProofRepository = accountProofRepository;
    }


    public void validateMobileNumber(String mobileNumber) {
        String regex = "^[0-9]{10}$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(mobileNumber.trim());
        if (!matcher.matches()) {
            throw new AccountServiceException("INVALID_MOBILE_NUMBER", "The provided mobile number is Invalid. It should have only 10 digits and should have numbers only");
        }
    }

    public void validateEmailAddress(String email) {
        String regex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(email);
        if (!matcher.matches()) {
            throw new AccountServiceException("INVALID_EMAIL_ADDRESS", "The provided email address is invalid. The format may invalid");
        }
    }

    public String createNewAccount(AccountRequest accountRequest) {
        try {
            AccountTypeEnum typeEnum = accountRequest.getAccountType();
            String branchName = accountRequest.getBranchName().trim();
            BranchNameAddressXref branchNameAddressXref = branchNameAddressXrefRepository.findByBranchNameIgnoreCase(branchName)
                    .orElseThrow(() -> new AccountServiceException("BRANCH_NAME_NOT_FOUND", "The selected branch name does not exist"));

            com.skcorp.skbank.account_service.client.models.Address customerRequestAddress = accountRequest.getCustomerAddress();
            AddressUsageEnum addressUsageEnum = customerRequestAddress.getUsage();
            Character addressUsage = addressUsageEnum.equals(AddressUsageEnum.BRANCH)
                    ? 'B' : 'C';
            GenderEnum genderEnum = accountRequest.getGender();
            Character gender = genderEnum.equals(GenderEnum.MALE)
                    ? 'M' : genderEnum.equals(GenderEnum.FEMALE)
                    ? 'F' : 'T';
            Address branchAddress = branchNameAddressXref.getBranchAddress();
            Address customerAddress = Address
                    .builder()
                    .doorNo(customerRequestAddress.getDoorNo())
                    .lineOne(customerRequestAddress.getAddressLineOne())
                    .lineTwo(customerRequestAddress.getAddressLineTwo())
                    .city(customerRequestAddress.getCity())
                    .state(customerRequestAddress.getState())
                    .pinCode(customerRequestAddress.getPinCode())
                    .usageFor(addressUsage)
                    .build();

            AbstractAccountNumber abstractAccountNumber = accountLogRepository.findLastCreatedAccountNumber();
            String lastCreatedAccountNumber = null;

            if (abstractAccountNumber != null && StringUtils.hasLength(abstractAccountNumber.getAccountNumber())) {
                lastCreatedAccountNumber = abstractAccountNumber.getAccountNumber();
            }
            LocalDate dob = accountRequest.getDateOfBirth();
            AccountNumberCustomerId accountNumberCustomerId = generateAccountNumberAndCustomerId(lastCreatedAccountNumber);

            Customer customer = Customer.builder()
                    .id(accountNumberCustomerId.getCustomerId())
                    .email(null)
                    .isActive(true)
                    .gender(gender)
                    .dateOfBirth(dob)
                    .firstName(accountRequest.getFirstName())
                    .lastName(accountRequest.getLastName())
                    .mobileNumber(accountRequest.getMobileNumber())
                    .address(customerAddress)
                    .build();

            Account account = Account.builder()
                    .accountNumber(accountNumberCustomerId.getAccountNumber())
                    .type(accountRequest.getAccountType().toString())
                    .customer(customer)
                    .branchAddress(branchAddress)
                    .isActive(false)
                    .build();

            AccountLog accountLog = new AccountLog();
            accountLog.setAccount(account);
            accountLog.setStatus("INITIATED");
            accountLog.setUpdatedAt(LocalDateTime.now());

            AccountLog savedAccount = accountLogRepository.save(accountLog);

            return savedAccount.getAccount().getAccountNumber();
        } catch (AccountServiceException serviceException) {
            throw serviceException;
        } catch (Exception exception) {
            throw exception;
        }
    }

    private AccountNumberCustomerId generateAccountNumberAndCustomerId(String lastGeneratedId) {
        LocalDate date = LocalDate.now();
        String prefix = "SKB";
        String year = String.valueOf(date.getYear());
        String month = String.format("%02d", date.getMonthValue());

        String customerIdString = year + month;

        int nextNumber = 1;

        // Check if the last ID matches the current year and month
        String expectedPrefix = prefix + year + month;
        if (lastGeneratedId != null && lastGeneratedId.startsWith(expectedPrefix)) {
            // Extract the number part from the last ID
            String numberPart = lastGeneratedId.substring(expectedPrefix.length());
            nextNumber = Integer.parseInt(numberPart) + 1;
            customerIdString += nextNumber;
        }

        Long custId = Long.parseLong(customerIdString);

        // Format the next number with zero-padding to 6 digits
        String formattedNumber = String.format("%06d", nextNumber);
        String accNo = expectedPrefix + formattedNumber;

        AccountNumberCustomerId accountNumberCustomerId = new AccountNumberCustomerId();
        accountNumberCustomerId.setAccountNumber(accNo);
        accountNumberCustomerId.setCustomerId(custId);

        return accountNumberCustomerId;
    }

    public AccountLog fetchExistingAccountLog(String accountNumber, String status) {

        AccountLog accountLog = accountLogRepository.findByStatusIgnoreCaseAndAccountAccountNumber(status, accountNumber)
                .orElseThrow(() -> new AccountServiceException("ACCOUNT_NOT_FOUND", "The provided account has not initiated yet."));

        return accountLog;
    }

    public void uploadProof(byte[] form, String type, Account account) {

        try {
            AccountProof accountProof = new AccountProof();
            accountProof.setAccount(account);
            accountProof.setForm(form);
            accountProof.setType(type);

            accountProofRepository.save(accountProof);
        } catch (Exception e) {
            throw new AccountServiceException(e.getMessage());
        }
    }

    public void updateAccountLogs(List<AccountLog> accountLogs) {
        accountLogRepository.saveAll(accountLogs);
    }
}
