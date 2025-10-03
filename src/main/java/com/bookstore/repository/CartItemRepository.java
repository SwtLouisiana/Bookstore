package com.bookstore.repository;

import com.bookstore.model.CartItem;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    Optional<CartItem> findByShoppingCartIdAndBookId(Long cartId, Long bookId);
    
    Optional<CartItem> findByIdAndShoppingCartId(Long id, Long shoppingCartId);
    
}
