package com.bookstore.controller;

import com.bookstore.dto.user.UserLoginRequestDto;
import com.bookstore.dto.user.UserLoginResponseDto;
import com.bookstore.dto.user.UserRegistrationRequestDto;
import com.bookstore.dto.user.UserResponseDto;
import com.bookstore.exception.RegistrationException;
import com.bookstore.security.AuthenticationService;
import com.bookstore.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "User management", description = "Endpoint for managing users")
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UserService userService;
    private final AuthenticationService authenticationService;
    
    @PostMapping("/registration")
    @Operation(summary = "Register a new user", description = "Register a new user")
    public UserResponseDto registerUser(@Valid @RequestBody UserRegistrationRequestDto requestDto)
            throws RegistrationException {
        return userService.registerUser(requestDto);
    }
    
    @PostMapping("/login")
    @Operation(
            summary = "User login",
            description = "Authenticate user by email and password. Returns JWT access token."
    )
    public UserLoginResponseDto login(@RequestBody @Valid UserLoginRequestDto requestDto) {
        return authenticationService.authenticate(requestDto);
    }
}
