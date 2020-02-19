package com.team14.virtualwallet.models.dtos.userdto;

import io.swagger.annotations.ApiModelProperty;

public class UserPersonalDataDto {

    @ApiModelProperty(notes = "Unique username for application.")
    private String username;
    @ApiModelProperty(notes = "User phone number.")
    private String phone;
    @ApiModelProperty(notes = "User full name. Not obligatory on register.")
    private String fullname;
    @ApiModelProperty(notes = "User email. Registration mail send to this email address.")
    private String email;
    @ApiModelProperty(notes = "Enabled, after registration.")
    private boolean isEnabled;
    @ApiModelProperty(notes = "Blocked from admin.")
    private boolean isBlocked;
    @ApiModelProperty(notes = "URL of user picture.")
    private String pictureUrl;
    @ApiModelProperty(notes = "Number of hours after registration.")
    private String createdBeforeHours;

    public UserPersonalDataDto() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public void setEnabled(boolean enabled) {
        isEnabled = enabled;
    }

    public boolean isBlocked() {
        return isBlocked;
    }

    public void setBlocked(boolean blocked) {
        isBlocked = blocked;
    }

    public String getPictureUrl() {
        return pictureUrl;
    }

    public void setPictureUrl(String pictureUrl) {
        this.pictureUrl = pictureUrl;
    }

    public String getCreatedBeforeHours() {
        return createdBeforeHours;
    }

    public void setCreatedBeforeHours(String createdBeforeHours) {
        this.createdBeforeHours = createdBeforeHours;
    }
}