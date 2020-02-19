package com.team14.virtualwallet.controllers.rest;

import com.team14.virtualwallet.controllers.rest.constants.RestControllersConstants;
import com.team14.virtualwallet.exceptions.DuplicateEntityException;
import com.team14.virtualwallet.exceptions.EntityNotFoundException;
import com.team14.virtualwallet.exceptions.InvalidPasswordException;
import com.team14.virtualwallet.models.User;
import com.team14.virtualwallet.models.dtos.userdto.UserPasswordUpdateDto;
import com.team14.virtualwallet.models.dtos.userdto.UserPersonalDataDto;
import com.team14.virtualwallet.models.dtos.userdto.UserPersonalDataUpdateDto;
import com.team14.virtualwallet.models.dtos.userdto.UserRegisterDto;
import com.team14.virtualwallet.services.contracts.UsersService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@Api(value = "Virtual wallet", description = "Basic operations for user - Get/Register and Update of Password/Photo/Data")
public class UserRestController {

    private UsersService usersService;

    @Autowired
    public UserRestController(UsersService usersService) {
        this.usersService = usersService;
    }

    @GetMapping(path = "/api/v1/public/user/{username}", consumes = "application/json", produces = "application/json")
    @ApiOperation(value = "List user by username parameter", response = UserPersonalDataDto.class)
    public UserPersonalDataDto getUser(@PathVariable(required = true) String username) {
        try {
            return usersService.getUserPersonalData(username);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }
    }

    @ApiOperation(value = "Register user", response = User.class)
    @PostMapping(path = "/api/v1/public/user/register", produces = "application/json",
            consumes = "application/json")
    public User register(@Valid @RequestBody UserRegisterDto registerUserDto) {
        try {
            return usersService.register(registerUserDto);
        } catch (DuplicateEntityException | IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }
    }

    @ApiOperation(value = "Edit password", response = String.class)
    @PutMapping(path = "/api/v1/private/user/update/password", produces = "application/json",
            consumes = "application/json")
    public String editPassword(@Valid @RequestBody UserPasswordUpdateDto userPasswordUpdateDto, Principal principal) {
        try {
            usersService.passwordUpdate(userPasswordUpdateDto, principal.getName());
        } catch (EntityNotFoundException | InvalidPasswordException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }

        return RestControllersConstants.PASSWORD_UPDATED_SUCCESSFUL;
    }

    @ApiOperation(value = "Edit personal data", response = String.class)
    @PutMapping(path = "/api/v1/private/user/update/personal-data", produces = "application/json",
            consumes = "application/json")
    public String editPersonalData(@Valid @RequestBody UserPersonalDataUpdateDto userPersonalDataUpdateDto, Principal principal) {
        try {
            usersService.personalDataUpdate(userPersonalDataUpdateDto, principal.getName());
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }

        return RestControllersConstants.PERSONAL_DATA_UPDATE_SUCCESSFUL;
    }

    @ApiOperation(value = "Update photo", response = String.class)
    @PutMapping(path = "/api/v1/private/user/update/photo", consumes = "application/json", produces = "application/json")
    @ResponseBody
    public String editPhoto(@RequestParam MultipartFile picture, Principal principal) {
        try {
            usersService.profilePictureUpdate(principal.getName(), picture);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }

        return RestControllersConstants.PICTURE_UPDATE_SUCCESSFUL;
    }

    @PostMapping(value = "/logout")
    public String logoutPage(HttpServletRequest request, HttpServletResponse response) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth != null) {
            new SecurityContextLogoutHandler().logout(request, response, auth);
        }

        return RestControllersConstants.LOGOUT_SUCCESSFUL;
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
