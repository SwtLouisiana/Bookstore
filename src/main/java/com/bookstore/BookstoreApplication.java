package com.bookstore;

import com.bookstore.model.Book;
import com.bookstore.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import java.math.BigDecimal;


@SpringBootApplication
public class BookstoreApplication {
    @Autowired
    private BookService bookService;
    
    public static void main(String[] args) {
        SpringApplication.run(BookstoreApplication.class, args);
    }
    
    @Bean
    public CommandLineRunner commandLineRunner() {
        return args -> {
            Book book = new Book();
            book.setTitle("Book Title");
            book.setAuthor("Author");
            book.setDescription("Description");
            book.setIsbn("ISBN");
            book.setCoverImage("Cover Image");
            book.setPrice(BigDecimal.valueOf(1234));
            bookService.save(book);
            bookService.findAll();
        };
    }
}
