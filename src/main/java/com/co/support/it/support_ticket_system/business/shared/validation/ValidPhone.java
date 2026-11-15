package com.co.support.it.support_ticket_system.business.shared.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * Teléfono opcional: si viene, debe tener entre 7 y 15 dígitos y puede iniciar con +.
 */
@Documented
@Constraint(validatedBy = PhoneValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER, ElementType.RECORD_COMPONENT})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidPhone {

    String message() default "El teléfono debe tener entre 7 y 15 dígitos y solo puede iniciar con +";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
