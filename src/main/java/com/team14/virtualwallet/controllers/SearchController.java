package com.team14.virtualwallet.controllers;

import com.team14.virtualwallet.models.dtos.userdto.UserPictureDto;
import com.team14.virtualwallet.services.contracts.UserProfilesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;

@Controller
@ApiIgnore
public class SearchController {

    private UserProfilesService userProfilesService;

    @Autowired
    public SearchController(UserProfilesService userProfilesService) {
        this.userProfilesService = userProfilesService;
    }

    @GetMapping("/newtransaction/sendmoney/searchbyusername")
    @ResponseBody
    public List<String> searchByUsername(HttpServletRequest request) {
        return userProfilesService.searchByUsername(request.getParameter("term"));
    }

    @GetMapping("/newtransaction/sendmoney/searchbyexactusername")
    @ResponseBody
    public ResponseEntity<Object> searchByExactUsername(HttpServletRequest request) {
        String foundUsername = userProfilesService.searchByExactUsername(request.getParameter("term"));
        if (foundUsername == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        return ResponseEntity.ok(foundUsername);
    }

    @GetMapping("/newtransaction/sendmoney/searchbyphone")
    @ResponseBody
    public List<String> searchByPhone(HttpServletRequest request) {
        return userProfilesService.searchByPhone(request.getParameter("term"));
    }

    @GetMapping("/newtransaction/sendmoney/searchbyemail")
    @ResponseBody
    public List<String> search(HttpServletRequest request) {
        return userProfilesService.searchByEmail(request.getParameter("term"));
    }

    @GetMapping("/newtransaction/sendmoney/searchbyusernamegetpicture")
    @ResponseBody
    public List<UserPictureDto> searchByUsernameGetPictureUrl(HttpServletRequest request) {
        return userProfilesService.searchByUsernameGetPictureUrl(request.getParameter("term"));
    }

    @GetMapping("/newtransaction/sendmoney/searchbyemailgetpicture")
    @ResponseBody
    public List<UserPictureDto> searchByEmailGetPictureUrl(HttpServletRequest request) {
        return userProfilesService.searchByEmailGetPictureUrl(request.getParameter("term"));
    }

    @GetMapping("/newtransaction/sendmoney/searchbyphonegetpicture")
    @ResponseBody
    public List<UserPictureDto> searchByPhoneGetPictureUrl(HttpServletRequest request) {
        return userProfilesService.searchByPhoneGetPictureUrl(request.getParameter("term"));
    }

    @GetMapping("/newtransaction/topup/searchbywalletname")
    @ResponseBody
    public BigDecimal getWalletBalanceByWalletName(HttpServletRequest request, Principal principal) {
        return userProfilesService.getWalletBalanceByWalletName(principal.getName(), request.getParameter("term"));
    }


}
