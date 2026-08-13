package com.example.SpringDataJpa.resturantApp.OrderDetails.dto;


public record OrderDetailsRequestDto(
    
    Integer itemId,
    Integer quantity
) {
    
}
