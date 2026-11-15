package com.co.support.it.support_ticket_system.business.ticket.entity;

import com.co.support.it.support_ticket_system.business.ticket.domain.TicketPriority;
import com.co.support.it.support_ticket_system.business.ticket.domain.TicketStatus;
import jakarta.persistence.*;
import lombok.Getter;
import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.Subselect;
import org.hibernate.annotations.Synchronize;

import java.time.LocalDateTime;

/**
 * VO de solo lectura sobre la vista {@code vw_ticket_summary}.
 * Se mapea con {@link Subselect} para que Hibernate no intente crear una tabla con ese nombre;
 * la vista la crean los scripts de {@code db/}. {@link Immutable} impide cualquier escritura.
 */
@Entity
@Immutable
@Subselect("select * from vw_ticket_summary")
@Synchronize({"tickets", "categories", "requesters", "support_agents", "ticket_comments"})
@Getter
public class TicketSummaryView {

    @Id
    @Column(name = "ticket_id")
    private Long ticketId;

    @Column(name = "ticket_number")
    private String ticketNumber;

    private String title;

    @Enumerated(EnumType.STRING)
    private TicketStatus status;

    @Enumerated(EnumType.STRING)
    private TicketPriority priority;

    @Column(name = "category_name")
    private String categoryName;

    @Column(name = "requester_full_name")
    private String requesterFullName;

    @Column(name = "requester_email")
    private String requesterEmail;

    @Column(name = "agent_full_name")
    private String agentFullName;

    @Column(name = "comment_count")
    private Long commentCount;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "resolved_at")
    private LocalDateTime resolvedAt;

    @Column(name = "closed_at")
    private LocalDateTime closedAt;

    @Column(name = "age_hours")
    private Long ageHours;
}
