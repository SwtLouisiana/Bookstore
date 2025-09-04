package com.bookstore.mapper;

import com.bookstore.config.MapperConfig;
import com.bookstore.dto.UserRegistrationRequestDto;
import com.bookstore.dto.UserResponseDto;
import com.bookstore.model.User;
import org.mapstruct.Mapper;

@Mapper(config = MapperConfig.class)
public interface UserMapper {
    User toModel(UserRegistrationRequestDto requestDto);
    
    UserResponseDto toUserResponseDto(User user);
    
}
