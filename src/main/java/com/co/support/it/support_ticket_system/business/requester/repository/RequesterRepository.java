package com.co.support.it.support_ticket_system.business.requester.repository;

import com.co.support.it.support_ticket_system.business.requester.entity.RequesterEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RequesterRepository extends JpaRepository<RequesterEntity,Long> {
}
