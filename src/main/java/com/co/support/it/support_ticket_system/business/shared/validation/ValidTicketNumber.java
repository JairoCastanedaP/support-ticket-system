package com.co.support.it.support_ticket_system.business.shared.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * Número legible de ticket: TCK-año-consecutivo de 6 dígitos, por ejemplo TCK-2026-000123.
 */
@Documented
@Constraint(validatedBy = TicketNumberValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER, ElementType.RECORD_COMPONENT})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidTicketNumber {

    String message() default "El número de ticket debe tener el formato TCK-AAAA-NNNNNN, por ejemplo TCK-2026-000123";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
