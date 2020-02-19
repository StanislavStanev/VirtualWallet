package com.team14.virtualwallet.models.customValidators;

import com.team14.virtualwallet.models.customValidators.contracts.ExpirationDate;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ExpirationDateValidator implements ConstraintValidator<ExpirationDate, String> {

    @Override
    public void initialize(ExpirationDate constraintAnnotation) { }

    @Override
    public boolean isValid(String expirationDate, ConstraintValidatorContext constraintValidatorContext) {
        Pattern csvPattern = Pattern.compile("^[0-9]{2}\\/[0-9]{2}$");
        Matcher matcher = csvPattern.matcher(expirationDate);

        if (matcher.matches()) {
            return true;
        }

        return false;
    }
}
