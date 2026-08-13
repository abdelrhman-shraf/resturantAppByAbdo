package com.example.SpringDataJpa.resturantApp.Orders.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.example.SpringDataJpa.resturantApp.Orders.Enums.PaymentMethodEnum;
import com.example.SpringDataJpa.resturantApp.Orders.Enums.StatusEnum;

public record OrderDetailsRowsDto(
    Integer orderId,
    LocalDateTime dateTime,
    StatusEnum status,
    PaymentMethodEnum paymentMethod,
    BigDecimal totalAmount,
    Integer customerId,
    String firstName,
    String lastName,
    String phone,
    Integer menuItemId,
    String name,
    Integer quantity,
    BigDecimal unitPrice,
    BigDecimal subtotal

) {
    
}
