package com.team14.virtualwallet.controllers;


import com.team14.virtualwallet.exceptions.DuplicateEntityException;
import com.team14.virtualwallet.exceptions.InvalidPasswordException;
import com.team14.virtualwallet.models.User;
import com.team14.virtualwallet.models.UserProfile;
import com.team14.virtualwallet.models.dtos.transactionDtos.TransactionPresentDto;
import com.team14.virtualwallet.models.dtos.userdto.UserPersonalDataDto;
import com.team14.virtualwallet.models.dtos.userdto.UserRegisterDto;
import com.team14.virtualwallet.services.contracts.TransactionsService;
import com.team14.virtualwallet.services.contracts.UserProfilesService;
import com.team14.virtualwallet.services.contracts.UsersService;
import com.team14.virtualwallet.services.contracts.WalletsService;
import com.team14.virtualwallet.utils.ControllerHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;

@Controller
@ApiIgnore
public class HomeController {

    private UsersService usersService;
    private UserProfilesService userProfilesService;
    private WalletsService walletsService;
    private TransactionsService transactionsService;

    private String errorMessage;
    private Boolean hasError;

    @Autowired
    public HomeController(UsersService usersService, UserProfilesService userProfilesService, WalletsService walletsService, TransactionsService transactionsService) {
        this.usersService = usersService;
        this.userProfilesService = userProfilesService;
        this.walletsService = walletsService;
        this.transactionsService = transactionsService;
    }

    @GetMapping("/")
    public ModelAndView index(ModelAndView modelAndView) {
        modelAndView.setViewName("index");
        return modelAndView;
    }

    @GetMapping("/login")
    public ModelAndView login(ModelAndView modelAndView) {
        modelAndView.setViewName("login");
        return modelAndView;
    }

    @GetMapping("/home")
    public ModelAndView home(ModelAndView modelAndView, Principal principal) {
        boolean isAdmin = usersService.userIsAdmin(principal.getName());

        if (isAdmin) {
            modelAndView.setViewName("redirect:/admin/home");
            return modelAndView;
        }

        User user = usersService.findByUsername(principal.getName());
        UserProfile userProfile = userProfilesService.findByUser(user);

        BigDecimal walletBalance = walletsService.getDefault(userProfile).getBalance();
        List<TransactionPresentDto> transactions = transactionsService.getOldestTransactionsByCount(principal.getName(), "ALL", false, 3);

        modelAndView.addObject("transactions", transactions);
        modelAndView.addObject("walletBalance", walletBalance);
        modelAndView.setViewName("home");

        return modelAndView;
    }

    @GetMapping("/admin/home")
    public ModelAndView homeAdmin(ModelAndView modelAndView, Principal principal) {
        List<TransactionPresentDto> transactions = transactionsService.getOldestTransactionsByCount(principal.getName(), "ALL", false, 3);
        List<UserPersonalDataDto> oldestUsers = usersService.findFiveOldest();
        BigDecimal walletBalance = walletsService.getDefault(userProfilesService.findByUser(usersService.findByUsername(principal.getName()))).getBalance();

        modelAndView.addObject("oldestUsers", oldestUsers);
        modelAndView.addObject("transactions", transactions);
        modelAndView.addObject("walletBalance", walletBalance);
        modelAndView.setViewName("admin-home");
        return modelAndView;
    }

    @GetMapping("/register")
    public ModelAndView register(ModelAndView modelAndView, Model model) {
        setupPageErrorData(model);

        modelAndView.setViewName("register");
        modelAndView.addObject("hasError", hasError);
        modelAndView.addObject("errorMessage", errorMessage);
        modelAndView.addObject("registerUserDto", new UserRegisterDto());
        return modelAndView;
    }

    @PostMapping("/register")
    public ModelAndView registerUser(@Valid @ModelAttribute UserRegisterDto registerUserDto, BindingResult bindingResult, RedirectAttributes attr, ModelAndView modelAndView) {
        if (bindingResult.hasErrors()) {
            ControllerHelper.setErrorDetails(attr, ControllerHelper.generateFieldErrorsMessage(bindingResult.getFieldErrors()));
            modelAndView.setViewName("redirect:/register");
            return modelAndView;
        }

        try {
            usersService.register(registerUserDto);
        } catch (DuplicateEntityException | IllegalArgumentException | InvalidPasswordException e) {
            e.printStackTrace();
            ControllerHelper.setErrorDetails(attr, e.getMessage());
            modelAndView.setViewName("redirect:/register");
            return modelAndView;
        } catch (Exception e) {
            e.printStackTrace();
        }

        modelAndView.setViewName("redirect:/login");
        return modelAndView;
    }

    @GetMapping("/error")
    public ModelAndView error(ModelAndView modelAndView) {
        modelAndView.setViewName("error");
        return modelAndView;
    }

    @GetMapping("/privacy-policy")
    public ModelAndView privacyPolicy(ModelAndView modelAndView) {
        modelAndView.setViewName("regular-page.html");
        return modelAndView;
    }

    @GetMapping("/terms")
    public ModelAndView terms(ModelAndView modelAndView) {
        modelAndView.setViewName("regular-page-v2.html");
        return modelAndView;
    }

    private void setupPageErrorData(Model model) {
        if (model.containsAttribute("error")) {
            setErrorMessageData((String) model.getAttribute("errorText"), true);
        } else {
            setErrorMessageData("", false);
        }
    }

    private void setErrorMessageData(String errorMessage, boolean hasError) {
        this.errorMessage = errorMessage;
        this.hasError = hasError;
    }
}