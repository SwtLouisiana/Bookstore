package com.bookstore.controller;

import com.bookstore.dto.order.OrderRequestDto;
import com.bookstore.dto.order.OrderResponseDto;
import com.bookstore.dto.order.UpdateOrderStatusRequestDto;
import com.bookstore.dto.orderitem.OrderItemResponseDto;
import com.bookstore.model.User;
import com.bookstore.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Order management", description = "Endpoint for managing user orders")
@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;
    
    @Operation(
            summary = "Create a new order",
            description = "Creates a new order for the authenticated user based on the items in "
                    + "their shopping cart.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Order created successfully",
                            content = @Content(schema = @Schema(implementation =
                                    OrderResponseDto.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid request data"),
                    @ApiResponse(responseCode = "404", description = "Shopping cart not found")
            }
    )
    @PostMapping
    public OrderResponseDto createOrder(@RequestBody @Valid OrderRequestDto orderRequestDto,
                                        @AuthenticationPrincipal User user) {
        return orderService.createOrder(orderRequestDto, user);
    }
    
    @Operation(
            summary = "Get all orders for the current user",
            description = "Retrieves a paginated list of all orders placed by the authenticated "
                    + "user.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Orders retrieved "
                            + "successfully"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized")
            }
    )
    @GetMapping
    public Page<OrderResponseDto> getAllOrders(@AuthenticationPrincipal User user,
                                               Pageable pageable) {
        return orderService.getAllOrders(user.getId(), pageable);
    }
    
    @Operation(
            summary = "Get all items for a specific order",
            description = "Retrieves all order items for a specific order belonging to the "
                    + "authenticated user.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Order items retrieved "
                            + "successfully"),
                    @ApiResponse(responseCode = "404", description = "Order not found")
            }
    )
    @GetMapping("/{orderId}/items")
    public Page<OrderItemResponseDto> getAllOrderItems(@PathVariable Long orderId,
                                                       @AuthenticationPrincipal User user,
                                                       Pageable pageable
    ) {
        return orderService.getAllOrderItems(orderId, user.getId(), pageable);
    }
    
    @Operation(
            summary = "Get a specific item from an order",
            description = "Retrieves details for a specific order item from the user's order.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Order item retrieved "
                            + "successfully"),
                    @ApiResponse(responseCode = "404", description = "Order item not found")
            }
    )
    @GetMapping("/{orderId}/items/{itemId}")
    public OrderItemResponseDto getOrderItem(@PathVariable Long orderId,
                                             @PathVariable Long itemId,
                                             @AuthenticationPrincipal User user) {
        return orderService.getOrderItem(orderId, itemId, user.getId());
    }
    
    @Operation(
            summary = "Update order status (Admin only)",
            description = "Allows an admin to update the status of a specific order.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Order status updated "
                            + "successfully"),
                    @ApiResponse(responseCode = "403", description = "Access denied (Admin only)"),
                    @ApiResponse(responseCode = "404", description = "Order not found")
            }
    )
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PatchMapping("/{orderId}/status")
    public OrderResponseDto updateOrderStatus(@PathVariable Long orderId,
                                              @RequestBody UpdateOrderStatusRequestDto requestDto,
                                              @AuthenticationPrincipal User user
    ) {
        return orderService.updateOrderStatus(orderId, requestDto, user);
    }
}
