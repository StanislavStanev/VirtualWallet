package com.team14.virtualwallet.models.dtos.transactionDtos;

import java.util.ArrayList;
import java.util.List;

public class TransactionPageDto {

    private List<TransactionPresentDto> transactions;
    private boolean hasNext;
    private boolean hasPrevious;
    private int currentPage;
    private long allRecords;
    private long fromRecordNum;
    private long toRecordNum;
    private boolean isAdmin;
    private String fromDate;
    private String toDate;
    private String sender;
    private String recipient;
    private String type;
    private String sort;
    private List<String> senders;
    private List<String> recipients;

    public TransactionPageDto() {
        this.transactions = new ArrayList<>();
        this.senders = new ArrayList<>();
        this.recipients = new ArrayList<>();
    }

    public List<TransactionPresentDto> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<TransactionPresentDto> transactions) {
        this.transactions = transactions;
    }

    public boolean isHasNext() {
        return hasNext;
    }

    public void setHasNext(boolean hasNext) {
        this.hasNext = hasNext;
    }

    public boolean isHasPrevious() {
        return hasPrevious;
    }

    public void setHasPrevious(boolean hasPrevious) {
        this.hasPrevious = hasPrevious;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public long getAllRecords() {
        return allRecords;
    }

    public void setAllRecords(long allRecords) {
        this.allRecords = allRecords;
    }

    public long getFromRecordNum() {
        return fromRecordNum;
    }

    public void setFromRecordNum(long fromRecordNum) {
        this.fromRecordNum = fromRecordNum;
    }

    public long getToRecordNum() {
        return toRecordNum;
    }

    public void setToRecordNum(long toRecordNum) {
        this.toRecordNum = toRecordNum;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

    public String getFromDate() {
        return fromDate;
    }

    public void setFromDate(String fromDate) {
        this.fromDate = fromDate;
    }

    public String getToDate() {
        return toDate;
    }

    public void setToDate(String toDate) {
        this.toDate = toDate;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    public List<String> getSenders() {
        return senders;
    }

    public void setSenders(List<String> senders) {
        this.senders = senders;
    }

    public List<String> getRecipients() {
        return recipients;
    }

    public void setRecipients(List<String> recipients) {
        this.recipients = recipients;
    }
}
