package com.team14.virtualwallet.models.dtos.transactionDtos;

import java.math.BigDecimal;

public class TransactionPresentDto {

    private Long id;
    private BigDecimal amount;
    private String from;
    private String to;
    private String type;
    private String executedOn;
    private String senderWalletName;
    private String receiverWalletName;

    public TransactionPresentDto() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getExecutedOn() {
        return executedOn;
    }

    public void setExecutedOn(String executedOn) {
        this.executedOn = executedOn;
    }

    public String getSenderWalletName() {
        return senderWalletName;
    }

    public void setSenderWalletName(String senderWalletName) {
        this.senderWalletName = senderWalletName;
    }

    public String getReceiverWalletName() {
        return receiverWalletName;
    }

    public void setReceiverWalletName(String receiverWalletName) {
        this.receiverWalletName = receiverWalletName;
    }
}
