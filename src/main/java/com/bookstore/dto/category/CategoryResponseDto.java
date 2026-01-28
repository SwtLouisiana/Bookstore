package com.bookstore.dto.category;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode
public class CategoryResponseDto {
    private Long id;
    private String name;
    private String description;
}
