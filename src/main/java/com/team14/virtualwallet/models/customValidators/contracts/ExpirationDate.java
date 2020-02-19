package com.team14.virtualwallet.models.customValidators.contracts;

import com.team14.virtualwallet.models.customValidators.CsvValidator;
import com.team14.virtualwallet.models.customValidators.ExpirationDateValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = ExpirationDateValidator.class)
@Target( { ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface ExpirationDate {

    String message() default "Expiration date must be exactly 5 symbols long. Ex. 01/01";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}