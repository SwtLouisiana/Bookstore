package com.bookstore.repository;

import com.bookstore.model.OrderItem;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    Page<OrderItem> findAllByOrderIdAndOrderUserId(Long orderId, Long userId, Pageable pageable);
    
    Optional<OrderItem> findByOrderIdAndIdAndOrderUserId(Long orderId, Long itemId, Long userId);
}
