package com.bookstore.dto.order;

import com.bookstore.model.enums.Status;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateOrderStatusRequestDto {
    @NotNull
    private Status status;
}
