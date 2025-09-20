package com.bookstore.controller;

import com.bookstore.dto.book.BookDtoWithoutCategoriesIds;
import com.bookstore.dto.category.CategoryRequestDto;
import com.bookstore.dto.category.CategoryResponseDto;
import com.bookstore.service.BookService;
import com.bookstore.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Category management", description = "Endpoint for managing categories")
@RequiredArgsConstructor
@RestController
@RequestMapping("/categories")
public class CategoryController {
    private final CategoryService categoryService;
    private final BookService bookService;
    
    @Operation(
            summary = "Create a new category",
            description = "Creates a new category. Requires ADMIN role",
            responses = {
                    @ApiResponse(responseCode = "201",
                            description = "Category created successfully"),
                    @ApiResponse(responseCode = "403", description = "Access denied")
            }
    )
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public CategoryResponseDto createCategory(CategoryRequestDto categoryRequestDto) {
        return categoryService.save(categoryRequestDto);
    }
    
    @Operation(
            summary = "Get all categories",
            description = "Returns a paginated list of all categories",
            responses = {
                    @ApiResponse(responseCode = "200",
                            description = "List of categories retrieved successfully")
            }
    )
    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    public Page<CategoryResponseDto> getAll(Pageable pageable) {
        return categoryService.findAll(pageable);
    }
    
    @Operation(
            summary = "Get category by ID",
            description = "Returns a single category by its ID",
            responses = {
                    @ApiResponse(responseCode = "200",
                            description = "Category retrieved successfully"),
                    @ApiResponse(responseCode = "404", description = "Category not found")
            }
    )
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{id}")
    public CategoryResponseDto getCategoryById(@PathVariable Long id) {
        return categoryService.getById(id);
    }
    
    @Operation(
            summary = "Update category",
            description = "Updates an existing category by ID. Requires ADMIN role",
            responses = {
                    @ApiResponse(responseCode = "200",
                            description = "Category updated successfully"),
                    @ApiResponse(responseCode = "403", description = "Access denied"),
                    @ApiResponse(responseCode = "404", description = "Category not found")
            }
    )
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/{id}")
    public CategoryResponseDto updateCategory(@PathVariable Long id, @RequestBody
            @Valid CategoryRequestDto categoryRequestDto) {
        return categoryService.update(id, categoryRequestDto);
    }
    
    @Operation(
            summary = "Delete category",
            description = "Deletes a category by ID. Requires ADMIN role",
            responses = {
                    @ApiResponse(responseCode = "204",
                            description = "Category deleted successfully"),
                    @ApiResponse(responseCode = "403", description = "Access denied"),
                    @ApiResponse(responseCode = "404", description = "Category not found")
            }
    )
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public void deleteCategory(@PathVariable Long id) {
        categoryService.deleteById(id);
    }
    
    @Operation(
            summary = "Get books by category ID",
            description = "Returns a paginated list of books for a given category",
            responses = {
                    @ApiResponse(responseCode = "200",
                            description = "Books retrieved successfully"),
                    @ApiResponse(responseCode = "404", description = "Category not found")
            }
    )
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{id}/books")
    public Page<BookDtoWithoutCategoriesIds> getBooksByCategoryId(
            @PathVariable Long id, Pageable pageable) {
        return bookService.findAllByCategoryId(id, pageable);
    }
}
