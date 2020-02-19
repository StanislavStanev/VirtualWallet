package com.team14.virtualwallet.models.customValidators;

import com.team14.virtualwallet.models.customValidators.contracts.CardNumber;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CardNumberValidator implements ConstraintValidator<CardNumber, String> {

    @Override
    public void initialize(CardNumber constraintAnnotation) { }

    @Override
    public boolean isValid(String cardNumber, ConstraintValidatorContext constraintValidatorContext) {
        Pattern csvPattern = Pattern.compile("^[0-9]{4}-[0-9]{4}-[0-9]{4}-[0-9]{4}$");
        Matcher matcher = csvPattern.matcher(cardNumber);

        if (matcher.matches()) {
            return true;
        }

        return false;
    }
}
