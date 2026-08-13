package com.example.SpringDataJpa.resturantApp.MenuItems.Dto;

import java.math.BigDecimal;

public record MenuItemResponseDto(Integer itemId,
    String itemName,
    String description,
    BigDecimal price,
    boolean available,
    Integer categoryId,
    String categoryName) {
        
}
    

