package com.bookstore.service;

import com.bookstore.dto.book.BookDto;
import com.bookstore.dto.book.CreateBookRequestDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BookService {
    BookDto save(CreateBookRequestDto requestDto);
    
    Page<BookDto> findAll(Pageable pageable);
    
    BookDto findById(Long id);
    
    void deleteById(Long id);
    
    BookDto updateBook(CreateBookRequestDto requestDto, Long id);
}
