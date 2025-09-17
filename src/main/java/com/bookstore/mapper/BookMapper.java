package com.bookstore.mapper;

import com.bookstore.config.MapperConfig;
import com.bookstore.dto.book.BookDto;
import com.bookstore.dto.book.BookDtoWithoutCategoriesIds;
import com.bookstore.dto.book.CreateBookRequestDto;
import com.bookstore.model.Book;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(config = MapperConfig.class)
public interface BookMapper {
    BookDto toDto(Book book);
    
    Book toModel(CreateBookRequestDto requestDto);
    
    BookDtoWithoutCategoriesIds toDtoWithoutCategories(Book book);
    
    void updateBook(CreateBookRequestDto requestDto,@MappingTarget Book book);
}
