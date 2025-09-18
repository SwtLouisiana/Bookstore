package com.bookstore.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserLoginRequestDto {
    @NotBlank(message = "Email is required")
    @Email
    private String email;
    
    @NotBlank(message = "Please enter the password")
    @Size(min = 8, message = "Password must be at least 8 characters")
    private String password;
}
