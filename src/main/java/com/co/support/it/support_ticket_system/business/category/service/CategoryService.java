package com.co.support.it.support_ticket_system.business.category.service;

import com.co.support.it.support_ticket_system.business.category.dto.CategoryRequestDto;
import com.co.support.it.support_ticket_system.business.category.dto.CategoryResponseDto;
import com.co.support.it.support_ticket_system.generic.service.GenericService;

public interface CategoryService
        extends GenericService<CategoryRequestDto, CategoryResponseDto, Long> {
}
