package com.team14.virtualwallet.models.dtos.userdto;

import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.Size;

public class UserPasswordUpdateDto {
    @ApiModelProperty(notes = "Password must be between 8 and 55 characters. Allowed symbols (!@#$%^&*())")
    private String password;
    @ApiModelProperty(notes = "Password confirm.")
    private String confirmPassword;

    public UserPasswordUpdateDto() {
    }

    @Size(min = 8, max = 55, message = "Password should be between 8 and 55 symbols.")
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Size(min = 8, max = 55, message = "Password should be between 8 and 55 symbols.")
    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }
}
