package com.bookstore.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.bookstore.dto.book.BookDto;
import com.bookstore.dto.book.CreateBookRequestDto;
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
class BookControllerTest {
    
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
            findAll() returns paged books when books exist
            """)
    @WithMockUser(username = "user")
    void findAll_ExistingBooks_ReturnsPagedBooks_200() throws Exception {
        List<BookDto> expectedBooks = TestUtil.getAllBooksDto();
        
        MvcResult result = mockMvc.perform(get("/books")
                        .param("page", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();
        
        String json = result.getResponse().getContentAsString();
        JsonNode root = objectMapper.readTree(json);
        JsonNode booksJson = root.get("content");
        
        List<BookDto> actualBooks = objectMapper.convertValue(
                booksJson,
                new TypeReference<>() {
                }
        );
        
        assertEquals(3, actualBooks.size());
        
        assertThat(actualBooks)
                .usingRecursiveComparison()
                .isEqualTo(expectedBooks);
    }
    
    @Test
    @DisplayName("""
            getById() returns the correct book for an existing ID
            """)
    @WithMockUser(username = "user")
    void getById_ExistingBook_ReturnsBook_200() throws Exception {
        BookDto expectedBookDto = TestUtil.getTheHobbitBookDto();
        
        MvcResult result = mockMvc.perform(get("/books/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();
        
        BookDto actualBookDto = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                BookDto.class
        );
        
        assertEquals(expectedBookDto, actualBookDto);
    }
    
    @Test
    @DisplayName("""
            getById() returns 404 NOT FOUND when the book does not exist
            """)
    @WithMockUser(username = "user")
    void getById_NonExistingBook_Returns404() throws Exception {
        Long nonExistingBookId = 999L;
        
        mockMvc.perform(get("/books/{id}", nonExistingBookId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
    
    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    @DisplayName("""
            save() with valid request as ADMIN returns created BookDto
            """)
    void save_ValidRequestDto_AsAdmin_ReturnsSavedBook_200() throws Exception {
        CreateBookRequestDto requestDto = TestUtil.getCreateLordOfTheRingsRequestDto();
        BookDto expectedBookDto = TestUtil.convertToBookDto(requestDto);
        
        String jsonRequest = objectMapper.writeValueAsString(requestDto);
        
        MvcResult result = mockMvc.perform(post("/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();
        
        BookDto actualBookDto = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                BookDto.class
        );
        
        assertNotNull(actualBookDto.getId());
        assertThat(actualBookDto)
                .usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(expectedBookDto);
    }
    
    @Test
    @DisplayName("""
            delete() deletes an existing book for ADMIN and returns 204 No Content
            """)
    @WithMockUser(username = "admin", roles = "ADMIN")
    void deleteById_ExistingBook_AsAdmin_Returns204() throws Exception {
        long existingBookId = 1L;
        
        mockMvc.perform(delete("/books/{id}", existingBookId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
        
        mockMvc.perform(get("/books/{id}", existingBookId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
    
    @Test
    @DisplayName("""
            updateBook() updates an existing book and returns the updated bookDto (200)
            """)
    @WithMockUser(username = "admin", roles = "ADMIN")
    void updateBook_ValidRequestDto_AsAdmin_ReturnsUpdatedBookDto_200() throws Exception {
        Long bookId = 1L;
        
        CreateBookRequestDto requestDto = TestUtil.getUpdateCreateBookRequestDto();
        BookDto expectedBookDto = TestUtil.convertToBookDto(requestDto);
        expectedBookDto.setId(bookId);
        
        String jsonRequest = objectMapper.writeValueAsString(requestDto);
        
        MvcResult result = mockMvc.perform(put("/books/{id}", bookId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();
        
        BookDto actualBookDto = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                BookDto.class
        );
        
        assertEquals(expectedBookDto, actualBookDto);
        
    }
    
    @Test
    @DisplayName("""
            updateBook() with a non-existing id returns 404
            """)
    @WithMockUser(username = "admin", roles = "ADMIN")
    void updateBook_NonExistingId_Returns404() throws Exception {
        
        CreateBookRequestDto requestDto = TestUtil.getNonExistingBookRequestDto();
        Long nonExistingId = 999L;
        
        String jsonRequest = objectMapper.writeValueAsString(requestDto);
        
        mockMvc.perform(put("/books/{id}", nonExistingId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isNotFound());
    }
}
