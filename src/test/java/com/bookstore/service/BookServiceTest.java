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
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Set;
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
        CreateBookRequestDto requestDto = new CreateBookRequestDto();
        requestDto.setTitle("The Lord of the Rings");
        requestDto.setAuthor("J.R.R. Tolkien");
        requestDto.setIsbn("978-0618640157");
        requestDto.setPrice(BigDecimal.valueOf(35.00));
        requestDto.setDescription("Epic high-fantasy novel.");
        requestDto.setCoverImage("lotr.jpg");
        requestDto.setCategoriesIds(Set.of(1L));
        
        Book bookTolkien = new Book();
        bookTolkien.setId(1L);
        bookTolkien.setTitle("The Lord of the Rings");
        bookTolkien.setAuthor("J.R.R. Tolkien");
        bookTolkien.setIsbn("978-0618640157");
        bookTolkien.setPrice(BigDecimal.valueOf(35.00));
        bookTolkien.setDescription("Epic high-fantasy novel.");
        bookTolkien.setCoverImage("lotr.jpg");
        
        BookDto bookTolkienDto = new BookDto();
        bookTolkienDto.setId(1L);
        bookTolkienDto.setTitle("The Lord of the Rings");
        bookTolkienDto.setAuthor("J.R.R. Tolkien");
        bookTolkienDto.setIsbn("978-0618640157");
        bookTolkienDto.setPrice(BigDecimal.valueOf(35.00));
        bookTolkienDto.setDescription("Epic high-fantasy novel.");
        bookTolkienDto.setCoverImage("lotr.jpg");
        
        when(bookMapper.toModel(requestDto)).thenReturn(bookTolkien);
        when(bookRepository.save(bookTolkien)).thenReturn(bookTolkien);
        when(bookMapper.toDto(bookTolkien)).thenReturn(bookTolkienDto);
        
        BookDto savedBookDto = bookService.save(requestDto);
        
        assertEquals(bookTolkienDto, savedBookDto);
        
        verify(bookMapper).toModel(requestDto);
        verify(bookRepository).save(bookTolkien);
        verify(bookMapper).toDto(bookTolkien);
        
    }
    
    @Test
    @DisplayName("""
            findAll() returns a paginated list of BookDto for a valid page request
            """)
    void findAll_ValidPageRequest_ReturnsPagedBookDtoList() {
        
        Book bookTolkien = new Book();
        bookTolkien.setId(1L);
        bookTolkien.setTitle("The Lord of the Rings");
        bookTolkien.setAuthor("J.R.R. Tolkien");
        bookTolkien.setIsbn("978-0618640157");
        bookTolkien.setPrice(BigDecimal.valueOf(35.00));
        bookTolkien.setDescription("Epic high-fantasy novel.");
        bookTolkien.setCoverImage("lotr.jpg");
        
        Book bookOrwell = new Book();
        bookOrwell.setId(2L);
        bookOrwell.setTitle("1984");
        bookOrwell.setAuthor("George Orwell");
        bookOrwell.setIsbn("978-0451524935");
        bookOrwell.setPrice(BigDecimal.valueOf(18.00));
        bookOrwell.setDescription("Dystopian social science fiction novel.");
        bookOrwell.setCoverImage("1984.jpg");
        
        BookDto bookTolkienDto = new BookDto();
        bookTolkienDto.setId(1L);
        bookTolkienDto.setTitle("The Lord of the Rings");
        bookTolkienDto.setAuthor("J.R.R. Tolkien");
        bookTolkienDto.setIsbn("978-0618640157");
        bookTolkienDto.setPrice(BigDecimal.valueOf(35.00));
        bookTolkienDto.setDescription("Epic high-fantasy novel.");
        bookTolkienDto.setCoverImage("lotr.jpg");
        
        BookDto bookOrwellDto = new BookDto();
        bookOrwellDto.setId(2L);
        bookOrwellDto.setTitle("1984");
        bookOrwellDto.setAuthor("George Orwell");
        bookOrwellDto.setIsbn("978-0451524935");
        bookOrwellDto.setPrice(BigDecimal.valueOf(18.00));
        bookOrwellDto.setDescription("Dystopian social science fiction novel.");
        bookOrwellDto.setCoverImage("1984.jpg");
        
        Pageable pageable = PageRequest.of(0, 10);
        Page<Book> bookPage = new PageImpl<>(List.of(bookTolkien, bookOrwell), pageable, 2);
        
        when(bookRepository.findAll(pageable)).thenReturn(bookPage);
        when(bookMapper.toDto(bookTolkien)).thenReturn(bookTolkienDto);
        when(bookMapper.toDto(bookOrwell)).thenReturn(bookOrwellDto);
        
        Page<BookDto> resultPage = bookService.findAll(pageable);
        
        Page<BookDto> expectedPage = new PageImpl<>(List.of(bookTolkienDto, bookOrwellDto),
                pageable, 2);
        
        assertEquals(expectedPage, resultPage);
        
        verify(bookRepository).findAll(pageable);
        verify(bookMapper).toDto(bookTolkien);
        verify(bookMapper).toDto(bookOrwell);
        
    }
    
    @Test
    @DisplayName("""
            findById() returns BookDto when a book with the given ID exists
            """)
    void findById_ExistingId_ReturnsBookDto() {
        Book bookTolkien = new Book();
        bookTolkien.setId(1L);
        bookTolkien.setTitle("The Lord of the Rings");
        bookTolkien.setAuthor("J.R.R. Tolkien");
        bookTolkien.setIsbn("978-0618640157");
        bookTolkien.setPrice(BigDecimal.valueOf(35.00));
        bookTolkien.setDescription("Epic high-fantasy novel.");
        bookTolkien.setCoverImage("lotr.jpg");
        
        BookDto bookTolkienDto = new BookDto();
        bookTolkienDto.setId(1L);
        bookTolkienDto.setTitle("The Lord of the Rings");
        bookTolkienDto.setAuthor("J.R.R. Tolkien");
        bookTolkienDto.setIsbn("978-0618640157");
        bookTolkienDto.setPrice(BigDecimal.valueOf(35.00));
        bookTolkienDto.setDescription("Epic high-fantasy novel.");
        bookTolkienDto.setCoverImage("lotr.jpg");
        
        when(bookRepository.findById(anyLong())).thenReturn(Optional.of(bookTolkien));
        when(bookMapper.toDto(bookTolkien)).thenReturn(bookTolkienDto);
        
        BookDto bookDto = bookService.findById(1L);
        
        assertEquals(bookTolkienDto, bookDto);
        
        verify(bookRepository).findById(anyLong());
        verify(bookMapper).toDto(bookTolkien);
        
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
        Book bookTolkien = new Book();
        bookTolkien.setId(1L);
        bookTolkien.setTitle("The Lord of the Rings");
        bookTolkien.setAuthor("J.R.R. Tolkien");
        bookTolkien.setIsbn("978-0618640157");
        bookTolkien.setPrice(BigDecimal.valueOf(35.00));
        bookTolkien.setDescription("Epic high-fantasy novel.");
        bookTolkien.setCoverImage("lotr.jpg");
        
        bookService.deleteById(1L);
        
        verify(bookRepository).deleteById(1L);
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
        
        CreateBookRequestDto requestDto = new CreateBookRequestDto();
        requestDto.setTitle("The Lord of the Rings: Extended Edition");
        requestDto.setAuthor("J.R.R. Tolkien");
        requestDto.setIsbn("978-0618640157");
        requestDto.setPrice(BigDecimal.valueOf(40.00));
        requestDto.setDescription("Updated epic high-fantasy novel");
        requestDto.setCoverImage("lotr-extended.jpg");
        requestDto.setCategoriesIds(Set.of(1L));
        
        Long bookId = 1L;
        
        Book existingBook = new Book();
        existingBook.setId(bookId);
        existingBook.setTitle("The Lord of the Rings");
        existingBook.setAuthor("J.R.R. Tolkien");
        existingBook.setIsbn("978-0618640157");
        existingBook.setPrice(BigDecimal.valueOf(35.00));
        existingBook.setDescription("Epic high-fantasy novel");
        existingBook.setCoverImage("lotr.jpg");
        
        BookDto updatedBookDto = new BookDto();
        updatedBookDto.setId(bookId);
        updatedBookDto.setTitle("The Lord of the Rings: Extended Edition");
        updatedBookDto.setAuthor("J.R.R. Tolkien");
        updatedBookDto.setIsbn("978-0618640157");
        updatedBookDto.setPrice(BigDecimal.valueOf(40.00));
        updatedBookDto.setDescription("Updated epic high-fantasy novel");
        updatedBookDto.setCoverImage("lotr-extended.jpg");
        
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
        
        CreateBookRequestDto requestDto = new CreateBookRequestDto();
        requestDto.setTitle("Some title");
        requestDto.setAuthor("Some author");
        requestDto.setIsbn("1234567890");
        requestDto.setPrice(BigDecimal.valueOf(10.00));
        requestDto.setCategoriesIds(Set.of(1L));
        
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
        
        Book bookHobbit = new Book();
        bookHobbit.setId(1L);
        bookHobbit.setTitle("The Hobbit");
        bookHobbit.setAuthor("J.R.R. Tolkien");
        bookHobbit.setIsbn("9780547928227");
        bookHobbit.setPrice(BigDecimal.valueOf(12.99));
        bookHobbit.setDescription("A fantasy classic");
        
        Book bookLotr = new Book();
        bookLotr.setId(2L);
        bookLotr.setTitle("The Lord of the Rings");
        bookLotr.setAuthor("J.R.R. Tolkien");
        bookLotr.setIsbn("978-0618640157");
        bookLotr.setPrice(BigDecimal.valueOf(35.00));
        bookLotr.setDescription("Epic high-fantasy novel.");
        
        BookDtoWithoutCategoriesIds hobbitDto = new BookDtoWithoutCategoriesIds();
        hobbitDto.setId(1L);
        hobbitDto.setTitle("The Hobbit");
        hobbitDto.setAuthor("J.R.R. Tolkien");
        hobbitDto.setIsbn("9780547928227");
        hobbitDto.setPrice(BigDecimal.valueOf(12.99));
        hobbitDto.setDescription("A fantasy classic");
        
        BookDtoWithoutCategoriesIds lotrDto = new BookDtoWithoutCategoriesIds();
        lotrDto.setId(2L);
        lotrDto.setTitle("The Lord of the Rings");
        lotrDto.setAuthor("J.R.R. Tolkien");
        lotrDto.setIsbn("978-0618640157");
        lotrDto.setPrice(BigDecimal.valueOf(35.00));
        lotrDto.setDescription("Epic high-fantasy novel.");
        
        Pageable pageable = PageRequest.of(0, 10);
        
        Page<Book> bookPage = new PageImpl<>(List.of(bookHobbit, bookLotr), pageable, 2);
        
        Long categoryId = 1L;
        
        when(bookRepository.findAllByCategories_Id(categoryId, pageable)).thenReturn(bookPage);
        when(bookMapper.toDtoWithoutCategories(bookHobbit)).thenReturn(hobbitDto);
        when(bookMapper.toDtoWithoutCategories(bookLotr)).thenReturn(lotrDto);
        
        Page<BookDtoWithoutCategoriesIds> books = bookService.findAllByCategoryId(categoryId,
                pageable);
        
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
