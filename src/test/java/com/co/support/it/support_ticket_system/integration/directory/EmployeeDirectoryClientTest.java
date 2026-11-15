package com.co.support.it.support_ticket_system.integration.directory;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class EmployeeDirectoryClientTest {

    @Test
    void toEmployee_splitsNameAndCleansThePhone() {
        EmployeeDto employee = EmployeeDirectoryClient.toEmployee(new EmployeeDirectoryClient.DirectoryUser(
                1L, "Leanne Graham", "Sincere@april.biz", "1-770-736-8031 x56442"));

        assertThat(employee.name()).isEqualTo("Leanne");
        assertThat(employee.surname()).isEqualTo("Graham");
        assertThat(employee.email()).isEqualTo("Sincere@april.biz");
        assertThat(employee.phoneNumber()).isEqualTo("17707368031");
    }

    @Test
    void toEmployee_dropsTitlesAndKeepsCompoundSurnames() {
        EmployeeDto employee = EmployeeDirectoryClient.toEmployee(new EmployeeDirectoryClient.DirectoryUser(
                5L, "Mrs. Dennis Schulist", "Karley_Dach@jasper.info", "(254)954-1289"));

        assertThat(employee.name()).isEqualTo("Dennis");
        assertThat(employee.surname()).isEqualTo("Schulist");
        assertThat(employee.phoneNumber()).isEqualTo("2549541289");
    }

    @Test
    void normalizePhone_discardsValuesThatAreNotPhones() {
        assertThat(EmployeeDirectoryClient.normalizePhone("123")).isNull();
        assertThat(EmployeeDirectoryClient.normalizePhone(null)).isNull();
    }
}
