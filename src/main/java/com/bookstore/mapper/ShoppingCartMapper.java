package com.bookstore.mapper;

import com.bookstore.config.MapperConfig;
import com.bookstore.dto.shoppingcart.ShoppingCartResponseDto;
import com.bookstore.model.ShoppingCart;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapperConfig.class, uses = {CartItemMapper.class})
public interface ShoppingCartMapper {
    @Mapping(target = "userId", expression =
            "java(shoppingCart.getUser() != null ? shoppingCart.getUser().getId() : null)")
    ShoppingCartResponseDto toResponseDto(ShoppingCart shoppingCart);
    
}
