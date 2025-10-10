package com.bookstore.dto.user;

import com.bookstore.validation.FieldMatch;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@FieldMatch
public class UserRegistrationRequestDto {
    
    @NotBlank(message = "Email is required")
    @Email
    private String email;
    
    @NotBlank(message = "Please enter the password")
    @Size(min = 8, message = "Password must be at least 8 characters")
    private String password;
    
    @NotBlank(message = "Confirm Password is required")
    private String repeatPassword;
    
    @NotBlank(message = "Please enter the first name")
    private String firstName;
    
    @NotBlank(message = "Please enter the last name")
    private String lastName;
    
    private String shippingAddress;
    
}
