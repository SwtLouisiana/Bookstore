package com.bookstore.service;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.bookstore.dto.book.BookDto;
import com.bookstore.dto.book.BookDtoWithoutCategoriesIds;
import com.bookstore.dto.book.CreateBookRequestDto;
import com.bookstore.exception.EntityNotFoundException;
import com.bookstore.mapper.BookMapper;
import com.bookstore.model.Book;
import com.bookstore.repository.BookRepository;
import com.bookstore.service.impl.BookServiceImpl;
import com.bookstore.util.TestUtil;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {
    
    @Mock
    private BookRepository bookRepository;
    
    @Mock
    private BookMapper bookMapper;
    
    @InjectMocks
    private BookServiceImpl bookService;
    
    @Test
    @DisplayName("""
            save() persists a valid book and returns the saved BookDto
            """)
    void save_ValidRequestDto_ReturnsSavedBookDto() {
        CreateBookRequestDto requestDto = TestUtil.getUpdateCreateBookRequestDto();
        Book bookHobbit = TestUtil.getTheHobbitBook();
        BookDto bookHobbitDto = TestUtil.getTheHobbitBookDto();
        
        when(bookMapper.toModel(requestDto)).thenReturn(bookHobbit);
        when(bookRepository.save(bookHobbit)).thenReturn(bookHobbit);
        when(bookMapper.toDto(bookHobbit)).thenReturn(bookHobbitDto);
        
        BookDto savedBookDto = bookService.save(requestDto);
        
        assertEquals(bookHobbitDto, savedBookDto);
        
        verify(bookMapper).toModel(requestDto);
        verify(bookRepository).save(bookHobbit);
        verify(bookMapper).toDto(bookHobbit);
    }
    
    @Test
    @DisplayName("""
            findAll() returns a paginated list of BookDto for a valid page request
            """)
    void findAll_ValidPageRequest_ReturnsPagedBookDtoList() {
        Book bookHobbit = TestUtil.getTheHobbitBook();
        Book bookDune = TestUtil.getDuneBook();
        
        BookDto bookHobbitDto = TestUtil.getTheHobbitBookDto();
        BookDto bookDuneDto = TestUtil.getDuneBookDto();
        
        Pageable pageable = PageRequest.of(0, 10);
        Page<Book> bookPage = new PageImpl<>(List.of(bookHobbit, bookDune), pageable, 2);
        
        when(bookRepository.findAll(pageable)).thenReturn(bookPage);
        when(bookMapper.toDto(bookHobbit)).thenReturn(bookHobbitDto);
        when(bookMapper.toDto(bookDune)).thenReturn(bookDuneDto);
        
        Page<BookDto> resultPage = bookService.findAll(pageable);
        
        Page<BookDto> expectedPage = new PageImpl<>(
                List.of(bookHobbitDto, bookDuneDto),
                pageable,
                2
        );
        
        assertEquals(expectedPage, resultPage);
        
        verify(bookRepository).findAll(pageable);
        verify(bookMapper).toDto(bookHobbit);
        verify(bookMapper).toDto(bookDune);
    }
    
    @Test
    @DisplayName("""
            findById() returns BookDto when a book with the given ID exists
            """)
    void findById_ExistingId_ReturnsBookDto() {
        Book bookHobbit = TestUtil.getTheHobbitBook();
        BookDto bookHobbitDto = TestUtil.getTheHobbitBookDto();
        
        when(bookRepository.findById(1L)).thenReturn(Optional.of(bookHobbit));
        when(bookMapper.toDto(bookHobbit)).thenReturn(bookHobbitDto);
        
        BookDto result = bookService.findById(1L);
        
        assertEquals(bookHobbitDto, result);
        
        verify(bookRepository).findById(1L);
        verify(bookMapper).toDto(bookHobbit);
        
    }
    
    @Test
    @DisplayName("""
            findById() throws EntityNotFoundException when a book with the given ID does not exist
            """)
    void findById_NonExistingId_ThrowsEntityNotFoundException() {
        when(bookRepository.findById(anyLong())).thenReturn(Optional.empty());
        
        assertThrows(EntityNotFoundException.class, () -> bookService.findById(1L));
        
        verify(bookRepository).findById(anyLong());
        
    }
    
    @Test
    @DisplayName("""
             deleteById() deletes the book when a valid ID is provided
            """)
    void deleteById_ExistingId_DeletesBook() {
        Long bookId = 1L;
        
        bookService.deleteById(bookId);
        
        verify(bookRepository).deleteById(bookId);
        verify(bookRepository, never()).save(any());
    }
    
    @Test
    @DisplayName("""
            deleteById() does not throw an exception when the book ID does not exist
            """)
    void deleteById_NonExistingId_DoesNotThrowException() {
        Long nonExistingId = 999L;
        
        assertDoesNotThrow(() -> bookService.deleteById(nonExistingId));
        
        verify(bookRepository).deleteById(nonExistingId);
        
    }
    
    @Test
    @DisplayName("""
            updateBook() updates 'The Lord of the Rings' and returns updated BookDto
            """)
    void updateBook_ExistingIdAndValidRequestDto_UpdatesAndReturnsBookDto() {
        
        Long bookId = 1L;
        CreateBookRequestDto requestDto = TestUtil.getUpdateCreateBookRequestDto();
        Book existingBook = TestUtil.getTheHobbitBook();
        BookDto updatedBookDto = TestUtil.convertToBookDto(requestDto);
        updatedBookDto.setId(bookId);
        
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(existingBook));
        doNothing().when(bookMapper).updateBook(requestDto, existingBook);
        when(bookRepository.save(existingBook)).thenReturn(existingBook);
        when(bookMapper.toDto(existingBook)).thenReturn(updatedBookDto);
        
        BookDto result = bookService.updateBook(requestDto, bookId);
        
        assertEquals(updatedBookDto, result);
        
        verify(bookRepository).findById(bookId);
        verify(bookMapper).updateBook(requestDto, existingBook);
        verify(bookRepository).save(existingBook);
        verify(bookMapper).toDto(existingBook);
        
    }
    
    @Test
    @DisplayName("""
            updateBook() throws EntityNotFoundException when book ID does not exist
            """)
    void updateBook_NonExistingId_ThrowsEntityNotFoundException() {
        
        CreateBookRequestDto requestDto = TestUtil.getNonExistingBookRequestDto();
        Long nonExistingId = 999L;
        
        when(bookRepository.findById(nonExistingId)).thenReturn(Optional.empty());
        
        assertThrows(EntityNotFoundException.class,
                () -> bookService.updateBook(requestDto, nonExistingId));
        
        verify(bookRepository).findById(nonExistingId);
        verify(bookMapper, never()).updateBook(any(), any());
        verify(bookRepository, never()).save(any());
        verify(bookMapper, never()).toDto(any());
    }
    
    @Test
    @DisplayName("""
            findAllByCategoryId() returns a paged list of books (without category IDs) for a valid
             category ID and pageable
            """)
    void findAllByCategoryId_ValidCategoryIdAndPageable_ReturnsPagedBookDtoWithoutCategories() {
        
        Book bookHobbit = TestUtil.getTheHobbitBook();
        Book bookLotr = TestUtil.getLordOfTheRingsBook();
        
        BookDtoWithoutCategoriesIds hobbitDto = TestUtil.getTheHobbitDtoWithoutCategories();
        BookDtoWithoutCategoriesIds lotrDto = TestUtil.getLordOfTheRingsDtoWithoutCategories();
        
        Pageable pageable = PageRequest.of(0, 10);
        Page<Book> bookPage = new PageImpl<>(List.of(bookHobbit, bookLotr), pageable, 2);
        Long categoryId = 1L;
        
        when(bookRepository.findAllByCategories_Id(categoryId, pageable)).thenReturn(bookPage);
        when(bookMapper.toDtoWithoutCategories(bookHobbit)).thenReturn(hobbitDto);
        when(bookMapper.toDtoWithoutCategories(bookLotr)).thenReturn(lotrDto);
        
        Page<BookDtoWithoutCategoriesIds> books = bookService.findAllByCategoryId(
                categoryId, pageable);
        
        assertNotNull(books);
        assertEquals(2, books.getTotalElements());
        assertEquals(2, books.getContent().size());
        assertEquals(hobbitDto, books.getContent().get(0));
        assertEquals(lotrDto, books.getContent().get(1));
        
        verify(bookRepository).findAllByCategories_Id(categoryId, pageable);
        verify(bookMapper).toDtoWithoutCategories(bookHobbit);
        verify(bookMapper).toDtoWithoutCategories(bookLotr);
    }
    
    @Test
    @DisplayName("""
            findAllByCategoryId() returns empty page when no books exist for given category
            """)
    void findAllByCategoryId_NoBooksFound_ReturnsEmptyPage() {
        Long categoryId = 999L;
        Pageable pageable = PageRequest.of(0, 10);
        
        Page<Book> emptyBookPage = new PageImpl<>(List.of(), pageable, 0);
        
        when(bookRepository.findAllByCategories_Id(categoryId, pageable))
                .thenReturn(emptyBookPage);
        
        Page<BookDtoWithoutCategoriesIds> books =
                bookService.findAllByCategoryId(categoryId, pageable);
        
        assertNotNull(books);
        assertTrue(books.isEmpty());
        assertEquals(0, books.getTotalElements());
        assertEquals(0, books.getContent().size());
        
        verify(bookRepository).findAllByCategories_Id(categoryId, pageable);
        verify(bookMapper, never()).toDtoWithoutCategories(any());
    }
    
}
