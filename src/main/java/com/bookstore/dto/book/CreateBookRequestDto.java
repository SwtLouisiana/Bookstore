package com.bookstore.dto.book;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateBookRequestDto {
    @NotBlank(message = "Please enter the title")
    private String title;
    @NotBlank(message = "Please enter the author name")
    private String author;
    @NotBlank
    private String isbn;
    @NotNull
    @Positive(message = "The price must be 0 or greater")
    private BigDecimal price;
    private String description;
    private String coverImage;
    @NotEmpty
    private Set<Long> categoriesIds;
}
