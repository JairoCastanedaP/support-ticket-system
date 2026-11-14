package com.co.support.it.support_ticket_system.business.category.controller.impl;

import com.co.support.it.support_ticket_system.business.category.controller.CategoryController;
import com.co.support.it.support_ticket_system.business.category.dto.CategoryRequestDto;
import com.co.support.it.support_ticket_system.business.category.dto.CategoryResponseDto;
import com.co.support.it.support_ticket_system.business.category.service.CategoryService;
import com.co.support.it.support_ticket_system.generic.controller.GenericCrudControllerImpl;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.co.support.it.support_ticket_system.business.shared.constants.APIEndPoints.CATEGORIES;

@RestController
@RequestMapping(CATEGORIES)
public class CategoryControllerImpl
        extends GenericCrudControllerImpl<CategoryRequestDto, CategoryResponseDto, Long>
        implements CategoryController {

    public CategoryControllerImpl(CategoryService categoryService) {
        super(categoryService, CATEGORIES);
    }
}
