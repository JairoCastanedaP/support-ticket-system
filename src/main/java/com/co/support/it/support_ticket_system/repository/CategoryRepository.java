package com.co.support.it.support_ticket_system.repository;

import com.co.support.it.support_ticket_system.entity.CategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<CategoryEntity, Long> {
}
