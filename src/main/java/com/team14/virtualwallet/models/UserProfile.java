package com.team14.virtualwallet.models;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "user_profiles")
public class UserProfile extends BaseEntity {

    @Column(name = "fullname")
    @Size(min = 5, max = 128, message = "Your name must be between 5 and 128 characters.")
    private String fullName;

    @OneToOne(targetEntity = Picture.class, fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "picture_id")
    private Picture picture;

    @OneToMany(targetEntity = BankCard.class, fetch = FetchType.EAGER)
    private Set<BankCard> bankCards;

    @Column(name = "phone_number")
    @Size(min = 10, max = 25, message = "Phone number must be between 10 and 25 characters.")
    private String phoneNumber;

    @ManyToMany(targetEntity = Wallet.class, cascade = {
            CascadeType.PERSIST,
            CascadeType.MERGE
    }, fetch = FetchType.EAGER)
    @JoinTable(name = "user_profiles_wallets",
            joinColumns = @JoinColumn(name = "user_profile_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "wallets_id", referencedColumnName = "id")
    )
    private Set<Wallet> wallets;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User user;

    public UserProfile() {
        this.bankCards = new HashSet<>();
        this.wallets = new HashSet<>();
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public Picture getPicture() {
        return picture;
    }

    public void setPicture(Picture picture) {
        this.picture = picture;
    }

    public Set<BankCard> getBankCards() {
        return bankCards;
    }

    public void setBankCards(Set<BankCard> bankCards) {
        this.bankCards = bankCards;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Set<Wallet> getWallets() {
        return wallets;
    }

    public void setWallets(Set<Wallet> wallets) {
        this.wallets = wallets;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}

