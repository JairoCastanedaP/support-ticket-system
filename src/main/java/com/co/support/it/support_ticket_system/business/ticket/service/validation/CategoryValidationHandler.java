package com.co.support.it.support_ticket_system.business.ticket.service.validation;

import com.co.support.it.support_ticket_system.business.category.entity.CategoryEntity;
import com.co.support.it.support_ticket_system.business.category.exception.CategoryNotFoundException;
import com.co.support.it.support_ticket_system.business.category.repository.CategoryRepository;
import com.co.support.it.support_ticket_system.generic.exception.BusinessRuleException;
import org.springframework.stereotype.Component;

/**
 * La categoría debe existir (404) y estar activa (409).
 */
@Component
public class CategoryValidationHandler extends TicketValidationHandler {

    private final CategoryRepository categoryRepository;

    public CategoryValidationHandler(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    protected void check(TicketValidationContext context) {
        Long id = context.getRequest().categoryId();
        CategoryEntity category = categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException(id));

        if (!category.isActive()) {
            throw new BusinessRuleException("La categoría con id " + id + " está inactiva");
        }
        context.setCategory(category);
    }
}
