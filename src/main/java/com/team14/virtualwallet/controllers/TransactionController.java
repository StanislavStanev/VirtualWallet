package com.team14.virtualwallet.controllers;

import com.team14.virtualwallet.controllers.rest.constants.TransactionControllerConstants;
import com.team14.virtualwallet.exceptions.*;
import com.team14.virtualwallet.models.User;
import com.team14.virtualwallet.models.dtos.SearchByDto;
import com.team14.virtualwallet.models.dtos.apitopupdto.TopUpExtendedDto;
import com.team14.virtualwallet.models.dtos.transactionDtos.TransactionCreateDto;
import com.team14.virtualwallet.models.dtos.transactionDtos.TransactionPageDto;
import com.team14.virtualwallet.models.dtos.userdto.UserWalletDto;
import com.team14.virtualwallet.services.contracts.*;
import com.team14.virtualwallet.utils.ControllerHelper;
import com.team14.virtualwallet.utils.ModelFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import springfox.documentation.annotations.ApiIgnore;

import java.security.Principal;
import java.util.List;

@Controller
@ApiIgnore
@Scope(value = "session", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class TransactionController {

    private TransactionsService transactionsService;
    private UserProfilesService userProfilesService;
    private BankCardsService bankCardsService;
    private UsersService usersService;
    private EmailSenderService emailSenderService;
    private Boolean hasSuccess;
    private String successMessage;

    private Boolean hasError;
    private String errorMessage;


    @Autowired

    public TransactionController(TransactionsService transactionsService, UserProfilesService userProfilesService, BankCardsService bankCardsService, UsersService usersService, EmailSenderService emailSenderService) {
        this.transactionsService = transactionsService;
        this.userProfilesService = userProfilesService;
        this.bankCardsService = bankCardsService;
        this.usersService = usersService;
        this.emailSenderService = emailSenderService;
    }

    @GetMapping("/newtransaction/topup")
    public ModelAndView topUpGet(ModelAndView modelAndView, Model model, Principal principal) {
        setupPageErrorData(model);
        setupPageSuccessData(model);

        try {
            bankCardsService.hasRegisteredCards(principal.getName());
        } catch (EntityNotFoundException e) {
            e.printStackTrace();
            setErrorMessageData(e.getMessage(), true);
        }

        User user = usersService.findByUsername(principal.getName());
        List<UserWalletDto> userWalletDtoList = userProfilesService.getUserProfileWalletsList(principal.getName());

        modelAndView.setViewName("topup");
        modelAndView.addObject("hasError", hasError);
        modelAndView.addObject("errorMessage", errorMessage);
        modelAndView.addObject("hasSuccess", hasSuccess);
        modelAndView.addObject("successMessage", successMessage);
        modelAndView.addObject("wallets", userWalletDtoList);
        modelAndView.addObject("isBlocked", user.isBlocked());
        modelAndView.addObject("topUpDto", new TopUpExtendedDto());
        modelAndView.addObject("balance", userProfilesService.getWalletBalance(principal.getName()));
        return modelAndView;
    }

    @PostMapping("/newtransaction/topup")
    public ModelAndView topUpPost(@ModelAttribute(name = "topUpDto") TopUpExtendedDto topUpDto, ModelAndView modelAndView, RedirectAttributes attr, Principal principal) {
        try {
            transactionsService.topUpUserWallet(topUpDto, principal.getName());
            ControllerHelper.setSuccessDetails(attr, TransactionControllerConstants.TOPUP_SUCCESS);
        } catch (NotSufficientFundsException | CommunicationErrorException | EntityNotFoundException | AccessDeniedException e) {
            e.printStackTrace();
            ControllerHelper.setErrorDetails(attr, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }

        modelAndView.setViewName("redirect:/newtransaction/topup");
        return modelAndView;
    }

    @GetMapping("/user/transactions")
    public ModelAndView transactions(@RequestParam(defaultValue = "0") int page,
                                     @RequestParam(defaultValue = "", required = false) String from,
                                     @RequestParam(defaultValue = "", required = false) String to,
                                     @RequestParam(defaultValue = "", required = false) String sender,
                                     @RequestParam(defaultValue = "", required = false) String recipient,
                                     @RequestParam(defaultValue = "ALL", required = false) String type,
                                     @RequestParam(defaultValue = "id", required = false) String sort,
                                     ModelAndView modelAndView,
                                     Model model,
                                     RedirectAttributes attr,
                                     Principal principal) {
        setupPageErrorData(model);

        try {
            TransactionPageDto transactionPageDto = this.transactionsService.getTransactionPage(page, from, to, sender, recipient, type, principal.getName(), sort);

            modelAndView.addObject("transactionPageDto", transactionPageDto);
            modelAndView.addObject("transactions", transactionPageDto.getTransactions());
            modelAndView.addObject("searchByDto", new SearchByDto());

        } catch (EntityNotFoundException | IllegalArgumentException e) {
            ControllerHelper.setErrorDetails(attr, e.getMessage());
            e.printStackTrace();
            modelAndView.setViewName("redirect:/user/transactions");
            return modelAndView;
        }

        modelAndView.setViewName("transactions");
        return modelAndView;
    }

    @PostMapping("/user/transactions")
    public ModelAndView transactionsConfirm(@ModelAttribute SearchByDto searchByDto, ModelAndView modelAndView) {
        modelAndView.setViewName("redirect:/user/transactions" + ControllerHelper.fillQueryString(searchByDto));
        return modelAndView;
    }

    @GetMapping("/newtransaction/sendmoney")
    public ModelAndView sendMoneyGet(ModelAndView modelAndView, Model model, Principal principal) {
        User user = usersService.findByUsername(principal.getName());

        if (user.isBlocked()) {
            modelAndView.setViewName("blockedUser");
            return modelAndView;
        }

        setupPageErrorData(model);
        setupPageSuccessData(model);

        List<UserWalletDto> userWalletDtoList = userProfilesService.getUserProfileWalletsList(principal.getName());


        modelAndView.addObject("hasError", hasError);
        modelAndView.addObject("errorMessage", errorMessage);
        modelAndView.addObject("hasSuccess", hasSuccess);
        modelAndView.addObject("successMessage", successMessage);
        modelAndView.addObject("wallets", userWalletDtoList);
        modelAndView.addObject("balance", userProfilesService.getWalletBalance(principal.getName()));
        modelAndView.addObject("transactionCreateDto", ModelFactory.transactionCreateDto());
        modelAndView.addObject("isBlocked", user.isBlocked());

        modelAndView.setViewName("sendmoney");
        return modelAndView;
    }

    @PostMapping("/newtransaction/sendmoney")
    public ModelAndView sendMoneyPost(@ModelAttribute(name = "transactionCreateDto") TransactionCreateDto transactionCreateDto, ModelAndView modelAndView, RedirectAttributes attr, Principal principal) {
        transactionCreateDto.setSenderUserName(principal.getName());
        try {
            transactionsService.preCreate(transactionCreateDto);
            ControllerHelper.setSuccessDetails(attr, TransactionControllerConstants.TRANSACTION_SUCCESS);
        } catch (NotSufficientFundsException | EntityNotFoundException | DuplicateEntityException | AccessDeniedException | IllegalArgumentException e) {
            ControllerHelper.setErrorDetails(attr, e.getMessage());
        }

        modelAndView.setViewName("redirect:/newtransaction/sendmoney");
        return modelAndView;
    }

    private void setupPageErrorData(Model model) {
        if (model.containsAttribute("error")) {
            setErrorMessageData((String) model.getAttribute("errorText"), true);
        } else {
            setErrorMessageData("", false);
        }
    }

    private void setupPageSuccessData(Model model) {
        if (model.containsAttribute("success")) {
            setSuccessMessageData((String) model.getAttribute("successText"), true);
        } else {
            setSuccessMessageData("", false);
        }
    }

    private void setErrorMessageData(String errorMessage, boolean hasError) {
        this.errorMessage = errorMessage;
        this.hasError = hasError;
    }

    private void setSuccessMessageData(String successMessage, boolean hasSuccess) {
        this.successMessage = successMessage;
        this.hasSuccess = hasSuccess;
    }
}