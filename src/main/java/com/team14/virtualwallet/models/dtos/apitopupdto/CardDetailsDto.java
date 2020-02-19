package com.team14.virtualwallet.models.dtos.apitopupdto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class CardDetailsDto {
    String cardNumber;
    String expirationDate;
    String cardholderName;
    String csv;

    public CardDetailsDto() {
    }

    @NotNull
    @Size(min = 20, max = 20, message = "Bank Card format is 0000-0000-0000-0000")
    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    @NotNull
    @Size(min = 5, max = 5, message = "Expiration date format is MM/YY, 5 chars")
    public String getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(String expirationDate) {
        this.expirationDate = expirationDate;
    }

    @NotNull
    @Size(min = 2, max = 40, message = "Card Holder name must be between 2 and 40 characters long.")
    public String getCardholderName() {
        return cardholderName;
    }

    public void setCardholderName(String cardholderName) {
        this.cardholderName = cardholderName;
    }

    @NotNull
    @Size(min = 3, max = 3, message = "CSV is exactly 3 digits log.")
    public String getCsv() {
        return csv;
    }

    public void setCsv(String csv) {
        this.csv = csv;
    }
}
