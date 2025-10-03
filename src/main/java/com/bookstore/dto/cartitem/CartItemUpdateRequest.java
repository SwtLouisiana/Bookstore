package com.bookstore.dto.cartitem;

import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CartItemUpdateRequest {
    @Positive
    private int quantity;
}
