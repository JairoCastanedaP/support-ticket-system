package com.co.support.it.support_ticket_system.business.category.exception;

public class CategoryNotFoundException extends RuntimeException {

    public CategoryNotFoundException(Long id) {
        super("No se encontró la categoría con id: " + id);
    }
}