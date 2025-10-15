package com.bookstore.mapper;

import com.bookstore.config.MapperConfig;
import com.bookstore.dto.user.UserRegistrationRequestDto;
import com.bookstore.dto.user.UserResponseDto;
import com.bookstore.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapperConfig.class)
public interface UserMapper {
    @Mapping(target = "roles", ignore = true)
    User toModel(UserRegistrationRequestDto requestDto);
    
    UserResponseDto toUserResponseDto(User user);
}
