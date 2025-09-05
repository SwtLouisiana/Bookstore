package com.bookstore.repository;

import com.bookstore.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    
    boolean existsByEmail(String email);
}
