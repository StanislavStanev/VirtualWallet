package com.team14.virtualwallet.controllers;

import com.team14.virtualwallet.exceptions.EntityNotFoundException;
import com.team14.virtualwallet.services.contracts.ConfirmationTokensService;
import com.team14.virtualwallet.services.contracts.EmailSenderService;
import com.team14.virtualwallet.services.contracts.UsersService;
import com.team14.virtualwallet.utils.ControllerHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import springfox.documentation.annotations.ApiIgnore;

import java.security.Principal;

@Controller
@ApiIgnore
public class VerificationController {

    private String EMAIL_VERIFICATION_FAILED = "Something went wrong with the Account Verification process. Please contact us at examplevirtualwallet@gmail.com.";
    private String TRANSACTION_VERIFICATION_FAILED = "Something went wrong with the Large Transaction Verification process. Please contact us at examplevirtualwallet@gmail.com.";

    private boolean dataHasError;
    private String errorMessage;

    private UsersService usersService;
    private EmailSenderService emailSenderService;
    private ConfirmationTokensService confirmationTokensService;

    @Autowired
    public VerificationController(UsersService usersService, EmailSenderService emailSenderService, ConfirmationTokensService confirmationTokensService) {
        this.usersService = usersService;
        this.emailSenderService = emailSenderService;
        this.confirmationTokensService = confirmationTokensService;
    }

    @GetMapping("/email-confirm")
    public ModelAndView verification(@RequestParam String token, ModelAndView modelAndView) {
        modelAndView.addObject("token", token);
        modelAndView.setViewName("email-confirm");
        return modelAndView;
    }

    @GetMapping("/verification")
    public ModelAndView verificationConfirm(@RequestParam String token, ModelAndView modelAndView) {
        try {
            confirmationTokensService.validateEmailToken(token);
        } catch (EntityNotFoundException | IllegalArgumentException e) {
            setErrorMessageData(true, e.getMessage());
        }

        modelAndView.setViewName("redirect:/verified");
        return modelAndView;
    }

    @GetMapping("/verified")
    public ModelAndView accountVerified(ModelAndView modelAndView) {
        modelAndView.setViewName("email-verified");
        return modelAndView;
    }

    private void setErrorMessageData(boolean dataHasError, String errorMessage) {
        this.dataHasError = dataHasError;
        this.errorMessage = errorMessage;
    }

    @PostMapping("/newtransaction/send-verification-email")
    public void largeTransactionVerificationEmailCodeSend(RedirectAttributes attr, Principal principal) {
        try {
            emailSenderService.sendTransactionCode(principal.getName());
        } catch (MailException e) {
            e.printStackTrace();
            ControllerHelper.setErrorDetails(attr, TRANSACTION_VERIFICATION_FAILED);

        }
    }
}
