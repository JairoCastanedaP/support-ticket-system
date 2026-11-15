package com.co.support.it.support_ticket_system.business.notification.repository;

import com.co.support.it.support_ticket_system.business.notification.domain.NotificationStatus;
import com.co.support.it.support_ticket_system.business.notification.entity.TicketNotificationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TicketNotificationRepository extends JpaRepository<TicketNotificationEntity, Long> {

    List<TicketNotificationEntity> findTop20ByStatusOrderByIdAsc(NotificationStatus status);
}
