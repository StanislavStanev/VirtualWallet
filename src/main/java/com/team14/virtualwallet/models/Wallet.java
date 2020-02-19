package com.team14.virtualwallet.models;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "wallets")
public class Wallet extends BaseEntity {

    @ManyToMany(targetEntity = Transaction.class, cascade = {
            CascadeType.PERSIST,
            CascadeType.MERGE
    }, fetch = FetchType.EAGER)
    @JoinTable(name = "wallets_transactions",
            joinColumns = @JoinColumn(name = "wallet_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "transaction_id", referencedColumnName = "id")
    )
    private Set<Transaction> transactions;

    @Column(name = "balance", nullable = false)
    private BigDecimal balance;

    @Column(name = "isDefault", nullable = false)
    private Boolean isDefault;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "isShared", nullable = false)
    private Boolean isShared;

    @ManyToMany(targetEntity = UserProfile.class, cascade = {
            CascadeType.PERSIST,
            CascadeType.MERGE
    }, fetch = FetchType.EAGER)
    @JoinTable(name = "user_profiles_wallets",
            joinColumns = @JoinColumn(name = "wallets_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "user_profile_id", referencedColumnName = "id")
    )
    private Set<UserProfile> members;

    @OneToOne(targetEntity = UserProfile.class, fetch = FetchType.EAGER)
    @JoinColumn(name = "admin_id")
    private UserProfile admin;

    public Wallet() {
        this.transactions = new HashSet<>();
        this.members = new HashSet<>();
    }

    public Set<Transaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(Set<Transaction> transactions) {
        this.transactions = transactions;
    }

    public void addTransaction(Transaction transaction) {
        this.transactions.add(transaction);
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public Boolean isDefault() {
        return isDefault;
    }

    public void setIsDefault(Boolean isDefault) {
        this.isDefault = isDefault;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public Boolean isShared() {
        return isShared;
    }

    public void setShared(Boolean shared) {
        isShared = shared;
    }

    public Set<UserProfile> getMembers() {
        return members;
    }

    public void setMembers(Set<UserProfile> members) {
        this.members = members;
    }

    public UserProfile getAdmin() {
        return admin;
    }

    public void setAdmin(UserProfile admin) {
        this.admin = admin;
    }

}
