package com.team14.virtualwallet.models.dtos.bankcarddto;


import com.team14.virtualwallet.models.customValidators.contracts.CSV;
import com.team14.virtualwallet.models.customValidators.contracts.CardNumber;
import io.swagger.annotations.ApiModelProperty;

public class BankCardDeleteDto {

    @CardNumber
    @ApiModelProperty(notes = "Bank card number. Format is XXXX-XXXX-XXXX-XXXX")
    private String cardNumber;

    @CSV
    @ApiModelProperty(notes = "Card CSV number. Format is exactly 3 digits.")
    private String csv;

    public BankCardDeleteDto() {
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getCsv() {
        return csv;
    }

    public void setCsv(String csv) {
        this.csv = csv;
    }
}
