package com.co.support.it.support_ticket_system.entity;


import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "tickets")
@Getter
@Setter
public class TicketEntity {

    private Long id;

    private String title;

    private String description;

    private String requesterName;
}
