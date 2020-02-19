package com.team14.virtualwallet.controllers;

import com.team14.virtualwallet.models.dtos.userdto.UserPersonalDataDto;
import com.team14.virtualwallet.services.contracts.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;


@Controller
@ApiIgnore
public class AdminController {

    private UsersService usersService;

    @Autowired
    public AdminController(UsersService usersService) {
        this.usersService = usersService;
    }

    @GetMapping("/admin/users")
    public ModelAndView userAdminPanel(ModelAndView modelAndView,
                                       @RequestParam(defaultValue = "0") int page,
                                       @RequestParam(value = "sort", defaultValue = "", required = false) String sort,
                                       @RequestParam(value = "username", defaultValue = "", required = false) String username,
                                       @RequestParam(value = "email", defaultValue = "", required = false) String email,
                                       @RequestParam(value = "phone", defaultValue = "", required = false) String phone) {

        List<UserPersonalDataDto> userPersonalDataDtoList = usersService.getAdminPageDtoUserProfiles(page, sort, username, email, phone);
        long numberOfPages = usersService.getNumberOfResults(username, email, phone);
        long numberOfButtons = usersService.getNumberOfButtons(numberOfPages);

        modelAndView.setViewName("admin-users");
        modelAndView.addObject("users", userPersonalDataDtoList);
        modelAndView.addObject("numberOfPages", numberOfPages);
        modelAndView.addObject("numberOfButtons", numberOfButtons);
        modelAndView.addObject("page", page);
        modelAndView.addObject("sort", sort);
        modelAndView.addObject("username", username);
        modelAndView.addObject("email", email);
        modelAndView.addObject("phone", phone);

        return modelAndView;
    }

    @GetMapping("/admin/users/unblock/{userName}")
    public ModelAndView userAdminPanelUserUnBlock(ModelAndView modelAndView, @PathVariable String userName) {
        usersService.unBlockUser(userName);
        modelAndView.setViewName("redirect:/admin/users");
        return modelAndView;
    }

    @GetMapping("/admin/users/block/{userName}")
    public ModelAndView userAdminPanelUserBlock(ModelAndView modelAndView, @PathVariable String userName) {
        usersService.blockUser(userName);
        modelAndView.setViewName("redirect:/admin/users");
        return modelAndView;
    }

    @GetMapping("/admin/transactions")
    public ModelAndView transactionsAdminPanel(ModelAndView modelAndView) {
        modelAndView.setViewName("redirect:/user/transactions");
        return modelAndView;
    }
}

