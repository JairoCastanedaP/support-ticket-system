package com.co.support.it.support_ticket_system.service;

import com.co.support.it.support_ticket_system.dto.CategoryRequestDto;
import com.co.support.it.support_ticket_system.dto.CategoryResponseDto;

import java.util.List;

public interface CategoryService {

    List<CategoryResponseDto> findAll();

    CategoryResponseDto findById(Long id);

    CategoryResponseDto save(CategoryRequestDto categoryRequestDto);
}