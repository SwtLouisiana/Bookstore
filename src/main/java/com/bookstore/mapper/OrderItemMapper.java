package com.bookstore.mapper;

import com.bookstore.config.MapperConfig;
import com.bookstore.dto.orderitem.OrderItemResponseDto;
import com.bookstore.model.CartItem;
import com.bookstore.model.Order;
import com.bookstore.model.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapperConfig.class)
public interface OrderItemMapper {
    
    @Mapping(source = "cartItem.book.price", target = "price")
    @Mapping(source = "cartItem.quantity", target = "quantity")
    @Mapping(source = "cartItem.book", target = "book")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "order", source = "order")
    @Mapping(target = "deleted", ignore = true)
    OrderItem toOrderItem(CartItem cartItem, Order order);
    
    @Mapping(source = "book.id", target = "bookId")
    OrderItemResponseDto toOrderItemResponseDto(OrderItem orderItem);
}
