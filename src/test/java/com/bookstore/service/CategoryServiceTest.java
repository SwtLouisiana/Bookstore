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
import com.bookstore.util.TestUtil;
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
        
        Category fantasyCategory = TestUtil.getFantasyCategory();
        Category scienceFictionCategory = TestUtil.getScienceFictionCategory();
        
        CategoryResponseDto fantasyDto = TestUtil.getFantasyCategoryResponseDto();
        CategoryResponseDto scienceFictionDto = TestUtil.getScienceFictionCategoryResponseDto();
        
        Pageable pageable = PageRequest.of(0, 10);
        Page<Category> categoryPage = new PageImpl<>(
                List.of(fantasyCategory, scienceFictionCategory),
                pageable,
                2
        );
        
        when(categoryRepository.findAll(pageable)).thenReturn(categoryPage);
        when(categoryMapper.toResponseDto(fantasyCategory)).thenReturn(fantasyDto);
        when(categoryMapper.toResponseDto(scienceFictionCategory)).thenReturn(scienceFictionDto);
        
        Page<CategoryResponseDto> resultPage = categoryService.findAll(pageable);
        
        Page<CategoryResponseDto> expectedPage = new PageImpl<>(
                List.of(fantasyDto, scienceFictionDto),
                pageable,
                2
        );
        
        assertEquals(expectedPage, resultPage);
        
        verify(categoryRepository).findAll(pageable);
        verify(categoryMapper).toResponseDto(fantasyCategory);
        verify(categoryMapper).toResponseDto(scienceFictionCategory);
        
    }
    
    @Test
    @DisplayName("""
            getById() returns the correct CategoryResponseDto for an existing ID
            """)
    void getById_ExistingId_ReturnsCategory() {
        
        Category fantasyCategory = TestUtil.getFantasyCategory();
        CategoryResponseDto fantasyDto = TestUtil.getFantasyCategoryResponseDto();
        
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(fantasyCategory));
        when(categoryMapper.toResponseDto(fantasyCategory)).thenReturn(fantasyDto);
        
        CategoryResponseDto result = categoryService.getById(1L);
        
        assertEquals(fantasyDto, result);
        
        verify(categoryRepository).findById(1L);
        verify(categoryMapper).toResponseDto(fantasyCategory);
        
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
        
        CategoryRequestDto requestDto = TestUtil.getFantasyCategoryRequestDto();
        Category fantasyCategory = TestUtil.getFantasyCategory();
        CategoryResponseDto fantasyDto = TestUtil.getFantasyCategoryResponseDto();
        
        when(categoryMapper.toEntity(requestDto)).thenReturn(fantasyCategory);
        when(categoryRepository.save(fantasyCategory)).thenReturn(fantasyCategory);
        when(categoryMapper.toResponseDto(fantasyCategory)).thenReturn(fantasyDto);
        
        CategoryResponseDto result = categoryService.save(requestDto);
        
        assertEquals(fantasyDto, result);
        
        verify(categoryMapper).toEntity(requestDto);
        verify(categoryRepository).save(fantasyCategory);
        verify(categoryMapper).toResponseDto(fantasyCategory);
        
    }
    
    @Test
    @DisplayName("""
            update() updates existing category and returns updated DTO
            """)
    void update_ExistingId_UpdatesAndReturnsCategory() {
        
        Long categoryId = 1L;
        CategoryRequestDto requestDto = TestUtil.getUpdatedFantasyCategoryRequestDto();
        
        Category oldCategory = TestUtil.getFantasyCategory();
        
        CategoryResponseDto updatedDto = TestUtil.convertToCategoryResponseDto(requestDto);
        updatedDto.setId(categoryId);
        
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(oldCategory));
        doNothing().when(categoryMapper).updateCategoryFromDto(requestDto, oldCategory);
        when(categoryRepository.save(oldCategory)).thenReturn(oldCategory);
        when(categoryMapper.toResponseDto(oldCategory)).thenReturn(updatedDto);
        
        CategoryResponseDto result = categoryService.update(categoryId, requestDto);
        
        assertEquals(updatedDto, result);
        
        verify(categoryRepository).findById(categoryId);
        verify(categoryMapper).updateCategoryFromDto(requestDto, oldCategory);
        verify(categoryRepository).save(oldCategory);
        verify(categoryMapper).toResponseDto(oldCategory);
    }
    
    @Test
    @DisplayName("""
            update() throws EntityNotFoundException when category with given ID does not exist
            """)
    void update_NonExistingId_ThrowsEntityNotFoundException() {
        
        CategoryRequestDto requestDto = TestUtil.getFantasyCategoryRequestDto();
        Long nonExistingId = 1L;
        
        when(categoryRepository.findById(nonExistingId)).thenReturn(Optional.empty());
        
        assertThrows(EntityNotFoundException.class,
                () -> categoryService.update(nonExistingId, requestDto));
        
        verify(categoryRepository).findById(nonExistingId);
        verifyNoInteractions(categoryMapper);
        verify(categoryRepository, never()).save(any());
    }
    
    @Test
    @DisplayName("""
            deleteById() deletes category when category with given ID exists
            """)
    void deleteById_ExistingId_DeletesCategory() {
        
        Long categoryId = 1L;
        
        categoryService.deleteById(categoryId);
        
        verify(categoryRepository).deleteById(categoryId);
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
