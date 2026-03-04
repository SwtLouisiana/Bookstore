package com.bookstore.util;

import com.bookstore.dto.book.BookDto;
import com.bookstore.dto.book.BookDtoWithoutCategoriesIds;
import com.bookstore.dto.book.CreateBookRequestDto;
import com.bookstore.dto.cartitem.CartItemRequestDto;
import com.bookstore.dto.cartitem.CartItemResponseDto;
import com.bookstore.dto.cartitem.CartItemUpdateRequest;
import com.bookstore.dto.category.CategoryRequestDto;
import com.bookstore.dto.category.CategoryResponseDto;
import com.bookstore.dto.shoppingcart.ShoppingCartResponseDto;
import com.bookstore.model.Book;
import com.bookstore.model.CartItem;
import com.bookstore.model.Category;
import com.bookstore.model.ShoppingCart;
import com.bookstore.model.User;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TestUtil {
    
    public static BookDto convertToBookDto(CreateBookRequestDto requestDto) {
        BookDto bookDto = new BookDto();
        bookDto.setTitle(requestDto.getTitle());
        bookDto.setAuthor(requestDto.getAuthor());
        bookDto.setIsbn(requestDto.getIsbn());
        bookDto.setPrice(requestDto.getPrice());
        bookDto.setCategoryIds(requestDto.getCategoriesIds());
        
        return bookDto;
    }
    
    public static CreateBookRequestDto getUpdateCreateBookRequestDto() {
        CreateBookRequestDto requestDto = new CreateBookRequestDto();
        requestDto.setTitle("The Hobbit (Updated)");
        requestDto.setAuthor("J.R.R. Tolkien");
        requestDto.setIsbn("9780547928227");
        requestDto.setPrice(BigDecimal.valueOf(19.99));
        requestDto.setCategoriesIds(Set.of(1L));
        
        return requestDto;
    }
    
    public static BookDto getTheHobbitBookDto() {
        BookDto bookDto = new BookDto();
        bookDto.setId(1L);
        bookDto.setTitle("The Hobbit");
        bookDto.setAuthor("J.R.R. Tolkien");
        bookDto.setIsbn("9780547928227");
        bookDto.setPrice(BigDecimal.valueOf(12.99));
        bookDto.setDescription("A fantasy classic");
        bookDto.setCategoryIds(Set.of(1L));
        
        return bookDto;
    }
    
    public static BookDto getDuneBookDto() {
        BookDto bookDto = new BookDto();
        bookDto.setId(2L);
        bookDto.setTitle("Dune");
        bookDto.setAuthor("Frank Herbert");
        bookDto.setIsbn("9780441172719");
        bookDto.setPrice(BigDecimal.valueOf(14.99));
        bookDto.setDescription("A sci-fi masterpiece");
        bookDto.setCoverImage(null);
        bookDto.setCategoryIds(Set.of(2L));
        
        return bookDto;
    }
    
    public static BookDto getSapiensBookDto() {
        BookDto bookDto = new BookDto();
        bookDto.setId(3L);
        bookDto.setTitle("Sapiens");
        bookDto.setAuthor("Yuval Noah Harari");
        bookDto.setIsbn("9780062316110");
        bookDto.setPrice(BigDecimal.valueOf(16.99));
        bookDto.setDescription("A brief history of humankind");
        bookDto.setCoverImage(null);
        bookDto.setCategoryIds(Set.of(3L));
        
        return bookDto;
    }
    
    public static Book getDuneBook() {
        Book book = new Book();
        book.setId(2L);
        book.setTitle("Dune");
        book.setAuthor("Frank Herbert");
        book.setIsbn("9780441172719");
        book.setPrice(BigDecimal.valueOf(14.99));
        book.setDescription("A sci-fi masterpiece");
        
        return book;
    }
    
    public static CreateBookRequestDto getCreateLordOfTheRingsRequestDto() {
        CreateBookRequestDto requestDto = new CreateBookRequestDto();
        requestDto.setTitle("The Lord of the Rings");
        requestDto.setAuthor("J.R.R. Tolkien");
        requestDto.setIsbn("9780618640157");
        requestDto.setPrice(BigDecimal.valueOf(35.00));
        requestDto.setCategoriesIds(Set.of(1L));
        
        return requestDto;
    }
    
    public static CreateBookRequestDto getNonExistingBookRequestDto() {
        CreateBookRequestDto requestDto = new CreateBookRequestDto();
        requestDto.setTitle("Some Title");
        requestDto.setAuthor("Some Author");
        requestDto.setIsbn("9999999999999");
        requestDto.setPrice(BigDecimal.valueOf(10.00));
        requestDto.setCategoriesIds(Set.of(1L));
        
        return requestDto;
    }
    
    public static List<BookDto> getAllBooksDto() {
        return List.of(
                getTheHobbitBookDto(),
                getDuneBookDto(),
                getSapiensBookDto()
        );
    }
    
    public static Book getTheHobbitBook() {
        Book book = new Book();
        book.setId(1L);
        book.setTitle("The Hobbit");
        book.setAuthor("J.R.R. Tolkien");
        book.setIsbn("9780547928227");
        book.setPrice(BigDecimal.valueOf(12.99));
        book.setDescription("A fantasy classic");
        
        return book;
    }
    
    public static Book getLordOfTheRingsBook() {
        Book book = new Book();
        book.setId(2L);
        book.setTitle("The Lord of the Rings");
        book.setAuthor("J.R.R. Tolkien");
        book.setIsbn("978-0618640157");
        book.setPrice(BigDecimal.valueOf(35.00));
        book.setDescription("Epic high-fantasy novel.");
        
        return book;
    }
    
    public static BookDtoWithoutCategoriesIds getTheHobbitDtoWithoutCategories() {
        BookDtoWithoutCategoriesIds dto = new BookDtoWithoutCategoriesIds();
        dto.setId(1L);
        dto.setTitle("The Hobbit");
        dto.setAuthor("J.R.R. Tolkien");
        dto.setIsbn("9780547928227");
        dto.setPrice(BigDecimal.valueOf(12.99));
        dto.setDescription("A fantasy classic");
        
        return dto;
    }
    
    public static BookDtoWithoutCategoriesIds getLordOfTheRingsDtoWithoutCategories() {
        BookDtoWithoutCategoriesIds dto = new BookDtoWithoutCategoriesIds();
        dto.setId(2L);
        dto.setTitle("The Lord of the Rings");
        dto.setAuthor("J.R.R. Tolkien");
        dto.setIsbn("978-0618640157");
        dto.setPrice(BigDecimal.valueOf(35.00));
        dto.setDescription("Epic high-fantasy novel.");
        
        return dto;
    }
    
    public static CategoryResponseDto convertToCategoryResponseDto(CategoryRequestDto requestDto) {
        CategoryResponseDto dto = new CategoryResponseDto();
        dto.setName(requestDto.getName());
        dto.setDescription(requestDto.getDescription());
        
        return dto;
    }
    
    public static CategoryRequestDto getFantasyCategoryRequestDto() {
        CategoryRequestDto requestDto = new CategoryRequestDto();
        requestDto.setName("Fantasy");
        requestDto.setDescription("Books about magical worlds");
        
        return requestDto;
    }
    
    public static CategoryRequestDto getScienceFictionCategoryRequestDto() {
        CategoryRequestDto requestDto = new CategoryRequestDto();
        requestDto.setName("Science Fiction");
        requestDto.setDescription("Books about futuristic technology");
        
        return requestDto;
    }
    
    public static CategoryRequestDto getHistoryCategoryRequestDto() {
        CategoryRequestDto requestDto = new CategoryRequestDto();
        requestDto.setName("History");
        requestDto.setDescription("Books about historical events");
        
        return requestDto;
    }
    
    public static CategoryRequestDto getCyberpunkCategoryRequestDto() {
        CategoryRequestDto requestDto = new CategoryRequestDto();
        requestDto.setName("Cyberpunk");
        requestDto.setDescription("Cyberpunk books");
        
        return requestDto;
    }
    
    public static CategoryResponseDto getFantasyCategoryResponseDto() {
        CategoryResponseDto dto = new CategoryResponseDto();
        dto.setId(1L);
        dto.setName("Fantasy");
        dto.setDescription("Books about magical worlds");
        
        return dto;
    }
    
    public static CategoryResponseDto getScienceFictionCategoryResponseDto() {
        CategoryResponseDto dto = new CategoryResponseDto();
        dto.setId(2L);
        dto.setName("Science Fiction");
        dto.setDescription("Books about futuristic technology");
        
        return dto;
    }
    
    public static CategoryResponseDto getHistoryCategoryResponseDto() {
        CategoryResponseDto dto = new CategoryResponseDto();
        dto.setId(3L);
        dto.setName("History");
        dto.setDescription("Books about historical events");
        
        return dto;
    }
    
    public static CategoryRequestDto getUpdatedFantasyCategoryRequestDto() {
        CategoryRequestDto requestDto = new CategoryRequestDto();
        requestDto.setName("Epic Fantasy");
        requestDto.setDescription("Updated description");
        
        return requestDto;
    }
    
    public static List<CategoryResponseDto> getAllCategoriesResponseDto() {
        return List.of(
                getFantasyCategoryResponseDto(),
                getScienceFictionCategoryResponseDto(),
                getHistoryCategoryResponseDto()
        );
    }
    
    public static Category getFantasyCategory() {
        Category category = new Category();
        category.setId(1L);
        category.setName("Fantasy");
        category.setDescription("Books about magical worlds");
        
        return category;
    }
    
    public static Category getScienceFictionCategory() {
        Category category = new Category();
        category.setId(2L);
        category.setName("Science Fiction");
        category.setDescription("Books about futuristic technology");
        
        return category;
    }
    
    public static Category getHistoryCategory() {
        Category category = new Category();
        category.setId(3L);
        category.setName("History");
        category.setDescription("Books about historical events");
        
        return category;
    }
    
    public static User getTestUser() {
        User user = new User();
        user.setId(1L);
        user.setEmail("testuser@example.com");
        user.setPassword("$2a$10$encodedPassword");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setShippingAddress("123 Main St");
        user.setDeleted(false);
        
        return user;
    }
    
    public static ShoppingCart getTestShoppingCart() {
        ShoppingCart cart = new ShoppingCart();
        cart.setId(1L);
        cart.setUser(getTestUser());
        cart.setCartItems(new HashSet<>());
        cart.setDeleted(false);
        
        return cart;
    }
    
    public static ShoppingCart getShoppingCartWithItems() {
        ShoppingCart cart = getTestShoppingCart();
        CartItem item1 = getCartItemForHobbit();
        CartItem item2 = getCartItemForDune();
        
        item1.setShoppingCart(cart);
        item2.setShoppingCart(cart);
        
        cart.getCartItems().add(item1);
        cart.getCartItems().add(item2);
        
        return cart;
    }
    
    public static CartItem getCartItemForHobbit() {
        CartItem item = new CartItem();
        item.setId(1L);
        item.setBook(getTheHobbitBook());
        item.setQuantity(2);
        
        return item;
    }
    
    public static CartItem getCartItemForDune() {
        CartItem item = new CartItem();
        item.setId(2L);
        item.setBook(getDuneBook());
        item.setQuantity(1);
        
        return item;
    }
    
    public static CartItemRequestDto getCartItemRequestDtoForHobbit() {
        CartItemRequestDto dto = new CartItemRequestDto();
        dto.setBookId(1L);
        dto.setQuantity(2);
        
        return dto;
    }
    
    public static CartItemRequestDto getCartItemRequestDtoForDune() {
        CartItemRequestDto dto = new CartItemRequestDto();
        dto.setBookId(2L);
        dto.setQuantity(1);
        
        return dto;
    }
    
    public static CartItemUpdateRequest getCartItemUpdateRequest(int quantity) {
        CartItemUpdateRequest request = new CartItemUpdateRequest();
        request.setQuantity(quantity);
        
        return request;
    }
    
    public static CartItemResponseDto getCartItemResponseDtoForHobbit() {
        CartItemResponseDto dto = new CartItemResponseDto();
        dto.setId(1L);
        dto.setBookId(1L);
        dto.setBookTitle("The Hobbit");
        dto.setQuantity(2);
        
        return dto;
    }
    
    public static CartItemResponseDto getCartItemResponseDtoForDune() {
        CartItemResponseDto dto = new CartItemResponseDto();
        dto.setId(2L);
        dto.setBookId(2L);
        dto.setBookTitle("Dune");
        dto.setQuantity(1);
        
        return dto;
    }
    
    public static ShoppingCartResponseDto getShoppingCartResponseDto() {
        ShoppingCartResponseDto dto = new ShoppingCartResponseDto();
        dto.setId(1L);
        dto.setUserId(1L);
        dto.setCartItems(List.of(
                getCartItemResponseDtoForHobbit(),
                getCartItemResponseDtoForDune()
        ));
        
        return dto;
    }
    
    public static ShoppingCartResponseDto getEmptyShoppingCartResponseDto() {
        ShoppingCartResponseDto dto = new ShoppingCartResponseDto();
        dto.setId(1L);
        dto.setUserId(1L);
        dto.setCartItems(List.of());
        
        return dto;
    }
}
