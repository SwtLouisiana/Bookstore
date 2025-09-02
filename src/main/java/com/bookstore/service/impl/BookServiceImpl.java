package com.bookstore.service.impl;

import com.bookstore.dto.BookDto;
import com.bookstore.dto.CreateBookRequestDto;
import com.bookstore.exception.EntityNotFoundException;
import com.bookstore.mapper.BookMapper;
import com.bookstore.model.Book;
import com.bookstore.repository.BookRepository;
import com.bookstore.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {
    private final BookRepository bookRepository;
    
    private final BookMapper bookMapper;
    
    @Override
    public BookDto save(CreateBookRequestDto requestDto) {
        Book book = bookMapper.toModel(requestDto);
        return bookMapper.toDto(bookRepository.save(book));
    }
    
    @Override
    public Page<BookDto> findAll(Pageable pageable) {
        return bookRepository.findAll(pageable)
                .map(bookMapper::toDto);
    }
    
    @Override
    public BookDto findById(Long id) {
        Book book = bookRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Can't find book by id" + id)
        );
        return bookMapper.toDto(book);
    }
    
    @Override
    public void deleteById(Long id) {
        bookRepository.deleteById(id);
    }
    
    @Override
    public BookDto updateBook(CreateBookRequestDto requestDto, Long id) {
        Book book = bookRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(
                "Can't find book by id" + id));
        bookMapper.updateBook(requestDto, book);
        bookRepository.save(book);
        return bookMapper.toDto(book);
    }
}
