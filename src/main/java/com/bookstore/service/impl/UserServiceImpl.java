package com.bookstore.service.impl;

import com.bookstore.dto.user.UserRegistrationRequestDto;
import com.bookstore.dto.user.UserResponseDto;
import com.bookstore.exception.RegistrationException;
import com.bookstore.mapper.UserMapper;
import com.bookstore.model.Role;
import com.bookstore.model.User;
import com.bookstore.model.enums.RoleName;
import com.bookstore.repository.RoleRepository;
import com.bookstore.repository.UserRepository;
import com.bookstore.service.ShoppingCartService;
import com.bookstore.service.UserService;
import java.util.HashSet;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final ShoppingCartService shoppingCartService;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    
    @Override
    public UserResponseDto registerUser(UserRegistrationRequestDto requestDto)
            throws RegistrationException {
        if (userRepository.existsByEmail(requestDto.getEmail())) {
            throw new RegistrationException("Email already exist: " + requestDto.getEmail());
        }
        User user = userMapper.toModel(requestDto);
        user.setPassword(passwordEncoder.encode(requestDto.getPassword()));
        
        Set<Role> roles = new HashSet<>();
        if (requestDto.getRoles() == null || requestDto.getRoles().isEmpty()) {
            Role userRole = roleRepository.findByName(RoleName.ROLE_USER)
                    .orElseThrow(() -> new RegistrationException("Default role USER not found"));
            roles.add(userRole);
        } else {
            for (String roleNameStr : requestDto.getRoles()) {
                RoleName roleName;
                try {
                    roleName = RoleName.valueOf(roleNameStr.toUpperCase());
                } catch (IllegalArgumentException e) {
                    throw new RegistrationException("Invalid role: " + roleNameStr);
                }
                Role role = roleRepository.findByName(roleName)
                        .orElseThrow(() ->
                                new RegistrationException("Role not found: " + roleName));
                roles.add(role);
            }
        }
        user.setRoles(roles);
        
        User savedUser = userRepository.save(user);
        
        shoppingCartService.initializeShoppingCart(savedUser);
        
        return userMapper.toUserResponseDto(savedUser);
    }
}
