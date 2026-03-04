INSERT INTO users (id, email, password, first_name, last_name, shipping_address, is_deleted)
VALUES (1, 'testuser@example.com', '$2a$10$encodedPassword', 'John', 'Doe', '123 Main St', false);

INSERT INTO users_roles (user_id, role_id)
VALUES (1, 1);
