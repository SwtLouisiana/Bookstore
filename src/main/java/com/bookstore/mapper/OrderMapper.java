package com.bookstore.mapper;

import com.bookstore.config.MapperConfig;
import com.bookstore.dto.order.OrderRequestDto;
import com.bookstore.dto.order.OrderResponseDto;
import com.bookstore.model.Order;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapperConfig.class, uses = OrderItemMapper.class)
public interface OrderMapper {
    @Mapping(target = "orderDate", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "total", expression = "java(java.math.BigDecimal.ZERO)")
    @Mapping(target = "status", expression = "java(com.bookstore.model.enums.Status.PENDING)")
    Order toOrder(OrderRequestDto orderRequestDto);
    
    @Mapping(source = "user.id", target = "userId")
    OrderResponseDto toResponseDto(Order order);
}
