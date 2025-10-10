package com.bookstore.service.impl;

import com.bookstore.dto.cartitem.CartItemRequestDto;
import com.bookstore.dto.cartitem.CartItemUpdateRequest;
import com.bookstore.dto.shoppingcart.ShoppingCartResponseDto;
import com.bookstore.mapper.CartItemMapper;
import com.bookstore.mapper.ShoppingCartMapper;
import com.bookstore.model.Book;
import com.bookstore.model.CartItem;
import com.bookstore.model.ShoppingCart;
import com.bookstore.model.User;
import com.bookstore.repository.BookRepository;
import com.bookstore.repository.CartItemRepository;
import com.bookstore.repository.ShoppingCartRepository;
import com.bookstore.service.ShoppingCartService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class ShoppingCartServiceImpl implements ShoppingCartService {
    private final ShoppingCartRepository shoppingCartRepository;
    private final ShoppingCartMapper shoppingCartMapper;
    private final BookRepository bookRepository;
    private final CartItemRepository cartItemRepository;
    private final CartItemMapper cartItemMapper;
    
    @Override
    public ShoppingCartResponseDto getShoppingCart(Long userId) {
        ShoppingCart cart = shoppingCartRepository.findByUserId(userId).orElseThrow(() ->
                new EntityNotFoundException("ShoppingCart not found for user " + userId));
        return shoppingCartMapper.toResponseDto(cart);
    }
    
    @Override
    public ShoppingCartResponseDto addCartItem(Long userId, CartItemRequestDto cartItem) {
        ShoppingCart cart = shoppingCartRepository
                .findByUserId(userId).orElseThrow(() ->
                        new EntityNotFoundException("ShoppingCart not found for user " + userId));
        Book book = bookRepository
                .findById(cartItem.getBookId()).orElseThrow(() ->
                        new EntityNotFoundException(
                                "Book not found for id " + cartItem.getBookId()));
        
        cart.getCartItems().stream()
                .filter(item -> item.getBook().getId().equals(book.getId()))
                .findFirst()
                .ifPresentOrElse(
                        existingItem -> existingItem.setQuantity(existingItem.getQuantity()
                                + cartItem.getQuantity()),
                        () -> {
                            CartItem newItem = cartItemMapper.toCartItem(cartItem);
                            newItem.setBook(book);
                            newItem.setShoppingCart(cart);
                            cart.getCartItems().add(newItem);
                        });
        
        shoppingCartRepository.save(cart);
        
        return shoppingCartMapper.toResponseDto(cart);
    }
    
    @Override
    public ShoppingCartResponseDto updateCartItem(Long userId, Long cartItemId,
                                                  CartItemUpdateRequest cartItemUpdateRequest) {
        ShoppingCart cart = shoppingCartRepository
                .findByUserId(userId).orElseThrow(() ->
                        new EntityNotFoundException("ShoppingCart not found for user " + userId));
        return cartItemRepository.findByIdAndShoppingCartId(cartItemId, cart.getId())
                .map(item -> {
                    item.setQuantity(cartItemUpdateRequest.getQuantity());
                    return shoppingCartMapper.toResponseDto(cart);
                })
                .orElseThrow(() -> new EntityNotFoundException(
                        "CartItem not found for id " + cartItemId));
    }
    
    @Override
    public void initializeShoppingCart(User user) {
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setUser(user);
        shoppingCartRepository.save(shoppingCart);
    }
    
    @Override
    public ShoppingCartResponseDto removeCartItem(Long userId, Long cartItemId) {
        ShoppingCart cart = shoppingCartRepository
                .findByUserId(userId).orElseThrow(() ->
                        new EntityNotFoundException("ShoppingCart not found for user " + userId));
        cartItemRepository.findByIdAndShoppingCartId(cartItemId, cart.getId())
                .ifPresentOrElse(cartItemRepository::delete,
                        () -> {
                            throw new EntityNotFoundException(
                                    "CartItem not found for id " + cartItemId);
                        });
        return shoppingCartMapper.toResponseDto(cart);
    }
}
