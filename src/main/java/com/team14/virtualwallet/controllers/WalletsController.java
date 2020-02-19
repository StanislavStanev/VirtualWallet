package com.team14.virtualwallet.controllers;

import com.team14.virtualwallet.exceptions.DuplicateEntityException;
import com.team14.virtualwallet.models.User;
import com.team14.virtualwallet.models.UserProfile;
import com.team14.virtualwallet.models.dtos.userdto.UserWalletDto;
import com.team14.virtualwallet.models.dtos.walletDto.SharedWalletUsersListDto;
import com.team14.virtualwallet.services.contracts.UserProfilesService;
import com.team14.virtualwallet.services.contracts.UsersService;
import com.team14.virtualwallet.services.contracts.WalletsService;
import com.team14.virtualwallet.utils.ControllerHelper;
import com.team14.virtualwallet.utils.ModelFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import springfox.documentation.annotations.ApiIgnore;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@ApiIgnore
public class WalletsController {

    private Boolean hasError;
    private String errorMessage;

    private UsersService usersService;
    private UserProfilesService userProfilesService;
    private WalletsService walletsService;
    @Autowired
    public WalletsController(UsersService usersService, UserProfilesService userProfilesService, WalletsService walletsService) {
        this.usersService = usersService;
        this.userProfilesService = userProfilesService;
        this.walletsService = walletsService;
    }

    @GetMapping("/user/wallets/walletCreate")
    public ModelAndView walletCreateGet(ModelAndView modelAndView,
                                        Model model,
                                        Principal principal) {
        User user = usersService.findByUsername(principal.getName());

        setupPageErrorData(model);

        modelAndView.addObject("hasError", hasError);
        modelAndView.addObject("errorMessage", errorMessage);
        modelAndView.addObject("isBlocked", user.isBlocked());
        modelAndView.addObject("userWalletDto", ModelFactory.createUserWalletDto());
        modelAndView.setViewName("walletcreate");
        return modelAndView;
    }

    @PostMapping("/user/wallets/walletCreate")
    public ModelAndView walletCreatePost(@ModelAttribute(name = "userWalletDto") UserWalletDto userWalletDto,
                                         ModelAndView modelAndView,
                                         RedirectAttributes attr,
                                         Principal principal) {
        User user = usersService.findByUsername(principal.getName());
        UserProfile userProfile = userProfilesService.findByUser(user);

        try {
            walletsService.create(userProfile, userWalletDto.getWalletName(), userWalletDto.getDefaultWallet(), userWalletDto.getSharedWallet());
        } catch (DuplicateEntityException e) {
            e.printStackTrace();
            ControllerHelper.setErrorDetails(attr, e.getMessage());
            modelAndView.setViewName("walletcreate");
            return modelAndView;
        }
        modelAndView.setViewName("redirect:/user/wallets/walletslist");
        return modelAndView;
    }

    @GetMapping("/user/wallets/walletslist")
    public ModelAndView walletListGet(ModelAndView modelAndView,
                                      Principal principal,
                                      @RequestParam(value = "walletname", defaultValue = "", required = false) String walletname) {
        modelAndView.setViewName("walletslist");
        User user = usersService.findByUsername(principal.getName());
        UserProfile userProfile = userProfilesService.findByUser(user);
        List<UserWalletDto> userWalletDtoList = userProfilesService.getUserProfileWalletsList(principal.getName());

        if (walletname.length() > 0) {
            walletsService.setDefault(userProfile, walletname);
            modelAndView.setViewName("redirect:/user/wallets/walletslist/");
            return modelAndView;
        }

        modelAndView.addObject("wallets", userWalletDtoList);
        modelAndView.addObject("isBlocked", user.isBlocked());
        return modelAndView;
    }

    @GetMapping("/user/wallets/walletslist/sharedlist/{walletId}")
    public ModelAndView sharedWalletUsersListGet(@PathVariable Long walletId,
                                                 ModelAndView modelAndView,
                                                 Principal principal) {
        modelAndView.setViewName("sharedlist");
        User user = usersService.findByUsername(principal.getName());
        UserProfile userProfile = userProfilesService.findByUser(user);
        //New walletService method for data delivery
        List<SharedWalletUsersListDto> sharedWalletUsersListDto = walletsService.getAllUsersInSharedWallet(walletId);
        modelAndView.addObject("users", sharedWalletUsersListDto);
        modelAndView.addObject("isBlocked", user.isBlocked());
        return modelAndView;
    }

    @GetMapping("/user/wallets/walletslist/sharedlist/delete")
    public ModelAndView sharedWalletUsersListPost(ModelAndView modelAndView,
                                                  @RequestParam(value = "walletId", defaultValue = "", required = false) String walletId,
                                                  @RequestParam(value = "user", defaultValue = "", required = false) String user) {
        modelAndView.setViewName("sharedlist");
        User member = usersService.findByUsername(user);
        UserProfile memberUserProfile = userProfilesService.findByUser(member);
        walletsService.removeMember(memberUserProfile, Long.parseLong(walletId));
        modelAndView.setViewName("redirect:/user/wallets/walletslist/sharedlist/" + walletId);
        return modelAndView;
    }

    @GetMapping("/user/wallets/walletslist/sharedlist/update")
    public ModelAndView sharedWalletUsersListAddRole(ModelAndView modelAndView,
                                                     @RequestParam(value = "walletId", defaultValue = "", required = false) String walletId,
                                                     @RequestParam(value = "user", defaultValue = "", required = false) String user,
                                                     @RequestParam(value = "roleId", defaultValue = "", required = false) String roleId) {
        modelAndView.setViewName("sharedlist");
        User member = usersService.findByUsername(user);
        UserProfile memberUserProfile = userProfilesService.findByUser(member);
        walletsService.updateSharedWalletRole(memberUserProfile.getId(), Long.parseLong(walletId), Long.parseLong(roleId));
        modelAndView.setViewName("redirect:/user/wallets/walletslist/sharedlist/" + walletId);
        return modelAndView;
    }

    @GetMapping("/user/wallets/walletslist/addmembers")
    public ModelAndView addMembersToSharedWalletListGet(ModelAndView modelAndView,
                                                        Principal principal) {
        User user = usersService.findByUsername(principal.getName());
        UserProfile userProfile = userProfilesService.findByUser(user);
        modelAndView.setViewName("sharedwalletaddmembers");
        List<UserWalletDto> userWalletDtoList = userProfilesService.getUserProfileWalletsList(principal.getName())
                .stream()
                .filter(w -> w.getShared())
                .collect(Collectors.toList());
        modelAndView.addObject("wallets", userWalletDtoList);
        modelAndView.addObject("userWalletDto", ModelFactory.createUserWalletDto());
        modelAndView.addObject("isBlocked", user.isBlocked());
        return modelAndView;
    }

    @PostMapping("/user/wallets/walletslist/addmembers")
    public ModelAndView addMembersToSharedWalletListPost(ModelAndView modelAndView,
                                                         Principal principal,
                                                         UserWalletDto userWalletDto) {
        User admin = usersService.findByUsername(principal.getName());
        UserProfile adminUserProfile = userProfilesService.findByUser(admin);
        User member = usersService.findByUsername(userWalletDto.getUsername());
        UserProfile memberUserProfile = userProfilesService.findByUser(member);

        //Call wallets service with add member to wallet
        walletsService.addMember(adminUserProfile, memberUserProfile, userWalletDto.getWalletName());
        modelAndView.addObject("isBlocked", admin.isBlocked());
        modelAndView.setViewName("redirect:/user/wallets/walletslist/addmembers");
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
