package com.bookstore.service;

import com.bookstore.dto.BookDto;
import com.bookstore.dto.CreateBookRequestDto;
import java.util.List;

public interface BookService {
    BookDto save(CreateBookRequestDto requestDto);
    
    List<BookDto> findAll();
    
    BookDto findById(Long id);
    
    void deleteById(Long id);
    
    BookDto updateBook(CreateBookRequestDto requestDto, Long id);
}
