package com.co.support.it.support_ticket_system.business.shared.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

public class TicketNumberValidator implements ConstraintValidator<ValidTicketNumber, String> {

    private static final Pattern TICKET_NUMBER = Pattern.compile("TCK-\\d{4}-\\d{6}", Pattern.CASE_INSENSITIVE);

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return value != null && TICKET_NUMBER.matcher(value).matches();
    }
}
