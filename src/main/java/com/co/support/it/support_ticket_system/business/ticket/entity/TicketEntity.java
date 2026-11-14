package com.co.support.it.support_ticket_system.business.ticket.entity;

import com.co.support.it.support_ticket_system.business.category.entity.CategoryEntity;
import com.co.support.it.support_ticket_system.business.requester.entity.RequesterEntity;
import com.co.support.it.support_ticket_system.business.supportagent.entity.SupportAgentEntity;
import com.co.support.it.support_ticket_system.business.ticket.domain.TicketPriority;
import com.co.support.it.support_ticket_system.business.ticket.domain.TicketStatus;
import com.co.support.it.support_ticket_system.generic.entity.GenericEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "tickets")
@AttributeOverride(
        name = "active",
        column = @Column(name = "is_active", nullable = false)
)
@Getter
@Setter
public class TicketEntity extends GenericEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ticket_id")
    private Long id;

    @Column(name = "ticket_number", nullable = false, unique = true, length = 40)
    private String ticketNumber;

    @Column(nullable = false, length = 150)
    private String title;

    @Column(nullable = false, length = 2000)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TicketPriority priority = TicketPriority.MEDIUM;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private TicketStatus status = TicketStatus.OPEN;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "category_id", nullable = false)
    private CategoryEntity category;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "requester_id", nullable = false)
    private RequesterEntity requester;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_agent_id")
    private SupportAgentEntity assignedAgent;

    @Column(name = "resolution_note", length = 1000)
    private String resolutionNote;

    @Column(name = "resolved_at")
    private LocalDateTime resolvedAt;

    @Column(name = "closed_at")
    private LocalDateTime closedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    /**
     * El número legible depende del id, que solo existe después del INSERT.
     * Mientras tanto se guarda un valor temporal único que {@link #assignTicketNumber()} reemplaza.
     */
    @PrePersist
    private void beforeInsert() {
        if (ticketNumber == null || ticketNumber.isBlank()) {
            ticketNumber = "TMP-" + UUID.randomUUID();
        }
    }

    /**
     * Asigna el número legible con el formato TCK-año-consecutivo, por ejemplo TCK-2026-000123.
     */
    public void assignTicketNumber() {
        int year = getCreatedAt() != null ? getCreatedAt().getYear() : LocalDateTime.now().getYear();
        ticketNumber = String.format("TCK-%d-%06d", year, id);
    }
}
