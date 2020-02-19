package com.team14.virtualwallet.models.dtos.apitopupdto;

import java.io.Serializable;
import java.math.BigDecimal;

public class TopUpDto implements Serializable {
    BigDecimal amount;
    String currency;
    String description;
    String idempotencyKey;
    String csv;
    CardDetailsDto cardDetails;

    public TopUpDto() {
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIdempotencyKey() {
        return idempotencyKey;
    }

    public void setIdempotencyKey(String idempotencyKey) {
        this.idempotencyKey = idempotencyKey;
    }

    public String getCsv() {
        return csv;
    }

    public void setCsv(String csv) {
        this.csv = csv;
    }

    public CardDetailsDto getCardDetails() {
        return cardDetails;
    }

    public void setCardDetails(CardDetailsDto cardDetails) {
        this.cardDetails = cardDetails;
    }


}
