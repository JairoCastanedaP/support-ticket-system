package com.co.support.it.support_ticket_system.business.category.service;

import com.co.support.it.support_ticket_system.business.category.dto.CategoryRequestDto;
import com.co.support.it.support_ticket_system.business.category.dto.CategoryResponseDto;
import com.co.support.it.support_ticket_system.business.category.entity.CategoryEntity;
import com.co.support.it.support_ticket_system.business.category.exception.CategoryNotFoundException;
import com.co.support.it.support_ticket_system.business.category.mapper.CategoryMapper;
import com.co.support.it.support_ticket_system.business.category.repository.CategoryRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    public CategoryServiceImpl(
            CategoryRepository categoryRepository,
            CategoryMapper categoryMapper
    ) {
        this.categoryRepository = categoryRepository;
        this.categoryMapper = categoryMapper;
    }

    @Override
    public List<CategoryResponseDto> findAll() {
        List<CategoryResponseDto> categories = new ArrayList<>();

        for (CategoryEntity category : categoryRepository.findAll()) {
            categories.add(categoryMapper.toResponseDto(category));
        }

        return categories;
    }

    @Override
    public CategoryResponseDto findById(Long id) {
        Optional<CategoryEntity> categoryOptional =
                categoryRepository.findById(id);

        if (categoryOptional.isEmpty()) {
            throw new CategoryNotFoundException(id);
        }

        return categoryMapper.toResponseDto(categoryOptional.get());
    }

    @Override
    public CategoryResponseDto save(CategoryRequestDto categoryRequestDto) {
        CategoryEntity category =
                categoryMapper.toEntity(categoryRequestDto);

        CategoryEntity savedCategory =
                categoryRepository.save(category);

        return categoryMapper.toResponseDto(savedCategory);
    }
}