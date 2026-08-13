package com.example.SpringDataJpa.resturantApp.Orders.dto;

import java.math.BigDecimal;

public record TopSellingItemDto(
    Integer menuItemId,
    String itemName,
    Long totalQuantitySold, // SUM(od.quantity)
    Long timesOrdered,      // COUNT(od)
    BigDecimal totalRevenue // SUM(od.subTotal)
) {
    
}
