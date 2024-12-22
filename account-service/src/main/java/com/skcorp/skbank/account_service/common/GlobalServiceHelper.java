package com.skcorp.skbank.account_service.common;

import com.skcorp.skbank.account_service.client.models.AccountPayload;
import com.skcorp.skbank.account_service.client.models.AccountProofTypeEnum;
import com.skcorp.skbank.account_service.client.models.AccountRequest;
import com.skcorp.skbank.account_service.client.models.AccountTypeEnum;
import com.skcorp.skbank.account_service.client.models.AddressUsageEnum;
import com.skcorp.skbank.account_service.client.models.GenderEnum;
import com.skcorp.skbank.account_service.client.models.JwtPayload;
import com.skcorp.skbank.account_service.common.dtos.AccountNumberCustomerId;
import com.skcorp.skbank.account_service.common.utils.JwtUtil;
import com.skcorp.skbank.account_service.entities.AccountLoginHistory;
import com.skcorp.skbank.account_service.entities.AccountSecurity;
import com.skcorp.skbank.account_service.entities.projections.AbstractAccountNumber;
import com.skcorp.skbank.account_service.entities.Account;
import com.skcorp.skbank.account_service.entities.AccountLog;
import com.skcorp.skbank.account_service.entities.AccountProof;
import com.skcorp.skbank.account_service.entities.Address;
import com.skcorp.skbank.account_service.entities.BranchNameAddressXref;
import com.skcorp.skbank.account_service.entities.Customer;
import com.skcorp.skbank.account_service.exceptions.AccountServiceException;
import com.skcorp.skbank.account_service.repositories.AccountLogRepository;
import com.skcorp.skbank.account_service.repositories.AccountLoginHistoryRepository;
import com.skcorp.skbank.account_service.repositories.AccountProofRepository;
import com.skcorp.skbank.account_service.repositories.AccountRepository;
import com.skcorp.skbank.account_service.repositories.AccountSecurityRepository;
import com.skcorp.skbank.account_service.repositories.BranchNameAddressXrefRepository;
import jakarta.persistence.EntityManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
public class GlobalServiceHelper {

    private final BranchNameAddressXrefRepository branchNameAddressXrefRepository;
    private final AccountRepository accountRepository;
    private final AccountLogRepository accountLogRepository;

    private final AccountProofRepository accountProofRepository;

    private final AccountSecurityRepository accountSecurityRepository;

    private final AccountLoginHistoryRepository accountLoginHistoryRepository;

    private final EntityManager entityManager;

    private final JwtUtil jwtUtil;

    public GlobalServiceHelper(
            BranchNameAddressXrefRepository branchNameAddressXrefRepository,
            AccountRepository accountRepository,
            AccountLogRepository accountLogRepository,
            AccountProofRepository accountProofRepository,
            AccountSecurityRepository accountSecurityRepository,
            JwtUtil jwtUtil,
            AccountLoginHistoryRepository accountLoginHistoryRepository,
            EntityManager entityManager) {
        this.branchNameAddressXrefRepository = branchNameAddressXrefRepository;
        this.accountRepository = accountRepository;
        this.accountLogRepository = accountLogRepository;
        this.accountProofRepository = accountProofRepository;
        this.accountSecurityRepository = accountSecurityRepository;
        this.jwtUtil = jwtUtil;
        this.accountLoginHistoryRepository = accountLoginHistoryRepository;
        this.entityManager = entityManager;
    }


//    public void validateMobileNumber(String mobileNumber) {
//        String regex = "^[0-9]{10}$";
//        Pattern pattern = Pattern.compile(regex);
//        Matcher matcher = pattern.matcher(mobileNumber.trim());
//        if (!matcher.matches()) {
//            throw new AccountServiceException("INVALID_MOBILE_NUMBER", "The provided mobile number is Invalid. It should have only 10 digits and should have numbers only");
//        }
//    }
//
//    public void validateEmailAddress(String email) {
//        String regex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
//        Pattern pattern = Pattern.compile(regex);
//        Matcher matcher = pattern.matcher(email);
//        if (!matcher.matches()) {
//            throw new AccountServiceException("INVALID_EMAIL_ADDRESS", "The provided email address is invalid. The format may invalid");
//        }
//    }

    public AccountPayload createNewAccount(AccountRequest accountRequest) {
        AccountPayload payload = new AccountPayload();
        try {
            AccountTypeEnum typeEnum = accountRequest.getAccountType();
            String branchName = accountRequest.getBranchName().trim();
            BranchNameAddressXref branchNameAddressXref = branchNameAddressXrefRepository.findByBranchNameIgnoreCase(branchName)
                    .orElseThrow(() -> new AccountServiceException("The selected branch name does not exist"));

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

            String savedAccNo = savedAccount.getAccount().getAccountNumber();
            Long custId = savedAccount.getAccount().getCustomer().getId();
            payload.setCustId(custId);
            payload.setAccountNumber(savedAccNo);

            JwtPayload jwtPayload = new JwtPayload();
            jwtPayload.setAccountNumber(savedAccNo);
            jwtPayload.setAccountType(savedAccount.getAccount().getType());
            jwtPayload.setCustId(custId);
            jwtPayload.setLastName(savedAccount.getAccount().getCustomer().getLastName());
            jwtPayload.setMobileNumber(savedAccount.getAccount().getCustomer().getMobileNumber());

            String token = jwtUtil.generateToken(jwtPayload);
            payload.setAccessToken(token);
        } catch (AccountServiceException serviceException) {
            throw serviceException;
        } catch (Exception exception) {
            throw exception;
        }
        return payload;
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

    public AccountLog checkAccountHasValidLog(String accountNumber, String status, boolean isActive) {
        String error = "The Provided * account number is not found";
        AccountLog accountLog = accountLogRepository.findByStatusIgnoreCaseAndAccountAccountNumberAndAccountIsActive(status, accountNumber, isActive)
                .orElseThrow(() -> new AccountServiceException(error.replace("*", accountNumber)));
        return accountLog;
    }

    public void uploadProof(byte[] file, AccountProofTypeEnum proofTypeEnum, Account account) {

        try {
            AccountProof accountProof = new AccountProof();
            accountProof.setAccount(account);
            accountProof.setForm(file);
            accountProof.setType(proofTypeEnum.toString());

            accountProofRepository.save(accountProof);
        } catch (Exception e) {
            throw new AccountServiceException(e.getMessage());
        }
    }

    public void updateAccountLog(Account account, String status) {

        AccountLog updatedAccountLog = new AccountLog();
        updatedAccountLog.setAccount(account);
        updatedAccountLog.setStatus(status);
        updatedAccountLog.setUpdatedAt(LocalDateTime.now());

        accountLogRepository.save(updatedAccountLog);
    }

    private String generateRandomPassword(int length) {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()-_=+";
        SecureRandom secureRandom = new SecureRandom();
        StringBuilder password = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int index = secureRandom.nextInt(characters.length());
            password.append(characters.charAt(index));
        }

        return password.toString();
    }

    public String updateAccountSecurity(Account account, int passwordLength) {

        AccountSecurity accountSecurity = new AccountSecurity();
        accountSecurity.setAccount(account);
        String password = generateRandomPassword(passwordLength);

        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encryptedPassword = passwordEncoder.encode(password);
        accountSecurity.setPassword(encryptedPassword);

        accountSecurityRepository.save(accountSecurity);

        return password;
    }

    public String updateAccountSecurity(Account account, String password) {

        AccountSecurity accountSecurity = new AccountSecurity();
        accountSecurity.setAccount(account);

        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encryptedPassword = passwordEncoder.encode(password);
        accountSecurity.setPassword(encryptedPassword);

        accountSecurityRepository.save(accountSecurity);

        return password;
    }

    public Account fetchAccountByAccountNumber(String accountNumber) {

        return accountRepository.findByAccountNumber(accountNumber).orElseThrow(() -> new AccountServiceException("Account Not Found"));
    }

    @Transactional
    public void addAccountLoginEntry(Account account) {

        try {
            Account attachedAccount = entityManager.merge(account);
            AccountLoginHistory loginHistory = new AccountLoginHistory();
            loginHistory.setAccount(attachedAccount);
            accountLoginHistoryRepository.save(loginHistory);
        } catch (Exception exception) {
            System.out.println(exception);
            // do nothing
        }
    }
}
