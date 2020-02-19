package com.team14.virtualwallet.models.dtos.userdto;

import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class UserRegisterDto {

    @NotNull
    @Size(min = 5, max = 128, message = "Full name must be between 5 and 128 characters.")
    @ApiModelProperty(notes = "User full name. Full name must be between 5 and 128 characters.")
    private String fullName;

    @NotNull
    @Size(min = 5, max = 50, message = "Username size must be between 5 and 50 characters.")
    @ApiModelProperty(notes = "Username. Must be unique. Must be between 5 and 50 characters.")
    private String username;

    @NotNull
    @Email(message = "Email is invalid.")
    @ApiModelProperty(notes = "User email.")
    private String email;

    @NotNull
    @Size(min = 8, max = 55, message = "Password must be between 8 and 55 characters. Allowed symbols (!@#$%^&*())")
    @ApiModelProperty(notes = "User password. Must be between 8 and 55 characters. Allowed symbols (!@#$%^&*())")
    private String password;

    private String confirmPassword;

    public UserRegisterDto() {
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }
}
