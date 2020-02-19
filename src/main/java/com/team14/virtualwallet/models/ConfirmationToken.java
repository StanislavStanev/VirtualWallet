package com.team14.virtualwallet.models;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
public class ConfirmationToken extends BaseEntity {

    @Column(name = "confirmation_token")
    private String confirmationToken;

    @Column(name = "token_type")
    private Integer tokenType;

    @Column(name = "is_used", nullable = false)
    Boolean isUsed;

    @Column(name = "created_date")
    private LocalDateTime createdDate;

    @Column(name = "activated_date")
    private LocalDateTime activatedDate;

    @OneToOne(targetEntity = User.class, fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "user_id")
    private User user;

    public ConfirmationToken() {
    }

    public String getConfirmationToken() {
        return confirmationToken;
    }

    public void setConfirmationToken(String confirmationToken) {
        this.confirmationToken = confirmationToken;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Integer getTokenType() {
        return tokenType;
    }

    public void setTokenType(Integer tokenType) {
        this.tokenType = tokenType;
    }

    public Boolean isUsed() {
        return isUsed;
    }

    public void setUsed(Boolean used) {
        isUsed = used;
    }

    public LocalDateTime getActivatedDate() {
        return activatedDate;
    }

    public void setActivatedDate(LocalDateTime activatedDate) {
        this.activatedDate = activatedDate;
    }
}