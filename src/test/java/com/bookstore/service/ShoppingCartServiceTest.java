package com.bookstore.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.bookstore.dto.cartitem.CartItemRequestDto;
import com.bookstore.dto.cartitem.CartItemUpdateRequest;
import com.bookstore.dto.shoppingcart.ShoppingCartResponseDto;
import com.bookstore.exception.EntityNotFoundException;
import com.bookstore.mapper.CartItemMapper;
import com.bookstore.mapper.ShoppingCartMapper;
import com.bookstore.model.Book;
import com.bookstore.model.CartItem;
import com.bookstore.model.ShoppingCart;
import com.bookstore.model.User;
import com.bookstore.repository.BookRepository;
import com.bookstore.repository.CartItemRepository;
import com.bookstore.repository.ShoppingCartRepository;
import com.bookstore.service.impl.ShoppingCartServiceImpl;
import com.bookstore.util.TestUtil;
import jakarta.persistence.EntityManager;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ShoppingCartServiceTest {
    
    @Mock
    private ShoppingCartRepository shoppingCartRepository;
    
    @Mock
    private ShoppingCartMapper shoppingCartMapper;
    
    @Mock
    private BookRepository bookRepository;
    
    @Mock
    private CartItemRepository cartItemRepository;
    
    @Mock
    private CartItemMapper cartItemMapper;
    
    @Mock
    private EntityManager entityManager;
    
    @InjectMocks
    private ShoppingCartServiceImpl shoppingCartService;
    
    @Test
    @DisplayName("getShoppingCart() returns ShoppingCartResponseDto for existing user")
    void getShoppingCart_ExistingUserId_ReturnsShoppingCartResponseDto() {
        Long userId = 1L;
        ShoppingCart cart = TestUtil.getTestShoppingCart();
        ShoppingCartResponseDto expectedDto = TestUtil.getShoppingCartResponseDto();
        
        when(shoppingCartRepository.findByUserId(userId)).thenReturn(Optional.of(cart));
        when(shoppingCartMapper.toResponseDto(cart)).thenReturn(expectedDto);
        
        ShoppingCartResponseDto result = shoppingCartService.getShoppingCart(userId);
        
        assertEquals(expectedDto, result);
        
        verify(shoppingCartRepository).findByUserId(userId);
        verify(shoppingCartMapper).toResponseDto(cart);
    }
    
    @Test
    @DisplayName("getShoppingCart() throws EntityNotFoundException for non-existing user")
    void getShoppingCart_NonExistingUserId_ThrowsEntityNotFoundException() {
        Long userId = 999L;
        
        when(shoppingCartRepository.findByUserId(userId)).thenReturn(Optional.empty());
        
        assertThrows(EntityNotFoundException.class,
                () -> shoppingCartService.getShoppingCart(userId));
        
        verify(shoppingCartRepository).findByUserId(userId);
        verifyNoInteractions(shoppingCartMapper);
    }
    
    @Test
    @DisplayName("addCartItem() adds new item when book is not in cart")
    void addCartItem_NewBook_AddsNewCartItem() {
        Long userId = 1L;
        CartItemRequestDto requestDto = TestUtil.getCartItemRequestDtoForHobbit();
        
        ShoppingCart cart = TestUtil.getTestShoppingCart();
        Book book = TestUtil.getTheHobbitBook();
        CartItem newItem = TestUtil.getCartItemForHobbit();
        ShoppingCartResponseDto expectedDto = TestUtil.getShoppingCartResponseDto();
        
        when(shoppingCartRepository.findByUserId(userId)).thenReturn(Optional.of(cart));
        when(bookRepository.findById(requestDto.getBookId())).thenReturn(Optional.of(book));
        when(cartItemMapper.toCartItem(requestDto)).thenReturn(newItem);
        when(shoppingCartRepository.save(cart)).thenReturn(cart);
        when(shoppingCartMapper.toResponseDto(cart)).thenReturn(expectedDto);
        
        ShoppingCartResponseDto result = shoppingCartService.addCartItem(userId, requestDto);
        
        assertEquals(expectedDto, result);
        assertEquals(1, cart.getCartItems().size());
        assertTrue(cart.getCartItems().contains(newItem));
        
        verify(shoppingCartRepository).findByUserId(userId);
        verify(bookRepository).findById(requestDto.getBookId());
        verify(cartItemMapper).toCartItem(requestDto);
        verify(shoppingCartRepository).save(cart);
        verify(shoppingCartMapper).toResponseDto(cart);
    }
    
    @Test
    @DisplayName("addCartItem() increases quantity when book already exists in cart")
    void addCartItem_ExistingBook_IncreasesQuantity() {
        Long userId = 1L;
        CartItemRequestDto requestDto = TestUtil.getCartItemRequestDtoForHobbit();
        requestDto.setQuantity(3);
        
        ShoppingCart cart = TestUtil.getShoppingCartWithItems();
        Book book = TestUtil.getTheHobbitBook();
        CartItem existingItem = cart.getCartItems().stream()
                .filter(item -> item.getBook().getId().equals(1L))
                .findFirst()
                .get();
        
        ShoppingCartResponseDto expectedDto = TestUtil.getShoppingCartResponseDto();
        
        when(shoppingCartRepository.findByUserId(userId)).thenReturn(Optional.of(cart));
        when(bookRepository.findById(requestDto.getBookId())).thenReturn(Optional.of(book));
        when(shoppingCartRepository.save(cart)).thenReturn(cart);
        when(shoppingCartMapper.toResponseDto(cart)).thenReturn(expectedDto);
        
        int initialQuantity = existingItem.getQuantity();
        
        ShoppingCartResponseDto result = shoppingCartService.addCartItem(userId, requestDto);
        
        assertEquals(expectedDto, result);
        assertEquals(initialQuantity + 3, existingItem.getQuantity());
        assertEquals(2, cart.getCartItems().size());
        
        verify(shoppingCartRepository).findByUserId(userId);
        verify(bookRepository).findById(requestDto.getBookId());
        verify(shoppingCartRepository).save(cart);
        verify(shoppingCartMapper).toResponseDto(cart);
        verifyNoInteractions(cartItemMapper);
    }
    
    @Test
    @DisplayName("addCartItem() throws EntityNotFoundException when shopping cart not found")
    void addCartItem_NonExistingUser_ThrowsEntityNotFoundException() {
        Long userId = 999L;
        CartItemRequestDto requestDto = TestUtil.getCartItemRequestDtoForHobbit();
        
        when(shoppingCartRepository.findByUserId(userId)).thenReturn(Optional.empty());
        
        assertThrows(EntityNotFoundException.class,
                () -> shoppingCartService.addCartItem(userId, requestDto));
        
        verify(shoppingCartRepository).findByUserId(userId);
        verifyNoInteractions(bookRepository, cartItemMapper, shoppingCartMapper);
    }
    
    @Test
    @DisplayName("addCartItem() throws EntityNotFoundException when book not found")
    void addCartItem_NonExistingBook_ThrowsEntityNotFoundException() {
        Long userId = 1L;
        CartItemRequestDto requestDto = TestUtil.getCartItemRequestDtoForHobbit();
        requestDto.setBookId(999L);
        
        ShoppingCart cart = TestUtil.getTestShoppingCart();
        
        when(shoppingCartRepository.findByUserId(userId)).thenReturn(Optional.of(cart));
        when(bookRepository.findById(999L)).thenReturn(Optional.empty());
        
        assertThrows(EntityNotFoundException.class,
                () -> shoppingCartService.addCartItem(userId, requestDto));
        
        verify(shoppingCartRepository).findByUserId(userId);
        verify(bookRepository).findById(999L);
        verifyNoInteractions(cartItemMapper, shoppingCartMapper);
        verify(shoppingCartRepository, never()).save(any());
    }
    
    @Test
    @DisplayName("updateCartItem() updates quantity for existing cart item")
    void updateCartItem_ExistingCartItem_UpdatesQuantity() {
        Long userId = 1L;
        Long cartItemId = 1L;
        
        ShoppingCart cart = TestUtil.getShoppingCartWithItems();
        CartItem cartItem = TestUtil.getCartItemForHobbit();
        cartItem.setId(cartItemId);
        cartItem.setShoppingCart(cart);
        
        ShoppingCartResponseDto expectedDto = TestUtil.getShoppingCartResponseDto();
        
        when(shoppingCartRepository.findByUserId(userId)).thenReturn(Optional.of(cart));
        when(cartItemRepository.findByIdAndShoppingCartId(cartItemId, cart.getId()))
                .thenReturn(Optional.of(cartItem));
        when(shoppingCartMapper.toResponseDto(cart)).thenReturn(expectedDto);
        
        CartItemUpdateRequest updateRequest = TestUtil.getCartItemUpdateRequest(5);
        
        ShoppingCartResponseDto result = shoppingCartService.updateCartItem(
                userId, cartItemId, updateRequest);
        
        assertEquals(expectedDto, result);
        assertEquals(5, cartItem.getQuantity());
        
        verify(shoppingCartRepository).findByUserId(userId);
        verify(cartItemRepository).findByIdAndShoppingCartId(cartItemId, cart.getId());
        verify(shoppingCartMapper).toResponseDto(cart);
    }
    
    @Test
    @DisplayName("updateCartItem() throws EntityNotFoundException when shopping cart not found")
    void updateCartItem_NonExistingShoppingCart_ThrowsEntityNotFoundException() {
        Long userId = 999L;
        Long cartItemId = 1L;
        CartItemUpdateRequest updateRequest = TestUtil.getCartItemUpdateRequest(5);
        
        when(shoppingCartRepository.findByUserId(userId)).thenReturn(Optional.empty());
        
        assertThrows(EntityNotFoundException.class,
                () -> shoppingCartService.updateCartItem(userId, cartItemId, updateRequest));
        
        verify(shoppingCartRepository).findByUserId(userId);
        verifyNoInteractions(cartItemRepository, shoppingCartMapper);
    }
    
    @Test
    @DisplayName("updateCartItem() throws EntityNotFoundException when cart item not found")
    void updateCartItem_NonExistingCartItem_ThrowsEntityNotFoundException() {
        Long userId = 1L;
        Long cartItemId = 999L;
        CartItemUpdateRequest updateRequest = TestUtil.getCartItemUpdateRequest(5);
        
        ShoppingCart cart = TestUtil.getTestShoppingCart();
        
        when(shoppingCartRepository.findByUserId(userId)).thenReturn(Optional.of(cart));
        when(cartItemRepository.findByIdAndShoppingCartId(cartItemId, cart.getId()))
                .thenReturn(Optional.empty());
        
        assertThrows(EntityNotFoundException.class,
                () -> shoppingCartService.updateCartItem(userId, cartItemId, updateRequest));
        
        verify(shoppingCartRepository).findByUserId(userId);
        verify(cartItemRepository).findByIdAndShoppingCartId(cartItemId, cart.getId());
        verifyNoInteractions(shoppingCartMapper);
    }
    
    @Test
    @DisplayName("initializeShoppingCart() creates and saves new shopping cart for user")
    void initializeShoppingCart_ValidUser_CreatesAndSavesShoppingCart() {
        User user = TestUtil.getTestUser();
        
        shoppingCartService.initializeShoppingCart(user);
        
        ArgumentCaptor<ShoppingCart> cartCaptor = ArgumentCaptor.forClass(ShoppingCart.class);
        verify(shoppingCartRepository).save(cartCaptor.capture());
        
        ShoppingCart savedCart = cartCaptor.getValue();
        assertEquals(user, savedCart.getUser());
        assertTrue(savedCart.getCartItems().isEmpty());
    }
    
    @Test
    @DisplayName("removeCartItem() removes cart item when it exists")
    void removeCartItem_ExistingCartItem_RemovesCartItem() {
        Long userId = 1L;
        Long cartItemId = 1L;
        
        ShoppingCart cart = TestUtil.getShoppingCartWithItems();
        CartItem cartItem = TestUtil.getCartItemForHobbit();
        cartItem.setId(cartItemId);
        cartItem.setShoppingCart(cart);
        
        when(shoppingCartRepository.findByUserId(userId)).thenReturn(Optional.of(cart));
        when(cartItemRepository.findByIdAndShoppingCartId(cartItemId, cart.getId()))
                .thenReturn(Optional.of(cartItem));
        
        shoppingCartService.removeCartItem(userId, cartItemId);
        
        verify(shoppingCartRepository).findByUserId(userId);
        verify(cartItemRepository).findByIdAndShoppingCartId(cartItemId, cart.getId());
        verify(cartItemRepository).delete(cartItem);
    }
    
    @Test
    @DisplayName("removeCartItem() throws EntityNotFoundException when shopping cart not found")
    void removeCartItem_NonExistingShoppingCart_ThrowsEntityNotFoundException() {
        Long userId = 999L;
        Long cartItemId = 1L;
        
        when(shoppingCartRepository.findByUserId(userId)).thenReturn(Optional.empty());
        
        assertThrows(EntityNotFoundException.class,
                () -> shoppingCartService.removeCartItem(userId, cartItemId));
        
        verify(shoppingCartRepository).findByUserId(userId);
        verifyNoInteractions(cartItemRepository);
    }
    
    @Test
    @DisplayName("removeCartItem() throws EntityNotFoundException when cart item not found")
    void removeCartItem_NonExistingCartItem_ThrowsEntityNotFoundException() {
        Long userId = 1L;
        Long cartItemId = 999L;
        
        ShoppingCart cart = TestUtil.getTestShoppingCart();
        
        when(shoppingCartRepository.findByUserId(userId)).thenReturn(Optional.of(cart));
        when(cartItemRepository.findByIdAndShoppingCartId(cartItemId, cart.getId()))
                .thenReturn(Optional.empty());
        
        assertThrows(EntityNotFoundException.class,
                () -> shoppingCartService.removeCartItem(userId, cartItemId));
        
        verify(shoppingCartRepository).findByUserId(userId);
        verify(cartItemRepository).findByIdAndShoppingCartId(cartItemId, cart.getId());
        verify(cartItemRepository, never()).delete(any());
    }
}
