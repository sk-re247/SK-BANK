package com.skcorp.skbank.account_service.common.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AccountNumberCustomerId {

    private String accountNumber;

    private Long customerId;
}
