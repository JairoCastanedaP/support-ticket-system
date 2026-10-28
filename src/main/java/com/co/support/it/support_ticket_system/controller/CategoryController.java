package com.co.support.it.support_ticket_system.controller;

import com.co.support.it.support_ticket_system.dto.CategoryRequestDto;
import com.co.support.it.support_ticket_system.dto.CategoryResponseDto;
import com.co.support.it.support_ticket_system.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public ResponseEntity<List<CategoryResponseDto>> findAll() {
        return ResponseEntity.ok(categoryService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponseDto> findById(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(categoryService.findById(id));
    }

    @PostMapping
    public ResponseEntity<CategoryResponseDto> save(
            @Valid @RequestBody CategoryRequestDto categoryRequestDto
    ) {
        CategoryResponseDto savedCategory =
                categoryService.save(categoryRequestDto);

        URI location = URI.create(
                "/api/v1/categories/" + savedCategory.id()
        );

        return ResponseEntity.created(location).body(savedCategory);
    }
}