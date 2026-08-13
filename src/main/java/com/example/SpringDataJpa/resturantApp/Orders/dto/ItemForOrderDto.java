package com.example.SpringDataJpa.resturantApp.Orders.dto;

import java.math.BigDecimal;

public record ItemForOrderDto(
    Integer menuItemId,
    String name,
    Integer quantity,
    BigDecimal unitPrice,
    BigDecimal subtotal
) {
    
}
