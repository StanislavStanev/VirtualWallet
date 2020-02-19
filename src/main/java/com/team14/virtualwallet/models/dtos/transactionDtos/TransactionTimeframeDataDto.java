package com.team14.virtualwallet.models.dtos.transactionDtos;

import java.util.ArrayList;
import java.util.List;

public class TransactionTimeframeDataDto {

    private List<String> dateNames;
    private List<Long> transactionCounts;

    public TransactionTimeframeDataDto() {
        this.dateNames = new ArrayList<>();
        this.transactionCounts = new ArrayList<>();
    }

    public List<String> getDateNames() {
        return dateNames;
    }

    public void setDateNames(List<String> dateNames) {
        this.dateNames = dateNames;
    }

    public List<Long> getTransactionCounts() {
        return transactionCounts;
    }

    public void setTransactionCounts(List<Long> transactionCounts) {
        this.transactionCounts = transactionCounts;
    }
}
