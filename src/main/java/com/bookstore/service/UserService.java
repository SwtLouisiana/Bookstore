package com.bookstore.service;

import com.bookstore.dto.user.UserRegistrationRequestDto;
import com.bookstore.dto.user.UserResponseDto;
import com.bookstore.exception.RegistrationException;

public interface UserService {
    UserResponseDto registerUser(UserRegistrationRequestDto requestDto)
            throws RegistrationException;
}
