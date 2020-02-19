package com.team14.virtualwallet.exceptions;

public class NotSufficientFundsException extends RuntimeException {
    public NotSufficientFundsException(String message) {
        super(message);
    }
}