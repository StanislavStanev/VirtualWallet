package com.team14.virtualwallet.controllers;

import com.team14.virtualwallet.exceptions.EntityNotFoundException;
import com.team14.virtualwallet.exceptions.InvalidPasswordException;
import com.team14.virtualwallet.models.dtos.userdto.UserPasswordUpdateDto;
import com.team14.virtualwallet.models.dtos.userdto.UserPersonalDataDto;
import com.team14.virtualwallet.models.dtos.userdto.UserPersonalDataUpdateDto;
import com.team14.virtualwallet.services.contracts.EmailSenderService;
import com.team14.virtualwallet.services.contracts.UsersService;
import com.team14.virtualwallet.utils.ControllerHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;
import java.io.IOException;
import java.security.Principal;

@Controller
@ApiIgnore
@Scope(value = "session", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class UserController {

    private String PICTURE_FAILED_TO_UPLOAD = "File used for Profile picture failed to upload. File must be one of several formats - .jpeg, .jpg, .png. Max size is 15MB.";
    private String EMAIL_VERIFICATION_FAILED = "Something went wrong with the Account Verification process. Please contact us at examplevirtualwallet@gmail.com.";

    private Boolean hasError;
    private String errorMessage;

    private UsersService usersService;
    private EmailSenderService emailSenderService;

    @Autowired
    public UserController(UsersService usersService, EmailSenderService emailSenderService) {
        this.usersService = usersService;
        this.emailSenderService = emailSenderService;
    }

    @GetMapping("/user/profile")
    public ModelAndView userProfile(ModelAndView modelAndView,
                                    Model model,
                                    Principal principal) {
        setupPageErrorData(model);

        UserPersonalDataDto userPersonalDataDto = usersService.getUserPersonalData(principal.getName());

        modelAndView.addObject("error", hasError);
        modelAndView.addObject("errorMessage", errorMessage);
        modelAndView.addObject("isEnabled", userPersonalDataDto.isEnabled());
        modelAndView.addObject("userPersonalData", userPersonalDataDto);
        modelAndView.addObject("userPersonalDataUpdateDto", new UserPersonalDataUpdateDto());
        modelAndView.addObject("userPasswordUpdateDto", new UserPasswordUpdateDto());
        modelAndView.setViewName("profile");

        return modelAndView;
    }

    @PostMapping("/user/profile")
    public ModelAndView userProfileConfirm(@RequestParam(defaultValue = "") String updateType,
                                           @ModelAttribute(name = "userPersonalDataUpdateDto") UserPersonalDataUpdateDto userPersonalDataUpdateDto,
                                           @ModelAttribute(name = "userPasswordUpdateDto") UserPasswordUpdateDto userPasswordUpdateDto,
                                           ModelAndView modelAndView,
                                           RedirectAttributes attr,
                                           Principal principal,
                                           HttpServletRequest request) {
        try {
            //TODO remove logic from controller
            switch (updateType) {
                case "password": usersService.passwordUpdate(userPasswordUpdateDto, principal.getName()); break;
                case "personalInfo": usersService.personalDataUpdate(userPersonalDataUpdateDto, principal.getName()); break;
                case "profilePicture":
                    Part part = request.getPart("picture");
                    usersService.profilePictureUpdate(principal.getName(), (MultipartFile) part.getInputStream());
                    break;
            }
        } catch (EntityNotFoundException | InvalidPasswordException | IllegalArgumentException e) {
            e.printStackTrace();
            ControllerHelper.setErrorDetails(attr, e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
            ControllerHelper.setErrorDetails(attr, PICTURE_FAILED_TO_UPLOAD);
        } catch (Exception e) {
            e.printStackTrace();
        }

        modelAndView.setViewName("redirect:/user/profile");
        return modelAndView;
    }

    @PostMapping("/user/send-verification-email")
    public ModelAndView verificationEmailConfirm(ModelAndView modelAndView, RedirectAttributes attr, Principal principal) {
        try {
            emailSenderService.sendEmailToUser(principal.getName());

        } catch (EntityNotFoundException e) {
            e.printStackTrace();
            ControllerHelper.setErrorDetails(attr, EMAIL_VERIFICATION_FAILED);
        }

        modelAndView.setViewName("redirect:/user/profile");
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