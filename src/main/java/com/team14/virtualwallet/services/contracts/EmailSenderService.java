package com.team14.virtualwallet.services.contracts;

public interface EmailSenderService {

    void sendEmailToUser(String username);

    void sendTransactionCode(String username);
}
