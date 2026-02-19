package com.bookstore.repository;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import com.bookstore.model.ShoppingCart;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(locations = "classpath:application.properties")
class ShoppingCartRepositoryTest {
    @Autowired
    private ShoppingCartRepository shoppingCartRepository;
    
    @Test
    @DisplayName("Find shopping cart by existing user ID returns shopping cart")
    @Sql(scripts = {
            "classpath:database/roles/add-roles-to-roles-table.sql",
            "classpath:database/users/add-user-to-users-table.sql",
            "classpath:database/shopping-carts/add-shopping-cart-to-shopping_carts-table.sql"
    })
    @Sql(scripts = {
            "classpath:database/shopping-carts/delete-shopping-carts.sql",
            "classpath:database/users/delete-users.sql",
            "classpath:database/roles/delete-roles.sql"
    }, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void findByUserId_ExistingUserId_ReturnsShoppingCart() {
        Long userId = 1L;
        
        Optional<ShoppingCart> result = shoppingCartRepository.findByUserId(userId);
        
        assertThat(result).isPresent();
        assertThat(result.get().getUser().getId()).isEqualTo(userId);
    }
    
    @Test
    @DisplayName("Find shopping cart by non-existing user ID returns empty")
    void findByUserId_NonExistingUserId_ReturnsEmpty() {
        Long nonExistingUserId = 999L;
        
        Optional<ShoppingCart> result = shoppingCartRepository.findByUserId(nonExistingUserId);
        
        assertThat(result).isEmpty();
    }
}
