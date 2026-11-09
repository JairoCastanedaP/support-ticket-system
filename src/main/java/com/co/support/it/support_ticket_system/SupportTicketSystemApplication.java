package com.co.support.it.support_ticket_system;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan(basePackages = {
		"com.co.support.it.support_ticket_system.business",
		"com.co.support.it.support_ticket_system.infrastructure.persistence.mysql.outbox.entity"
})
@EnableJpaRepositories(basePackages = {
		"com.co.support.it.support_ticket_system.business",
		"com.co.support.it.support_ticket_system.infrastructure.persistence.mysql.outbox.repository"
})
public class SupportTicketSystemApplication {

	public static void main(String[] args) {
		SpringApplication.run(SupportTicketSystemApplication.class, args);
	}

}
