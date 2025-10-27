package com.bookstore.service;

import com.bookstore.dto.order.OrderRequestDto;
import com.bookstore.dto.order.OrderResponseDto;
import com.bookstore.dto.order.UpdateOrderStatusRequestDto;
import com.bookstore.dto.orderitem.OrderItemResponseDto;
import com.bookstore.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrderService {
    OrderResponseDto createOrder(OrderRequestDto orderRequestDto, User user);
    
    Page<OrderResponseDto> getAllOrders(Long userId, Pageable pageable);
    
    Page<OrderItemResponseDto> getAllOrderItems(Long orderId, Long userId, Pageable pageable);
    
    OrderItemResponseDto getOrderItem(Long orderId, Long itemId, Long userId);
    
    OrderResponseDto updateOrderStatus(Long orderId, UpdateOrderStatusRequestDto orderRequestDto,
                                       User user);
}
