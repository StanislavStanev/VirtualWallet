package com.team14.virtualwallet.models.dtos.userdto;

import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.Email;
import javax.validation.constraints.Size;

public class UserPersonalDataUpdateDto {

    @ApiModelProperty(notes = "User full name. Full name must be between 5 and 128 characters.")
    private String fullName;
    @ApiModelProperty(notes = "User email.")
    private String email;
    @ApiModelProperty(notes = "User phone number.")
    private String phone;

    public UserPersonalDataUpdateDto() {
    }

    @Size(min = 5, max = 128, message = "Your name must be between 5 and 128 characters.")
    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    @Email(message = "Email must be valid.")
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Size(min = 10, max = 25, message = "Phone number must be between 10 and 25 characters.")
    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}