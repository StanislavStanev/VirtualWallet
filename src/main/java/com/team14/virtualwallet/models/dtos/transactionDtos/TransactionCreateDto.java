package com.team14.virtualwallet.models.dtos.transactionDtos;

import java.math.BigDecimal;

public class TransactionCreateDto {

    String receiver;
    String receiverDataType;
    String senderUserName;
    String walletName;
    String walletName2;
    String checksum;
    String token;
    BigDecimal amount;
    String note;

    public TransactionCreateDto() {
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getReceiverDataType() {
        return receiverDataType;
    }

    public void setReceiverDataType(String receiverDataType) {
        this.receiverDataType = receiverDataType;
    }

    public String getSenderUserName() {
        return senderUserName;
    }

    public void setSenderUserName(String senderUserName) {
        this.senderUserName = senderUserName;
    }

    public String getWalletName() {
        return walletName;
    }

    public void setWalletName(String walletName) {
        this.walletName = walletName;
    }

    public String getWalletName2() {
        return walletName2;
    }

    public void setWalletName2(String walletName2) {
        this.walletName2 = walletName2;
    }


    public String getChecksum() {
        return checksum;
    }

    public void setChecksum(String checksum) {
        this.checksum = checksum;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

}
