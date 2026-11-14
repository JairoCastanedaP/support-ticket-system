package com.co.support.it.support_ticket_system.business.category.repository;

import com.co.support.it.support_ticket_system.business.category.entity.CategoryEntity;
import com.co.support.it.support_ticket_system.generic.repository.GenericRepository;

public interface CategoryRepository extends GenericRepository<CategoryEntity, Long> {

    boolean existsByNameIgnoreCase(String name);

    boolean existsByNameIgnoreCaseAndIdNot(String name, Long id);
}
