package com.team14.virtualwallet.models.dtos.userdto;


import javax.validation.constraints.Size;
import java.math.BigDecimal;

public class UserWalletDto {

    private String walletName;
    private String defaultWallet;
    private String sharedWallet;
    private BigDecimal balance;
    private Boolean isDefault;
    private Boolean isShared;
    private Boolean isAdmin;
    private Long walletId;
    private String username;

    @Size(min = 1, max = 20, message = "Wallet name must be between 1 and 20 charachters")
    public String getWalletName() {
        return walletName;
    }

    public void setWalletName(String walletName) {
        this.walletName = walletName;
    }

    public String getDefaultWallet() {
        return defaultWallet;
    }

    public void setDefaultWallet(String defaultWallet) {
        this.defaultWallet = defaultWallet;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public Boolean getDefault() {
        return isDefault;
    }

    public void setDefault(Boolean aDefault) {
        isDefault = aDefault;
    }

    public Boolean getShared() {
        return isShared;
    }

    public void setShared(Boolean shared) {
        isShared = shared;
    }

    public String getSharedWallet() {
        return sharedWallet;
    }

    public void setSharedWallet(String sharedWallet) {
        this.sharedWallet = sharedWallet;
    }

    public Boolean getAdmin() {
        return isAdmin;
    }

    public void setAdmin(Boolean admin) {
        isAdmin = admin;
    }

    public Long getWalletId() {
        return walletId;
    }

    public void setWalletId(Long walletId) {
        this.walletId = walletId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
