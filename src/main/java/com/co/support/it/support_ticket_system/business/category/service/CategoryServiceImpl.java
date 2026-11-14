package com.co.support.it.support_ticket_system.business.category.service;

import com.co.support.it.support_ticket_system.business.category.dto.CategoryRequestDto;
import com.co.support.it.support_ticket_system.business.category.dto.CategoryResponseDto;
import com.co.support.it.support_ticket_system.business.category.entity.CategoryEntity;
import com.co.support.it.support_ticket_system.business.category.exception.CategoryNotFoundException;
import com.co.support.it.support_ticket_system.business.category.mapper.CategoryMapper;
import com.co.support.it.support_ticket_system.business.category.repository.CategoryRepository;
import com.co.support.it.support_ticket_system.generic.exception.DuplicateException;
import com.co.support.it.support_ticket_system.generic.exception.NotFoundException;
import com.co.support.it.support_ticket_system.generic.service.GenericServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceImpl
        extends GenericServiceImpl<CategoryEntity, CategoryRequestDto, CategoryResponseDto, Long>
        implements CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryServiceImpl(
            CategoryRepository categoryRepository,
            CategoryMapper categoryMapper
    ) {
        super(
                categoryRepository,
                categoryMapper::toEntity,
                categoryMapper::toResponseDto,
                categoryMapper::updateEntity
        );
        this.categoryRepository = categoryRepository;
    }

    @Override
    protected NotFoundException createNotFoundException(Long id) {
        return new CategoryNotFoundException(id);
    }

    @Override
    protected void validateUniqueness(CategoryRequestDto request, Long currentId) {
        boolean duplicated = currentId == null
                ? categoryRepository.existsByNameIgnoreCase(request.name())
                : categoryRepository.existsByNameIgnoreCaseAndIdNot(request.name(), currentId);

        if (duplicated) {
            throw new DuplicateException(
                    "Ya existe un registro de categoría con el nombre: " + request.name()
            );
        }
    }
}
