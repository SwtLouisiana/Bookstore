package com.bookstore.mapper;

import com.bookstore.config.MapperConfig;
import com.bookstore.dto.category.CategoryRequestDto;
import com.bookstore.dto.category.CategoryResponseDto;
import com.bookstore.model.Category;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(config = MapperConfig.class)
public interface CategoryMapper {
    Category toEntity(CategoryRequestDto categoryRequestDto);
    
    CategoryResponseDto toResponseDto(Category category);
    
    void updateCategoryFromDto(CategoryRequestDto categoryRequestDto,
                               @MappingTarget Category category);
}
