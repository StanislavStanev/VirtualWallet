package com.team14.virtualwallet.exceptions;


public class CommunicationErrorException extends RuntimeException {
    public CommunicationErrorException(String message) {
        super(message);
    }
}

