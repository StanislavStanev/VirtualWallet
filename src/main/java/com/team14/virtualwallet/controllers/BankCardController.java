package com.team14.virtualwallet.controllers;

import com.team14.virtualwallet.controllers.rest.constants.BankCardControllerConstants;
import com.team14.virtualwallet.exceptions.DuplicateEntityException;
import com.team14.virtualwallet.exceptions.EntityNotFoundException;
import com.team14.virtualwallet.models.BankCard;
import com.team14.virtualwallet.models.User;
import com.team14.virtualwallet.models.dtos.bankcarddto.BankCardDeleteDto;
import com.team14.virtualwallet.models.dtos.bankcarddto.BankCardRegisterDto;
import com.team14.virtualwallet.models.dtos.bankcarddto.BankCardUpdateDto;
import com.team14.virtualwallet.services.contracts.BankCardsService;
import com.team14.virtualwallet.services.contracts.UsersService;
import com.team14.virtualwallet.utils.ControllerHelper;
import com.team14.virtualwallet.utils.ModelFactory;
import com.team14.virtualwallet.utils.mappers.BankCardMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@ApiIgnore
@Scope(value = "session", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class BankCardController {

    private BankCardsService bankCardsService;
    private UsersService usersService;

    private Boolean hasSuccess;
    private String successMessage;

    private Boolean hasError;
    private String errorMessage;

    @Autowired
    public BankCardController(BankCardsService bankCardsService, UsersService usersService) {
        this.bankCardsService = bankCardsService;
        this.usersService = usersService;
    }

    @GetMapping("/user/bankcard")
    public ModelAndView bankCardGet(ModelAndView modelAndView, Model model, Principal principal) {
        User user = usersService.findByUsername(principal.getName());
        BankCard bankCard = bankCardsService.getActiveCard(principal.getName());

        setupPageErrorData(model);
        setupPageSuccessData(model);

        modelAndView.setViewName("bankcard");
        modelAndView.addObject("hasError", hasError);
        modelAndView.addObject("errorMessage", errorMessage);
        modelAndView.addObject("hasSuccess", hasSuccess);
        modelAndView.addObject("successMessage", successMessage);
        modelAndView.addObject("bankCard", BankCardMapper.BankCardToGetDto(bankCard));
        modelAndView.addObject("bankCardRegisterDto", ModelFactory.bankCardRegisterDto());
        modelAndView.addObject("bankCardUpdateDto", ModelFactory.bankCardUpdateDto());
        modelAndView.addObject("isBlocked", user.isBlocked());
        modelAndView.addObject("isEnabled", user.isEnabled());
        modelAndView.addObject("hasActiveCard", bankCard != null);
        return modelAndView;
    }

    @PostMapping("/user/bankcard/register")
    public ModelAndView bankCardRegister(@Valid @ModelAttribute(name = "bankCardRegisterDto") BankCardRegisterDto bankCardRegisterDto, BindingResult bindingResult, RedirectAttributes attr, ModelAndView modelAndView, Principal principal) {
        if (bindingResult.hasErrors()) {
            ControllerHelper.setErrorDetails(attr, ControllerHelper.generateFieldErrorsMessage(bindingResult.getFieldErrors()));
            modelAndView.setViewName("redirect:/user/bankcard");
            return modelAndView;
        }

        try {
            bankCardsService.register(bankCardRegisterDto, principal.getName());
            ControllerHelper.setSuccessDetails(attr, BankCardControllerConstants.BANKCARD_REGISTER_SUCCESS);
        } catch (DuplicateEntityException | IllegalArgumentException e) {
            e.printStackTrace();
            ControllerHelper.setErrorDetails(attr, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }

        modelAndView.setViewName("redirect:/user/bankcard");
        return modelAndView;
    }

    @PostMapping("/user/bankcard/update")
    public ModelAndView bankCardUpdate(@Valid @ModelAttribute(name = "bankCardUpdateDto") BankCardUpdateDto bankCardUpdateDto, BindingResult bindingResult, RedirectAttributes attr, ModelAndView modelAndView, Principal principal) {
        if (bindingResult.hasErrors()) {
            ControllerHelper.setErrorDetails(attr, ControllerHelper.generateFieldErrorsMessage(bindingResult.getFieldErrors()));
            modelAndView.setViewName("redirect:/user/bankcard");
            return modelAndView;
        }

        try {
            bankCardsService.update(bankCardUpdateDto, principal.getName());
            ControllerHelper.setSuccessDetails(attr, BankCardControllerConstants.BANKCARD_UPDATE_SUCCESS);
        } catch (DuplicateEntityException e) {
            e.printStackTrace();
            ControllerHelper.setErrorDetails(attr, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }

        modelAndView.setViewName("redirect:/user/bankcard");
        return modelAndView;
    }

    @GetMapping("/user/bankcard/delete")
    public ModelAndView bankCardDelete(BankCardDeleteDto bankCardDeleteDto, RedirectAttributes attr, ModelAndView modelAndView, Principal principal) {
        try {
            bankCardsService.delete(bankCardDeleteDto, principal.getName());
            ControllerHelper.setSuccessDetails(attr, BankCardControllerConstants.BANKCARD_DELETE_SUCCESS);
        } catch (DuplicateEntityException | EntityNotFoundException e) {
            ControllerHelper.setErrorDetails(attr, e.getMessage());
        }

        modelAndView.setViewName("redirect:/user/bankcard");
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
