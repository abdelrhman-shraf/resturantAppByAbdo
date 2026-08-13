package com.example.SpringDataJpa.resturantApp.Orders.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.example.SpringDataJpa.resturantApp.Orders.Enums.PaymentMethodEnum;
import com.example.SpringDataJpa.resturantApp.Orders.Enums.StatusEnum;

public record OrderResponseDto(
    int orderId,
     LocalDateTime orderDatetime,
     StatusEnum status,
     PaymentMethodEnum paymentMethod,
     BigDecimal totalAmount
) {
    
}
