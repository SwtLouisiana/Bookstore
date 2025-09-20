package com.bookstore.service.impl;

import com.bookstore.dto.category.CategoryRequestDto;
import com.bookstore.dto.category.CategoryResponseDto;
import com.bookstore.exception.EntityNotFoundException;
import com.bookstore.mapper.CategoryMapper;
import com.bookstore.model.Category;
import com.bookstore.repository.CategoryRepository;
import com.bookstore.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;
    
    @Override
    public Page<CategoryResponseDto> findAll(Pageable pageable) {
        return categoryRepository.findAll(pageable).map(categoryMapper::toResponseDto);
    }
    
    @Override
    public CategoryResponseDto getById(Long id) {
        Category category = categoryRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Can't find Category with id: " + id));
        return categoryMapper.toResponseDto(category);
    }
    
    @Override
    public CategoryResponseDto save(CategoryRequestDto categoryRequestDto) {
        return categoryMapper.toResponseDto(categoryRepository
                .save(categoryMapper.toEntity(categoryRequestDto)));
    }
    
    @Override
    public CategoryResponseDto update(Long id, CategoryRequestDto categoryRequestDto) {
        Category category = categoryRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Can't find Category with id: " + id));
        categoryMapper.updateCategoryFromDto(categoryRequestDto, category);
        Category updatedCategory = categoryRepository.save(category);
        return categoryMapper.toResponseDto(updatedCategory);
    }
    
    @Override
    public void deleteById(Long id) {
        Category category = categoryRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Can't find Category with id: " + id));
        categoryRepository.delete(category);
    }
}
