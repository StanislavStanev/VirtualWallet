package com.team14.virtualwallet.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
public class Transaction extends BaseEntity {

    @Column(name = "amount", nullable = false)
    private BigDecimal amount;

    @Column(name = "executed_on", nullable = false)
    private LocalDateTime executedOn;

    @Column(name = "is_top_up")
    private boolean isTopUp;

    @Column(name = "note")
    private String note;

    @ManyToOne(targetEntity = UserProfile.class)
    private UserProfile sender;

    @ManyToOne(targetEntity = UserProfile.class)
    private UserProfile receiver;

    @ManyToOne(targetEntity = Wallet.class)
    private Wallet senderWallet;

    @ManyToOne(targetEntity = Wallet.class)
    private Wallet receiverWallet;

    public Transaction() {
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public LocalDateTime getExecutedOn() {
        return executedOn;
    }

    public void setExecutedOn(LocalDateTime executedOn) {
        this.executedOn = executedOn;
    }

    public boolean isTopUp() {
        return isTopUp;
    }

    public void setTopUp(boolean topUp) {
        isTopUp = topUp;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public UserProfile getSender() {
        return sender;
    }

    public void setSender(UserProfile sender) {
        this.sender = sender;
    }

    public UserProfile getReceiver() {
        return receiver;
    }

    public void setReceiver(UserProfile receiver) {
        this.receiver = receiver;
    }

    public Wallet getSenderWallet() {
        return senderWallet;
    }

    public void setSenderWallet(Wallet senderWallet) {
        this.senderWallet = senderWallet;
    }

    public Wallet getReceiverWallet() {
        return receiverWallet;
    }

    public void setReceiverWallet(Wallet receiverWallet) {
        this.receiverWallet = receiverWallet;
    }

}
