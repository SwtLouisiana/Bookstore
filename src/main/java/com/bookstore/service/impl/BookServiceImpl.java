package com.bookstore.service.impl;

import com.bookstore.model.Book;
import com.bookstore.repository.BookRepository;
import com.bookstore.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {
    private final BookRepository bookRepository;
    
    @Override
    public Book save(Book book) {
      return bookRepository.save(book);
    }
    
    @Override
    public List<Book> findAll() {
        return bookRepository.findAll();
    }
}
