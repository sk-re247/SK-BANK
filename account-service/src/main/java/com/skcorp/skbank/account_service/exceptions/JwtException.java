package com.skcorp.skbank.account_service.exceptions;

public class JwtException extends RuntimeException {

    public JwtException(String message) {
        super(message);
    }
}
