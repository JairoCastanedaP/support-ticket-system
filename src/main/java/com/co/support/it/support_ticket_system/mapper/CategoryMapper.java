package com.co.support.it.support_ticket_system.mapper;


import com.co.support.it.support_ticket_system.dto.CategoryRequestDto;
import com.co.support.it.support_ticket_system.dto.CategoryResponseDto;
import com.co.support.it.support_ticket_system.entity.CategoryEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    CategoryEntity toEntity(CategoryRequestDto categoryRequestDto);

    CategoryResponseDto toResponseDto(CategoryEntity categoryEntity);
}