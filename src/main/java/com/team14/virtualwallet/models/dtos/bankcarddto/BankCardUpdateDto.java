package com.team14.virtualwallet.models.dtos.bankcarddto;

import com.team14.virtualwallet.models.customValidators.contracts.CSV;
import com.team14.virtualwallet.models.customValidators.contracts.CardNumber;
import com.team14.virtualwallet.models.customValidators.contracts.ExpirationDate;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.Size;

public class BankCardUpdateDto {

    @CardNumber
    @ApiModelProperty(notes = "Bank card number. Format is XXXX-XXXX-XXXX-XXXX")
    private String cardNumber;

    @ExpirationDate
    @ApiModelProperty(notes = "Bank card expiration date. Format is MM/YY")
    private String expirationDate;

    @CSV
    @ApiModelProperty(notes = "Card CSV number. Format is exactly 3 digits.")
    private String csv;

    @Size(min = 2, max = 40, message = "Issuer name cannot be empty")
    @ApiModelProperty(notes = "Bank issuer name.")
    private String cardIssuer;

    @Size(min = 2, max = 40, message = "Card Holder name must be between 2 and 40 characters long.")
    @ApiModelProperty(notes = "Card holder name written on card.")
    private String cardHolderName;

    public BankCardUpdateDto() {
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(String expirationDate) {
        this.expirationDate = expirationDate;
    }

    public String getCsv() {
        return csv;
    }

    public void setCsv(String csv) {
        this.csv = csv;
    }

    public String getCardIssuer() {
        return cardIssuer;
    }

    public void setCardIssuer(String cardIssuer) {
        this.cardIssuer = cardIssuer;
    }

    public String getCardHolderName() {
        return cardHolderName;
    }

    public void setCardHolderName(String cardHolderName) {
        this.cardHolderName = cardHolderName;
    }
}
