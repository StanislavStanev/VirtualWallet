package com.team14.virtualwallet.controllers.rest;

import com.team14.virtualwallet.models.dtos.transactionDtos.TransactionTimeframeDataDto;
import com.team14.virtualwallet.services.contracts.TransactionsService;
import com.team14.virtualwallet.services.contracts.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import java.security.Principal;

@RestController
@ApiIgnore
public class TransactionRestController {

    private UsersService usersService;
    private TransactionsService transactionsService;

    @Autowired
    public TransactionRestController(UsersService usersService, TransactionsService transactionsService) {
        this.usersService = usersService;
        this.transactionsService = transactionsService;
    }

    @GetMapping(value = "/user/transactions/{timeframe}", produces = "application/json")
    @ResponseBody
    public Object transactionsCount(@PathVariable String timeframe, Principal principal) {
        boolean isAdmin = usersService.userIsAdmin(principal.getName());

        TransactionTimeframeDataDto transactionTimeframeDataDto = this.transactionsService.getTimeframeData(timeframe, principal.getName(), isAdmin);
        return transactionTimeframeDataDto;
    }
}
