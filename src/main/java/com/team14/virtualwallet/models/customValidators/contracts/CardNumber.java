package com.team14.virtualwallet.models.customValidators.contracts;

import com.team14.virtualwallet.models.customValidators.CardNumberValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;


@Documented
@Constraint(validatedBy = CardNumberValidator.class)
@Target( { ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface CardNumber {

    String message() default "Card Number format is incorrect. Ex. 0000-0000-0000-0000";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}