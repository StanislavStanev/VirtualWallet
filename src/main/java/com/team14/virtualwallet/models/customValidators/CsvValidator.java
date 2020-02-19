package com.team14.virtualwallet.models.customValidators;

import com.team14.virtualwallet.models.customValidators.contracts.CSV;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CsvValidator implements ConstraintValidator<CSV, String> {

    @Override
    public void initialize(CSV constraintAnnotation) { }

    @Override
    public boolean isValid(String csv, ConstraintValidatorContext context) {
        Pattern csvPattern = Pattern.compile("^\\d{3}$");
        Matcher matcher = csvPattern.matcher(csv);

        if (matcher.matches()) {
            return true;
        }

        return false;
    }
}
