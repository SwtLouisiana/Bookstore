package com.bookstore.dto.category;

import jakarta.validation.constraints.NotBlank;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode
public class CategoryRequestDto {
    @NotBlank
    private String name;
    private String description;
}
