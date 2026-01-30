package com.bookstore.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.bookstore.model.Book;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(locations = "classpath:application.properties")
class BookRepositoryTest {
    
    @Autowired
    private BookRepository bookRepository;
    
    @Test
    @DisplayName("""
            Find all books by existing category id returns matching books
            """)
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
    void findAllByCategories_Id_ExistingCategoryId_ReturnsMatchingBooks() {
        Long categoryId = 1L;
        
        Page<Book> result = bookRepository.findAllByCategories_Id(categoryId, Pageable.unpaged());
        
        assertThat(result).isNotEmpty();
        assertThat(result.getContent())
                .extracting(Book::getTitle)
                .containsExactly("The Hobbit");
    }
}
