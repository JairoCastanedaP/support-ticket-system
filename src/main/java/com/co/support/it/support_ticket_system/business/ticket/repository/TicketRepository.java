package com.co.support.it.support_ticket_system.business.ticket.repository;

import com.co.support.it.support_ticket_system.business.ticket.entity.TicketEntity;
import com.co.support.it.support_ticket_system.business.ticket.projection.TicketSearchProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TicketRepository
        extends JpaRepository<TicketEntity, Long>,
        JpaSpecificationExecutor<TicketEntity>
{

    Optional<TicketEntity> findByIdAndActiveTrueAndDeletedAtIsNull(Long id);

    /**
     * JPQL: busca un ticket activo por su número legible y trae en la misma consulta
     * la categoría, el solicitante y el agente (evita una consulta adicional por relación).
     */
    @Query("""
            select t from TicketEntity t
              join fetch t.category
              join fetch t.requester
              left join fetch t.assignedAgent
             where upper(t.ticketNumber) = upper(:ticketNumber)
               and t.active = true
               and t.deletedAt is null
            """)
    Optional<TicketEntity> findActiveByTicketNumber(@Param("ticketNumber") String ticketNumber);

    /**
     * SQL nativo: búsqueda por texto en el título y la descripción.
     * Devuelve una proyección con las columnas necesarias, sin cargar entidades completas.
     */
    @Query(value = """
            SELECT t.ticket_id     AS id,
                   t.ticket_number AS ticketNumber,
                   t.title         AS title,
                   t.status        AS status,
                   t.priority      AS priority
              FROM tickets t
             WHERE t.is_active = TRUE
               AND t.deleted_at IS NULL
               AND (LOWER(t.title) LIKE CONCAT('%', LOWER(:text), '%')
                    OR LOWER(t.description) LIKE CONCAT('%', LOWER(:text), '%'))
             ORDER BY t.ticket_id DESC
             LIMIT 50
            """, nativeQuery = true)
    List<TicketSearchProjection> searchByText(@Param("text") String text);
}
