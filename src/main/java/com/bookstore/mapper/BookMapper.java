package com.bookstore.mapper;

import com.bookstore.config.MapperConfig;
import com.bookstore.dto.book.BookDto;
import com.bookstore.dto.book.BookDtoWithoutCategoriesIds;
import com.bookstore.dto.book.CreateBookRequestDto;
import com.bookstore.model.Book;
import com.bookstore.model.Category;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

@Mapper(config = MapperConfig.class)
public interface BookMapper {
    BookDto toDto(Book book);
    
    Book toModel(CreateBookRequestDto requestDto);
    
    BookDtoWithoutCategoriesIds toDtoWithoutCategories(Book book);
    
    void updateBook(CreateBookRequestDto requestDto,@MappingTarget Book book);
    
    @AfterMapping
    default void setCategoryIds(@MappingTarget BookDto bookDto, Book book) {
        Set<Long> categoryIds = Optional.ofNullable(book.getCategories())
                .orElse(Collections.emptySet())
                .stream()
                .map(Category::getId)
                .collect(Collectors.toSet());
        bookDto.setCategoryIds(categoryIds);
    }
    
    @AfterMapping
    default void setCategories(CreateBookRequestDto bookDto, @MappingTarget Book book) {
        Set<Category> categories = Optional.ofNullable(bookDto.getCategoriesIds())
                .orElse(Collections.emptySet())
                .stream()
                .map(Category::new)
                .collect(Collectors.toSet());
        book.setCategories(categories);
    }
    
    @Named("bookFromId")
    default Book bookFromId(Long bookId) {
        Book book = new Book();
        book.setId(bookId);
        return book;
    }
}
