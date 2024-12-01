package com.skcorp.skbank.account_service.exceptions;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class AccountServiceException extends RuntimeException {

    public String code;

    public AccountServiceException(String message) {
        super(message);
    }

    public AccountServiceException(String code, String message) {
        super(message);
        this.code = code;
    }
}
