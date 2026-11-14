package com.co.support.it.support_ticket_system.business.category.mapper;

import com.co.support.it.support_ticket_system.business.category.dto.CategoryRequestDto;
import com.co.support.it.support_ticket_system.business.category.dto.CategoryResponseDto;
import com.co.support.it.support_ticket_system.business.category.entity.CategoryEntity;
import com.co.support.it.support_ticket_system.generic.mapper.GenericMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface CategoryMapper extends GenericMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    CategoryEntity toEntity(CategoryRequestDto requestDto);

    @Mapping(target = "audit", source = "entity")
    CategoryResponseDto toResponseDto(CategoryEntity entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    void updateEntity(CategoryRequestDto requestDto, @MappingTarget CategoryEntity entity);
}
