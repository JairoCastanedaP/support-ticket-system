package com.co.support.it.support_ticket_system.business.category.controller;

import com.co.support.it.support_ticket_system.business.category.dto.CategoryRequestDto;
import com.co.support.it.support_ticket_system.business.category.dto.CategoryResponseDto;
import com.co.support.it.support_ticket_system.generic.controller.GenericController;

public interface CategoryController
        extends GenericController<CategoryRequestDto, CategoryResponseDto, Long> {
}
