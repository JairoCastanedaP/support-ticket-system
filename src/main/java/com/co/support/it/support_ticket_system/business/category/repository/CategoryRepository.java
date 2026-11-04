package com.co.support.it.support_ticket_system.business.category.repository;

import com.co.support.it.support_ticket_system.business.category.entity.CategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<CategoryEntity, Long> {
}
