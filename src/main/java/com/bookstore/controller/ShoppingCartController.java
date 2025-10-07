package com.bookstore.controller;

import com.bookstore.dto.cartitem.CartItemRequestDto;
import com.bookstore.dto.cartitem.CartItemResponseDto;
import com.bookstore.dto.cartitem.CartItemUpdateRequest;
import com.bookstore.dto.shoppingcart.ShoppingCartResponseDto;
import com.bookstore.model.User;
import com.bookstore.service.ShoppingCartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Shopping cart management", description = "Endpoint for managing shopping carts")
@RequiredArgsConstructor
@RestController
@RequestMapping("/cart")
public class ShoppingCartController {
    private final ShoppingCartService shoppingCartService;
    
    @Operation(summary = "Add book to shopping cart", description = "Adds a new book to the "
            + "current user's shopping cart. If the book already exists, its quantity will be "
            + "increased.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Book added to cart successfully",
                    content = @Content(schema = @Schema(implementation =
                            ShoppingCartResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Book not found"),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    @PostMapping
    public ShoppingCartResponseDto addCartItem(
            @RequestBody @Valid CartItemRequestDto cartItemRequestDto,
            @AuthenticationPrincipal User user) {
        return shoppingCartService.addCartItem(user.getId(), cartItemRequestDto);
    }
    
    @Operation(summary = "Get current shopping cart", description = "Retrieves the shopping cart "
            + "of the current authenticated user.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Shopping cart retrieved successfully",
                    content = @Content(schema = @Schema(implementation =
                            ShoppingCartResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Shopping cart not found for user")
    })
    @GetMapping
    public ShoppingCartResponseDto getShoppingCart(@AuthenticationPrincipal User user) {
        return shoppingCartService.getShoppingCart(user.getId());
    }
    
    @Operation(summary = "Update cart item quantity", description = "Updates the quantity of a "
            + "specific cart item in the user's shopping cart.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cart item updated successfully",
                    content = @Content(schema = @Schema(implementation =
                            CartItemResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Cart or item not found"),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    @PutMapping("/items/{cartItemId}")
    public ShoppingCartResponseDto updateCartItem(
            @PathVariable Long cartItemId,
            @RequestBody @Valid CartItemUpdateRequest cartItemUpdateRequest,
            @AuthenticationPrincipal User user) {
        return shoppingCartService.updateCartItem(user.getId(), cartItemId, cartItemUpdateRequest);
    }
    
    @Operation(summary = "Delete cart item", description = "Removes a specific item from the "
            + "user's shopping cart.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Item deleted successfully. Returns "
                    + "updated shopping cart",
                    content = @Content(schema = @Schema(implementation =
                            ShoppingCartResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Cart or item not found")
    })
    @DeleteMapping("/items/{cartItemId}")
    public ShoppingCartResponseDto deleteCartItem(@PathVariable Long cartItemId,
                                                  @AuthenticationPrincipal User user) {
        return shoppingCartService.removeCartItem(user.getId(), cartItemId);
    }
}
