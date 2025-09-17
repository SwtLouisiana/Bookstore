package com.bookstore.validation;

import com.bookstore.dto.user.UserRegistrationRequestDto;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordMatchesValidator implements ConstraintValidator<FieldMatch,
        UserRegistrationRequestDto> {
    
    @Override
    public boolean isValid(UserRegistrationRequestDto requestDto,
                           ConstraintValidatorContext constraintValidatorContext) {
        return requestDto.getPassword() != null
                && requestDto.getPassword().equals(requestDto.getRepeatPassword());
    }
}
