package com.co.support.it.support_ticket_system.repository;

import com.co.support.it.support_ticket_system.entity.RequesterEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RequesterRepository extends JpaRepository<RequesterEntity,Long> {
}
