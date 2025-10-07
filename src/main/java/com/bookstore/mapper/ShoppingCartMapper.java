package com.bookstore.mapper;

import com.bookstore.config.MapperConfig;
import com.bookstore.dto.shoppingcart.ShoppingCartResponseDto;
import com.bookstore.model.ShoppingCart;
import org.mapstruct.Mapper;

@Mapper(config = MapperConfig.class)
public interface ShoppingCartMapper {
    ShoppingCartResponseDto toResponseDto(ShoppingCart shoppingCart);
    
}
