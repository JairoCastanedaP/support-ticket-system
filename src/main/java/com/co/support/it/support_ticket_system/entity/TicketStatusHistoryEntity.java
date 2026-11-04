package com.co.support.it.support_ticket_system.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "ticket_status_history")
@Getter
@Setter
public class TicketStatusHistoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "status_history_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ticket_id", nullable = false)
    private TicketEntity ticket;

    @Column(name = "previous_status", nullable = false, length = 30)
    private String previousStatus;

    @Column(name = "new_status", nullable = false, length = 30)
    private String newStatus;

    @Column(name = "changed_at", nullable = false)
    private LocalDateTime changedAt;
}