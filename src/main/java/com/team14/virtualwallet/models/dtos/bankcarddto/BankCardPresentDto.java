package com.team14.virtualwallet.models.dtos.bankcarddto;

import io.swagger.annotations.ApiModelProperty;

public class BankCardPresentDto {
    @ApiModelProperty(notes = "Bank card number. Format is XXXX-XXXX-XXXX-XXXX")
    private String cardNumber;
    @ApiModelProperty(notes = "Card holder name written on card.")
    private String cardHolderName;
    @ApiModelProperty(notes = "Bank card expiration date. Format is MM/YY")
    private String expirationDate;
    @ApiModelProperty(notes = "Bank issuer name.")
    private String cardIssuer;
    @ApiModelProperty(notes = "Card CSV number. Format is exactly 3 digits.")
    private String csv;

    public BankCardPresentDto() {
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getCardHolderName() {
        return cardHolderName;
    }

    public void setCardHolderName(String cardHolderName) {
        this.cardHolderName = cardHolderName;
    }

    public String getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(String expirationDate) {
        this.expirationDate = expirationDate;
    }

    public String getCardIssuer() {
        return cardIssuer;
    }

    public void setCardIssuer(String cardIssuer) {
        this.cardIssuer = cardIssuer;
    }

    public String getCsv() {
        return csv;
    }

    public void setCsv(String csv) {
        this.csv = csv;
    }
}