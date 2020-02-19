package com.team14.virtualwallet.services;

import com.team14.virtualwallet.models.ConfirmationToken;
import com.team14.virtualwallet.services.contracts.ConfirmationTokensService;
import com.team14.virtualwallet.services.contracts.EmailSenderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import static java.lang.System.lineSeparator;

@Service("emailSenderService")
public class EmailSenderServiceImpl implements EmailSenderService {

    public static final int TOKEN_TYPE_EMAIL = 1;
    public static final int TOKEN_TYPE_TRANSACTION = 2;
    public static final String HELLO = "Hello, %s";
    public static final String TRANSACTION_VERIFICATION_MAIL_BODY = "To approve your transaction, please use following code: ";
    public static final String THANK_YOU = "Thank you";
    public static final String EMAIL_REGISTRATION_MAIL_BODY = "To validate your account, please click on the following : ";
    public static final String EMAIL_REGISTRATION_CONFIRMATION_LINK = "http://localhost:8080/email-confirm?token=%s";
    private static final String VALIDATION_EMAIL_SUBJECT = "Complete Registration!";
    private static final String VERIFY_TRANSACTION = "Verify transaction";
    private static final String APP_VALIDATION_EMAIL_ADDRESS = "examplevirtualwallet@gmail.com";
    private JavaMailSender javaMailSender;
    private ConfirmationTokensService confirmationTokensService;

    @Autowired
    public EmailSenderServiceImpl(JavaMailSender javaMailSender, ConfirmationTokensService confirmationTokensService) {
        this.javaMailSender = javaMailSender;
        this.confirmationTokensService = confirmationTokensService;
    }

    @Async
    public void sendEmailToUser(String username) {
        Integer token_type = TOKEN_TYPE_EMAIL;
        javaMailSender.send(getEmailMessage(username, token_type));
    }

    @Async
    public void sendTransactionCode(String username) {
        Integer token_type = TOKEN_TYPE_TRANSACTION;
        javaMailSender.send(getEmailMessage(username, token_type));
    }

    private SimpleMailMessage getEmailMessage(String username, Integer tokenType) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        ConfirmationToken confirmationToken = getToken(username, tokenType);

        mailMessage.setTo(confirmationToken.getUser().getEmail());
        mailMessage.setFrom(APP_VALIDATION_EMAIL_ADDRESS);

        if (tokenType == TOKEN_TYPE_EMAIL) {
            mailMessage.setSubject(VALIDATION_EMAIL_SUBJECT);
            mailMessage.setText(getEmailMessageRegistration(confirmationToken.getConfirmationToken(), username));
        } else if (tokenType == TOKEN_TYPE_TRANSACTION) {
            mailMessage.setSubject(VERIFY_TRANSACTION);
            mailMessage.setText(getEmailMessageTextLargeTransaction(confirmationToken.getConfirmationToken(), username));
        }
        return mailMessage;
    }

    private ConfirmationToken getToken(String username, Integer tokenType) {
        ConfirmationToken confirmationToken = confirmationTokensService.createToken(username, tokenType);
        return confirmationToken;
    }

    private String getEmailMessageRegistration(String confirmationToken, String username) {
        StringBuilder sb = new StringBuilder();

        sb.append(String.format(HELLO, username)).append(lineSeparator())
                .append(EMAIL_REGISTRATION_MAIL_BODY).append(lineSeparator())
                .append(String.format(EMAIL_REGISTRATION_CONFIRMATION_LINK, confirmationToken)).append(lineSeparator())
                .append(THANK_YOU).append(lineSeparator());

        return sb.toString().trim();
    }

    private String getEmailMessageTextLargeTransaction(String confirmationToken, String username) {
        StringBuilder sb = new StringBuilder();

        sb.append(String.format(HELLO, username)).append(lineSeparator())
                .append(TRANSACTION_VERIFICATION_MAIL_BODY + confirmationToken).append(lineSeparator())
                .append(THANK_YOU).append(lineSeparator());

        return sb.toString().trim();
    }
}