package com.team14.virtualwallet.models;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.Objects;

@Entity
@Table(name = "bank_cards")
public class BankCard extends BaseEntity {

    @Column(name = "card_number", unique = true, nullable = false)
    private String cardNumber;

    @Column(name = "expiration_date", nullable = false)
    private String expirationDate;

    @Column(name = "card_holder_name")
    @Size(min = 2, max = 40, message = "Card holder name must be between 2 and exactly 40 characters (a-z, A-Z).")
    private String cardHolderName;

    @Column(name = "csv")
    @Size(min = 3, max = 3, message = "CSV is exactly 3 digits log.")
    private String csv;

    @Column(name = "card_issuer")
    private String cardIssuer;

    @Column(name = "is_expired")
    private boolean isExpired;

    @Column(name = "is_deactivated")
    private boolean isDeactivated;

    public BankCard() {
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

    public String getCardHolderName() {
        return cardHolderName;
    }

    public void setCardHolderName(String cardHolderName) {
        this.cardHolderName = cardHolderName;
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

    public boolean isExpired() {
        return isExpired;
    }

    public void setExpired(boolean expired) {
        isExpired = expired;
    }

    public boolean isDeactivated() {
        return isDeactivated;
    }

    public void setDeactivated(boolean deactivated) {
        isDeactivated = deactivated;
    }

    @Override
    public int hashCode() {
        return Objects.hash(cardNumber, csv);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (!(obj instanceof BankCard)) {
            return false;
        }

        if (getClass() != obj.getClass()) {
            return false;
        }

        if (this == obj) {
            return true;
        }

        BankCard bankCard = (BankCard) obj;

        return Objects.equals(cardNumber, bankCard.cardNumber)
                && Objects.equals(csv, bankCard.csv);
    }
}