package com.co.support.it.support_ticket_system.business.category.exception;

import com.co.support.it.support_ticket_system.generic.exception.NotFoundException;

public class CategoryNotFoundException extends NotFoundException {

    public CategoryNotFoundException(Long id) {
        super("No se encontró la categoría con id: " + id);
    }
}