package com.bookstore.mapper;

import com.bookstore.config.MapperConfig;
import com.bookstore.dto.cartitem.CartItemRequestDto;
import com.bookstore.dto.cartitem.CartItemResponseDto;
import com.bookstore.model.CartItem;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(config = MapperConfig.class, uses = {BookMapper.class})
public interface CartItemMapper {
    CartItemRequestDto toRequestDto(CartItem cartItem);
    
    CartItemResponseDto toResponseDto(CartItem cartItem);
    
    @Mapping(target = "book", source = "bookId", qualifiedByName = "bookFromId")
    CartItem toCartItem(CartItemRequestDto cartItemRequestDto);
    
    @AfterMapping
    default void setBookId(@MappingTarget CartItemRequestDto cartItemRequestDto,
                           CartItem cartItem) {
        cartItemRequestDto.setBookId(cartItem.getBook().getId());
    }
    
    @AfterMapping
    default void mapBookInfoToDto(@MappingTarget CartItemResponseDto cartItemResponseDto,
                                  CartItem cartItem) {
        cartItemResponseDto.setBookId(cartItem.getBook().getId());
        cartItemResponseDto.setBookTitle(cartItem.getBook().getTitle());
    }
}
