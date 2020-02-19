package com.team14.virtualwallet.controllers.rest;

import com.team14.virtualwallet.controllers.rest.constants.RestControllersConstants;
import com.team14.virtualwallet.exceptions.DuplicateEntityException;
import com.team14.virtualwallet.exceptions.EntityNotFoundException;
import com.team14.virtualwallet.models.dtos.bankcarddto.BankCardDeleteDto;
import com.team14.virtualwallet.models.dtos.bankcarddto.BankCardPresentDto;
import com.team14.virtualwallet.models.dtos.bankcarddto.BankCardRegisterDto;
import com.team14.virtualwallet.models.dtos.bankcarddto.BankCardUpdateDto;
import com.team14.virtualwallet.services.contracts.BankCardsService;
import com.team14.virtualwallet.utils.mappers.BankCardMapper;
import io.swagger.annotations.Api;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@Api(value = "Virtual wallet", description = "CRUD operations for user bank card")
public class BankCardRestController {

    private BankCardsService bankCardsService;

    public BankCardRestController(BankCardsService bankCardsService) {
        this.bankCardsService = bankCardsService;
    }

    @GetMapping(path = "/api/v1/private/bank-card", consumes = "application/json", produces = "application/json")
    public BankCardPresentDto bankCard(Principal principal) {
        try {
            return BankCardMapper.BankCardToGetDto(bankCardsService.getActiveCard(principal.getName()));
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }
    }

    @PostMapping(path = "/api/v1/private/bank-card", consumes = "application/json", produces = "application/json")
    public String bankCardRegister(@Valid @RequestBody BankCardRegisterDto bankCardRegisterDto, Principal principal) {
        try {
            bankCardsService.register(bankCardRegisterDto, principal.getName());
        } catch (DuplicateEntityException | EntityNotFoundException e) {
            return e.getMessage();
        }

        return RestControllersConstants.REGISTER_SUCCESSFUL;
    }

    @PutMapping(path = "/api//v1/private/bank-card", consumes = "application/json", produces = "application/json")
    public String bankCardUpdate(@Valid @RequestBody BankCardUpdateDto bankCardUpdateDto, Principal principal) {
        try {
            bankCardsService.update(bankCardUpdateDto, principal.getName());
        } catch (DuplicateEntityException | EntityNotFoundException e) {
            return e.getMessage();
        }

        return RestControllersConstants.UPDATE_SUCCESSFUL;
    }

    @DeleteMapping(path = "/api/v1/private/bank-card", consumes = "application/json", produces = "application/json")
    public String bankCardDelete(@Valid @RequestBody BankCardDeleteDto bankCardDeleteDto, Principal principal) {
        try {
            bankCardsService.delete(bankCardDeleteDto, principal.getName());
        } catch (DuplicateEntityException | EntityNotFoundException e) {
            return e.getMessage();
        }

        return RestControllersConstants.DELETE_SUCCESSFUL;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    private List<String> validationError(MethodArgumentNotValidException ex) {
        return ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(fe -> fe.getDefaultMessage())
                .collect(Collectors.toList());
    }
}