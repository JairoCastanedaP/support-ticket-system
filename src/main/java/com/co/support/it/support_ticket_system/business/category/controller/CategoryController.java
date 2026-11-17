package com.co.support.it.support_ticket_system.business.category.controller;

import io.swagger.v3.oas.annotations.tags.Tag;

import com.co.support.it.support_ticket_system.business.category.dto.CategoryRequestDto;
import com.co.support.it.support_ticket_system.business.category.dto.CategoryResponseDto;
import com.co.support.it.support_ticket_system.generic.controller.GenericController;

@Tag(name = "Categorías", description = "Clasificación de los tickets de soporte. CRUD genérico con borrado lógico; el nombre es único.")
public interface CategoryController
        extends GenericController<CategoryRequestDto, CategoryResponseDto, Long> {
}
