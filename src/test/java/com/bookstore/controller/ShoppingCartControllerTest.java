package com.bookstore.controller;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.bookstore.dto.cartitem.CartItemRequestDto;
import com.bookstore.dto.cartitem.CartItemResponseDto;
import com.bookstore.dto.cartitem.CartItemUpdateRequest;
import com.bookstore.dto.shoppingcart.ShoppingCartResponseDto;
import com.bookstore.util.TestUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlMergeMode;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@Sql(scripts = {
        "classpath:database/roles/add-roles-to-roles-table.sql",
        "classpath:database/users/add-user-to-users-table.sql",
        "classpath:database/books/add-three-books-to-books-table.sql",
        "classpath:database/shopping-carts/add-shopping-cart-to-shopping_carts-table.sql"
})
@Sql(scripts = {
        "classpath:database/cart-items/delete-cart-items.sql",
        "classpath:database/shopping-carts/delete-shopping-carts.sql",
        "classpath:database/books/delete-books.sql",
        "classpath:database/users/delete-users.sql",
        "classpath:database/roles/delete-roles.sql"
}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ShoppingCartControllerTest {
    
    protected static MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @BeforeAll
    static void beforeAll(@Autowired WebApplicationContext applicationContext) {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(applicationContext)
                .apply(springSecurity())
                .build();
    }
    
    @Test
    @DisplayName("addCartItem() adds new book to shopping cart and returns updated cart")
    @WithUserDetails(value = "testuser@example.com",
            userDetailsServiceBeanName = "customUserDetailsService")
    void addCartItem_ValidRequest_AddsBookToCart_200() throws Exception {
        CartItemRequestDto requestDto = TestUtil.getCartItemRequestDtoForHobbit();
        
        String jsonRequest = objectMapper.writeValueAsString(requestDto);
        
        MvcResult result = mockMvc.perform(post("/cart")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();
        
        ShoppingCartResponseDto actualCart = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                ShoppingCartResponseDto.class
        );
        
        assertNotNull(actualCart);
        assertNotNull(actualCart.getCartItems());
        assertEquals(1, actualCart.getCartItems().size());
        
        CartItemResponseDto expectedItem = TestUtil.getCartItemResponseDtoForHobbit();
        
        CartItemResponseDto actualItem = actualCart.getCartItems().get(0);
        assertThat(actualItem)
                .usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(expectedItem);
    }
    
    @Test
    @DisplayName("addCartItem() returns 404 when book does not exist")
    @WithUserDetails(value = "testuser@example.com",
            userDetailsServiceBeanName = "customUserDetailsService")
    void addCartItem_NonExistingBook_Returns404() throws Exception {
        CartItemRequestDto requestDto = TestUtil.getCartItemRequestDtoForHobbit();
        requestDto.setBookId(999L);
        
        String jsonRequest = objectMapper.writeValueAsString(requestDto);
        
        mockMvc.perform(post("/cart")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isNotFound());
    }
    
    @Test
    @DisplayName("getShoppingCart() returns empty shopping cart for user")
    @WithUserDetails(value = "testuser@example.com",
            userDetailsServiceBeanName = "customUserDetailsService")
    void getShoppingCart_EmptyCart_ReturnsEmptyCart_200() throws Exception {
        ShoppingCartResponseDto expectedCart = TestUtil.getEmptyShoppingCartResponseDto();
        
        MvcResult result = mockMvc.perform(get("/cart")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();
        
        ShoppingCartResponseDto actualCart = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                ShoppingCartResponseDto.class
        );
        
        assertThat(actualCart)
                .usingRecursiveComparison()
                .isEqualTo(expectedCart);
    }
    
    @Test
    @DisplayName("getShoppingCart() returns shopping cart with items")
    @WithUserDetails(value = "testuser@example.com",
            userDetailsServiceBeanName = "customUserDetailsService")
    @SqlMergeMode(SqlMergeMode.MergeMode.MERGE)
    @Sql(scripts = "classpath:database/cart-items/add-cart-items-to-cart_items-table.sql")
    @Sql(scripts = "classpath:database/cart-items/delete-cart-items.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void getShoppingCart_CartWithItems_ReturnsCartWithItems_200() throws Exception {
        ShoppingCartResponseDto expectedCart = TestUtil.getShoppingCartResponseDto();
        
        MvcResult result = mockMvc.perform(get("/cart")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();
        
        ShoppingCartResponseDto actualCart = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                ShoppingCartResponseDto.class
        );
        
        assertThat(actualCart)
                .usingRecursiveComparison()
                .ignoringCollectionOrder()
                .isEqualTo(expectedCart);
    }
    
    @Test
    @DisplayName("updateCartItem() updates quantity of existing cart item")
    @WithUserDetails(value = "testuser@example.com",
            userDetailsServiceBeanName = "customUserDetailsService")
    @SqlMergeMode(SqlMergeMode.MergeMode.MERGE)
    @Sql(scripts = "classpath:database/cart-items/add-cart-items-to-cart_items-table.sql")
    @Sql(scripts = "classpath:database/cart-items/delete-cart-items.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void updateCartItem_ValidRequest_UpdatesQuantity_200() throws Exception {
        Long cartItemId = 1L;
        CartItemUpdateRequest updateRequest = TestUtil.getCartItemUpdateRequest(5);
        
        String jsonRequest = objectMapper.writeValueAsString(updateRequest);
        
        MvcResult result = mockMvc.perform(put("/cart/items/{cartItemId}", cartItemId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();
        
        ShoppingCartResponseDto actualCart = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                ShoppingCartResponseDto.class
        );
        
        assertNotNull(actualCart);
        CartItemResponseDto updatedItem = actualCart.getCartItems().stream()
                .filter(item -> item.getId().equals(cartItemId))
                .findFirst()
                .orElseThrow();
        
        assertEquals(5, updatedItem.getQuantity());
    }
    
    @Test
    @DisplayName("updateCartItem() returns 404 when cart item does not exist")
    @WithUserDetails(value = "testuser@example.com",
            userDetailsServiceBeanName = "customUserDetailsService")
    void updateCartItem_NonExistingCartItem_Returns404() throws Exception {
        Long nonExistingCartItemId = 999L;
        CartItemUpdateRequest updateRequest = TestUtil.getCartItemUpdateRequest(5);
        
        String jsonRequest = objectMapper.writeValueAsString(updateRequest);
        
        mockMvc.perform(put("/cart/items/{cartItemId}", nonExistingCartItemId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isNotFound());
    }
    
    @Test
    @DisplayName("deleteCartItem() removes item from cart and returns updated cart")
    @WithUserDetails(value = "testuser@example.com",
            userDetailsServiceBeanName = "customUserDetailsService")
    @SqlMergeMode(SqlMergeMode.MergeMode.MERGE)
    @Sql(scripts = "classpath:database/cart-items/add-cart-items-to-cart_items-table.sql")
    @Sql(scripts = "classpath:database/cart-items/delete-cart-items.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void deleteCartItem_ExistingCartItem_RemovesItem_200() throws Exception {
        Long cartItemId = 1L;
        
        MvcResult result = mockMvc.perform(delete("/cart/items/{cartItemId}", cartItemId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();
        
        ShoppingCartResponseDto actualCart = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                ShoppingCartResponseDto.class
        );
        
        assertNotNull(actualCart);
        boolean itemExists = actualCart.getCartItems().stream()
                .anyMatch(item -> item.getId().equals(cartItemId));
        
        assertFalse(itemExists, "Cart item should be removed");
    }
    
    @Test
    @DisplayName("deleteCartItem() returns 404 when cart item does not exist")
    @WithUserDetails(value = "testuser@example.com",
            userDetailsServiceBeanName = "customUserDetailsService")
    void deleteCartItem_NonExistingCartItem_Returns404() throws Exception {
        Long nonExistingCartItemId = 999L;
        
        mockMvc.perform(delete("/cart/items/{cartItemId}", nonExistingCartItemId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}
