package com.bookstore.service;

import com.bookstore.dto.cartitem.CartItemRequestDto;
import com.bookstore.dto.cartitem.CartItemUpdateRequest;
import com.bookstore.dto.shoppingcart.ShoppingCartResponseDto;
import com.bookstore.model.User;

public interface ShoppingCartService {
    ShoppingCartResponseDto getShoppingCart(Long userId);
    
    ShoppingCartResponseDto addCartItem(Long userId, CartItemRequestDto cartItem);
    
    ShoppingCartResponseDto updateCartItem(Long userId, Long cartItemId,
                                           CartItemUpdateRequest cartItemUpdateRequest);
    
    void initializeShoppingCart(User user);
    
    ShoppingCartResponseDto removeCartItem(Long userId, Long cartItemId);
}
