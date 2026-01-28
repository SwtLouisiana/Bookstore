package com.bookstore.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.bookstore.dto.book.BookDtoWithoutCategoriesIds;
import com.bookstore.dto.category.CategoryRequestDto;
import com.bookstore.dto.category.CategoryResponseDto;
import com.bookstore.util.TestUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@Sql(scripts = {
        "classpath:database/books/add-three-books-to-books-table.sql",
        "classpath:database/categories/add-three-categories-to-categories-table.sql",
        "classpath:database/books-categories/add-categories-for-books-to-books_categories"
                + "-table.sql"
})
@Sql(scripts = {
        "classpath:database/books-categories/delete-books_categories.sql",
        "classpath:database/categories/delete-categories.sql",
        "classpath:database/books/delete-books.sql"
}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CategoryControllerTest {
    
    protected static MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @BeforeAll
    static void beforeAll(@Autowired WebApplicationContext applicationContext) {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(applicationContext)
                .apply(springSecurity())
                .build();
    }
    
    @Test
    @DisplayName("""
            createCategory() creates a new category when request is valid and user has ADMIN role
            """)
    @WithMockUser(username = "admin", roles = "ADMIN")
    void createCategory_ValidRequest_AsAdmin_ReturnsCreatedCategory_201() throws Exception {
        CategoryRequestDto requestDto = TestUtil.getCyberpunkCategoryRequestDto();
        CategoryResponseDto expectedResponseDto = TestUtil.convertToCategoryResponseDto(requestDto);
        
        String jsonRequest = objectMapper.writeValueAsString(requestDto);
        
        MvcResult result = mockMvc.perform(post("/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();
        
        CategoryResponseDto actualResponseDto = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                CategoryResponseDto.class
        );
        
        assertNotNull(actualResponseDto.getId());
        assertThat(actualResponseDto)
                .usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(expectedResponseDto);
    }
    
    @Test
    @DisplayName("""
            getAll() returns a paged list of categories for an existing categories data set
            """)
    @WithMockUser(username = "user")
    void getAll_ExistingCategories_ReturnsPagedCategories_200() throws Exception {
        List<CategoryResponseDto> expectedCategories = TestUtil.getAllCategoriesResponseDto();
        
        MvcResult result = mockMvc.perform(get("/categories")
                        .param("page", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();
        
        String json = result.getResponse().getContentAsString();
        JsonNode root = objectMapper.readTree(json);
        JsonNode categoriesJson = root.get("content");
        
        List<CategoryResponseDto> actualCategories = objectMapper.convertValue(
                categoriesJson,
                new TypeReference<>() {}
        );
        
        assertEquals(3, actualCategories.size());
        
        assertThat(actualCategories)
                .usingRecursiveComparison()
                .isEqualTo(expectedCategories);
    }
    
    @Test
    @DisplayName("""
            getCategoryById() returns the category when ID exists
            """)
    @WithMockUser(username = "user")
    void getCategoryById_ExistingCategory_ReturnsCategory_200() throws Exception {
        Long categoryId = 1L;
        CategoryResponseDto expectedCategory = TestUtil.getFantasyCategoryResponseDto();
        
        MvcResult result = mockMvc.perform(get("/categories/{id}", categoryId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();
        
        CategoryResponseDto actualCategory = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                CategoryResponseDto.class
        );
        
        assertEquals(expectedCategory, actualCategory);
    }
    
    @Test
    @DisplayName("""
            updateCategory() updates an existing category when request is valid
             and user has ADMIN role
            """)
    @WithMockUser(username = "admin", roles = "ADMIN")
    void updateCategory_ValidRequest_ExistingCategory_AsAdmin_ReturnsUpdatedCategory_200()
            throws Exception {
        Long categoryId = 1L;
        
        CategoryRequestDto requestDto = TestUtil.getUpdatedFantasyCategoryRequestDto();
        CategoryResponseDto expectedCategory = TestUtil.convertToCategoryResponseDto(requestDto);
        expectedCategory.setId(categoryId);
        
        String jsonRequest = objectMapper.writeValueAsString(requestDto);
        
        MvcResult result = mockMvc.perform(put("/categories/{id}", categoryId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();
        
        CategoryResponseDto actualCategory = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                CategoryResponseDto.class
        );
        
        assertEquals(expectedCategory, actualCategory);
    }
    
    @Test
    @DisplayName("""
            updateCategory() returns 404 when category ID does not exist and user has ADMIN role
            """)
    @WithMockUser(username = "admin", roles = "ADMIN")
    void updateCategory_ValidRequest_NonExistingCategory_AsAdmin_Returns404() throws Exception {
        Long nonExistingId = 999L;
        
        CategoryRequestDto requestDto = TestUtil.getUpdatedFantasyCategoryRequestDto();
        
        String jsonRequest = objectMapper.writeValueAsString(requestDto);
        
        mockMvc.perform(put("/categories/{id}", nonExistingId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isNotFound());
    }
    
    @Test
    @DisplayName("""
            deleteCategory() deletes an existing category when user has ADMIN role
            """)
    @WithMockUser(username = "admin", roles = "ADMIN")
    void deleteCategory_ExistingCategory_AsAdmin_ReturnsNoContent_204() throws Exception {
        Long categoryId = 1L;
        
        mockMvc.perform(delete("/categories/{id}", categoryId))
                .andExpect(status().isNoContent());
    }
    
    @Test
    @DisplayName("""
            getBooksByCategoryId() returns a paged list of books for an existing category
            """)
    @WithMockUser(username = "user")
    void getBooksByCategoryId_ExistingCategory_ReturnsPagedBooks_200() throws Exception {
        Long categoryId = 1L;
        
        BookDtoWithoutCategoriesIds expectedBook = TestUtil.getTheHobbitDtoWithoutCategories();
        
        MvcResult result = mockMvc.perform(get("/categories/{id}/books", categoryId)
                        .param("page", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();
        
        String json = result.getResponse().getContentAsString();
        JsonNode root = objectMapper.readTree(json);
        JsonNode booksJson = root.get("content");
        
        List<BookDtoWithoutCategoriesIds> actualBooks = objectMapper.convertValue(
                booksJson,
                new TypeReference<List<BookDtoWithoutCategoriesIds>>() {}
        );
        
        assertNotNull(actualBooks);
        assertTrue(actualBooks.size() > 0);
        
        assertThat(actualBooks.get(0))
                .usingRecursiveComparison()
                .isEqualTo(expectedBook);
    }
    
}
