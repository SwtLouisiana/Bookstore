package com.bookstore.controller;

import com.bookstore.dto.BookDto;
import com.bookstore.dto.CreateBookRequestDto;
import com.bookstore.service.BookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Book management", description = "Endpoint fot managing books")
@RequiredArgsConstructor
@RestController
@RequestMapping("/books")
public class BookController {
    
    private final BookService bookService;
    
    @GetMapping
    @Operation(summary = "Get all books", description = "Get a list of all available books")
    public Page<BookDto> findAll(Pageable pageable) {
        return bookService.findAll(pageable);
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Get book by ID", description = "Get book by ID")
    public BookDto getBookById(@PathVariable Long id) {
        return bookService.findById(id);
    }
    
    @PostMapping
    @Operation(summary = "Create a new book", description = "Create a new book")
    public BookDto save(@RequestBody @Valid CreateBookRequestDto requestDto) {
        return bookService.save(requestDto);
    }
    
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete book by ID", description = "Delete book by ID")
    public void delete(@PathVariable Long id) {
        bookService.deleteById(id);
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "Update book by ID", description = "Update book by ID")
    public BookDto updateBook(@RequestBody @Valid CreateBookRequestDto requestDto,
                              @PathVariable Long id) {
        return bookService.updateBook(requestDto, id);
    }
}
