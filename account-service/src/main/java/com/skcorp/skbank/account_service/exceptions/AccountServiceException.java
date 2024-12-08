package com.skcorp.skbank.account_service.exceptions;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class AccountServiceException extends RuntimeException {
    public AccountServiceException(String message) {
        super(message);
    }
}
