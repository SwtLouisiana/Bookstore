package com.bookstore.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.bookstore.dto.category.CategoryRequestDto;
import com.bookstore.dto.category.CategoryResponseDto;
import com.bookstore.exception.EntityNotFoundException;
import com.bookstore.mapper.CategoryMapper;
import com.bookstore.model.Category;
import com.bookstore.repository.CategoryRepository;
import com.bookstore.service.impl.CategoryServiceImpl;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {
    
    @Mock
    private CategoryRepository categoryRepository;
    
    @Mock
    private CategoryMapper categoryMapper;
    
    @InjectMocks
    private CategoryServiceImpl categoryService;
    
    @Test
    @DisplayName("""
            findAll() returns a paged list of categories with correct total elements and DTOs
            """)
    void findAll_ValidPageRequest_ReturnsPagedCategoryList() {
        
        Category dramaCategory = new Category();
        dramaCategory.setId(1L);
        dramaCategory.setName("Drama");
        dramaCategory.setDescription("Dramatic");
        
        Category fantasyCategory = new Category();
        fantasyCategory.setId(2L);
        fantasyCategory.setName("Fantasy");
        fantasyCategory.setDescription("Fantasy");
        
        CategoryResponseDto dramaCategoryResponseDto = new CategoryResponseDto();
        dramaCategoryResponseDto.setId(1L);
        dramaCategoryResponseDto.setName("Drama");
        dramaCategoryResponseDto.setDescription("Dramatic");
        
        CategoryResponseDto fantasyCategoryResponseDto = new CategoryResponseDto();
        fantasyCategoryResponseDto.setId(2L);
        fantasyCategoryResponseDto.setName("Fantasy");
        fantasyCategoryResponseDto.setDescription("Fantasy");
        
        Pageable pageable = PageRequest.of(0, 10);
        Page<Category> categoryPage = new PageImpl<>(List.of(dramaCategory, fantasyCategory),
                pageable, 2);
        
        when(categoryRepository.findAll(pageable)).thenReturn(categoryPage);
        when(categoryMapper.toResponseDto(dramaCategory)).thenReturn(dramaCategoryResponseDto);
        when(categoryMapper.toResponseDto(fantasyCategory)).thenReturn(fantasyCategoryResponseDto);
        
        Page<CategoryResponseDto> resultPage = categoryService.findAll(pageable);
        
        Page<CategoryResponseDto> expectedPage = new PageImpl<>(
                List.of(dramaCategoryResponseDto, fantasyCategoryResponseDto), pageable, 2);
        
        assertEquals(expectedPage, resultPage);
        
        verify(categoryRepository).findAll(pageable);
        verify(categoryMapper).toResponseDto(dramaCategory);
        verify(categoryMapper).toResponseDto(fantasyCategory);
        
    }
    
    @Test
    @DisplayName("""
            getById() returns the correct CategoryResponseDto for an existing ID
            """)
    void getById_ExistingId_ReturnsCategory() {
        Category dramaCategory = new Category();
        dramaCategory.setId(1L);
        dramaCategory.setName("Drama");
        dramaCategory.setDescription("Dramatic");
        
        CategoryResponseDto dramaCategoryResponseDto = new CategoryResponseDto();
        dramaCategoryResponseDto.setId(1L);
        dramaCategoryResponseDto.setName("Drama");
        dramaCategoryResponseDto.setDescription("Dramatic");
        
        when(categoryRepository.findById(anyLong())).thenReturn(Optional.of(dramaCategory));
        when(categoryMapper.toResponseDto(dramaCategory)).thenReturn(dramaCategoryResponseDto);
        
        CategoryResponseDto categoryResponseDto = categoryService.getById(1L);
        
        assertEquals(dramaCategoryResponseDto, categoryResponseDto);
        
        verify(categoryRepository).findById(anyLong());
        verify(categoryMapper).toResponseDto(dramaCategory);
        
    }
    
    @Test
    @DisplayName("""
            getById() throws EntityNotFoundException for a non-existing ID
            """)
    void getById_NonExistingId_ThrowsEntityNotFoundException() {
        when(categoryRepository.findById(anyLong())).thenReturn(Optional.empty());
        
        assertThrows(EntityNotFoundException.class, () -> categoryService.getById(1L));
        
        verify(categoryRepository).findById(anyLong());
        
    }
    
    @Test
    @DisplayName("""
            save() persists a valid category and returns the saved CategoryResponseDto
            """)
    void save_ValidCategoryRequest_SavesAndReturnsCategory() {
        CategoryRequestDto categoryRequestDto = new CategoryRequestDto();
        categoryRequestDto.setName("Drama");
        categoryRequestDto.setDescription("Dramatic");
        
        Category dramaCategory = new Category();
        dramaCategory.setId(1L);
        dramaCategory.setName("Drama");
        dramaCategory.setDescription("Dramatic");
        
        CategoryResponseDto categoryResponseDto = new CategoryResponseDto();
        categoryResponseDto.setId(1L);
        categoryResponseDto.setName("Drama");
        categoryResponseDto.setDescription("Dramatic");
        
        when(categoryMapper.toEntity(categoryRequestDto)).thenReturn(dramaCategory);
        when(categoryRepository.save(dramaCategory)).thenReturn(dramaCategory);
        when(categoryMapper.toResponseDto(dramaCategory)).thenReturn(categoryResponseDto);
        
        CategoryResponseDto responseDto = categoryService.save(categoryRequestDto);
        
        assertEquals(categoryResponseDto, responseDto);
        
        verify(categoryRepository).save(dramaCategory);
        verify(categoryMapper).toResponseDto(any(Category.class));
        
    }
    
    @Test
    @DisplayName("""
            update() updates existing category and returns updated DTO
            """)
    void update_ExistingId_UpdatesAndReturnsCategory() {
        CategoryRequestDto categoryRequestDto = new CategoryRequestDto();
        categoryRequestDto.setName("Drama");
        categoryRequestDto.setDescription("Dramatic");
        
        Category dramaCategory = new Category();
        dramaCategory.setId(1L);
        dramaCategory.setName("Old name");
        dramaCategory.setDescription("Old description");
        
        CategoryResponseDto categoryResponseDto = new CategoryResponseDto();
        categoryResponseDto.setId(1L);
        categoryResponseDto.setName("Drama");
        categoryResponseDto.setDescription("Dramatic");
        
        when(categoryRepository.findById(anyLong())).thenReturn(Optional.of(dramaCategory));
        doNothing().when(categoryMapper).updateCategoryFromDto(categoryRequestDto, dramaCategory);
        when(categoryRepository.save(dramaCategory)).thenReturn(dramaCategory);
        when(categoryMapper.toResponseDto(dramaCategory)).thenReturn(categoryResponseDto);
        
        CategoryResponseDto responseDto = categoryService.update(dramaCategory.getId(),
                categoryRequestDto);
        
        assertEquals(categoryResponseDto, responseDto);
        
        verify(categoryRepository).findById(anyLong());
        verify(categoryMapper).updateCategoryFromDto(categoryRequestDto, dramaCategory);
        verify(categoryRepository).save(dramaCategory);
        verify(categoryMapper).toResponseDto(any(Category.class));
        
    }
    
    @Test
    @DisplayName("""
            update() throws EntityNotFoundException when category with given ID does not exist
            """)
    void update_NonExistingId_ThrowsEntityNotFoundException() {
        when(categoryRepository.findById(anyLong())).thenReturn(Optional.empty());
        
        assertThrows(EntityNotFoundException.class, () -> categoryService.update(1L,
                new CategoryRequestDto()));
        
        verify(categoryRepository).findById(anyLong());
        verifyNoInteractions(categoryMapper);
        verify(categoryRepository, never()).save(any());
        
    }
    
    @Test
    @DisplayName("""
            deleteById() deletes category when category with given ID exists
            """)
    void deleteById_ExistingId_DeletesCategory() {
        Category dramaCategory = new Category();
        dramaCategory.setId(1L);
        dramaCategory.setName("Drama");
        dramaCategory.setDescription("Dramatic");
        
        categoryService.deleteById(1L);
        
        verify(categoryRepository).deleteById(1L);
        verify(categoryRepository, never()).save(any());
        
    }
    
    @Test
    @DisplayName("""
            deleteById() throws EntityNotFoundException when category with given ID does not exist
            """)
    void deleteById_NonExistingId_DoesNotThrowException() {
        Long nonExistingId = 999L;
        
        assertDoesNotThrow(() -> categoryService.deleteById(nonExistingId));
        
        verify(categoryRepository).deleteById(nonExistingId);
        
    }
    
}
