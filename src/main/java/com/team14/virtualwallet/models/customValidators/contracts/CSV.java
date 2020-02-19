package com.team14.virtualwallet.models.customValidators.contracts;

import com.team14.virtualwallet.models.customValidators.CsvValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = CsvValidator.class)
@Target( { ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface CSV {

    String message() default "CSV must be exactly 3 digits long. Ex. 000";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}