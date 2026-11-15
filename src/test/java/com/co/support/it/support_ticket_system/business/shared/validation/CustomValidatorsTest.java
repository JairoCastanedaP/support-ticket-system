package com.co.support.it.support_ticket_system.business.shared.validation;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

class CustomValidatorsTest {

    private final PhoneValidator phoneValidator = new PhoneValidator();
    private final TicketNumberValidator ticketNumberValidator = new TicketNumberValidator();

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"3001112233", "+573001112233", "6012345"})
    void phone_valid(String phone) {
        assertThat(phoneValidator.isValid(phone, null)).isTrue();
    }

    @ParameterizedTest
    @ValueSource(strings = {"300-111-2233", "abc1234567", "123456", "1234567890123456", "57+3001112233"})
    void phone_invalid(String phone) {
        assertThat(phoneValidator.isValid(phone, null)).isFalse();
    }

    @ParameterizedTest
    @ValueSource(strings = {"TCK-2026-000123", "tck-2026-000001"})
    void ticketNumber_valid(String number) {
        assertThat(ticketNumberValidator.isValid(number, null)).isTrue();
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"TCK-26-000123", "TCK-2026-123", "TCK-2026-0001234", "ABC-2026-000123", "TCK-396df311-d152"})
    void ticketNumber_invalid(String number) {
        assertThat(ticketNumberValidator.isValid(number, null)).isFalse();
    }
}
