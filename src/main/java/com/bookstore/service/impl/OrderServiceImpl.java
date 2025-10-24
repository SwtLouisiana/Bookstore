package com.bookstore.service.impl;

import com.bookstore.dto.order.OrderRequestDto;
import com.bookstore.dto.order.OrderResponseDto;
import com.bookstore.dto.order.UpdateOrderStatusRequestDto;
import com.bookstore.dto.orderitem.OrderItemResponseDto;
import com.bookstore.exception.EntityNotFoundException;
import com.bookstore.mapper.OrderItemMapper;
import com.bookstore.mapper.OrderMapper;
import com.bookstore.model.CartItem;
import com.bookstore.model.Order;
import com.bookstore.model.OrderItem;
import com.bookstore.model.ShoppingCart;
import com.bookstore.model.User;
import com.bookstore.repository.CartItemRepository;
import com.bookstore.repository.OrderItemRepository;
import com.bookstore.repository.OrderRepository;
import com.bookstore.repository.ShoppingCartRepository;
import com.bookstore.service.OrderService;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;
    private final ShoppingCartRepository shoppingCartRepository;
    
    @Override
    public OrderResponseDto createOrder(OrderRequestDto orderRequestDto, User user) {
        ShoppingCart shoppingCart = shoppingCartRepository
                .findByUserId(user.getId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "ShoppingCart not found for user " + user.getId()));
        
        Set<CartItem> cartItems = shoppingCart.getCartItems();
        
        if (cartItems.isEmpty()) {
            throw new IllegalStateException("Cannot create order from empty cart");
        }
        
        Order order = orderMapper.toOrder(orderRequestDto);
        order.setUser(user);
        
        Set<OrderItem> orderItems = cartItems.stream()
                .map(cartItem -> orderItemMapper.toOrderItem(cartItem, order))
                .collect(Collectors.toSet());
        
        BigDecimal total = orderItems.stream()
                .map(item -> item.getPrice()
                        .multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        order.setOrderItems(orderItems);
        order.setTotal(total);
        
        orderRepository.save(order);
        
        shoppingCart.getCartItems().clear();
        shoppingCartRepository.save(shoppingCart);
        
        return orderMapper.toResponseDto(order);
    }
    
    @Override
    public Page<OrderResponseDto> getAllOrders(Long userId, Pageable pageable) {
        Page<Order> orders = orderRepository.findAllByUserId(userId, pageable);
        return orders.map(orderMapper::toResponseDto);
    }
    
    @Override
    public Page<OrderItemResponseDto> getAllOrderItems(Long orderId, Long userId,
                                                       Pageable pageable) {
        Page<OrderItem> orderItems = orderItemRepository.findAllByOrderIdAndOrderUserId(orderId,
                userId, pageable);
        return orderItems.map(orderItemMapper::toOrderItemResponseDto);
    }
    
    @Override
    public OrderItemResponseDto getOrderItem(Long orderId, Long itemId, Long userId) {
        OrderItem orderItem = orderItemRepository
                .findByOrderIdAndIdAndOrderUserId(orderId, itemId, userId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Order item not found for orderId: " + orderId + ", itemId: " + itemId
                ));
        
        return orderItemMapper.toOrderItemResponseDto(orderItem);
    }
    
    @Override
    public OrderResponseDto updateOrderStatus(Long orderId,
                                              UpdateOrderStatusRequestDto orderRequestDto,
                                              User user) {
        Order order = orderRepository.findByIdAndUserId(orderId, user.getId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Order not found for id: " + orderId + " and user id: " + user.getId()
                ));
        
        order.setStatus(orderRequestDto.getStatus());
        
        orderRepository.save(order);
        
        return orderMapper.toResponseDto(order);
    }
}
