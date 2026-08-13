package com.example.SpringDataJpa.resturantApp.Orders.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.example.SpringDataJpa.resturantApp.Orders.Enums.PaymentMethodEnum;
import com.example.SpringDataJpa.resturantApp.Orders.Enums.StatusEnum;

public record OrderInfoResponse(
    Integer orderId,
    LocalDateTime dateTime,
    StatusEnum status,
    PaymentMethodEnum paymentMethod,
    BigDecimal totalAmount,
    CustomerForOrdersDto customer,
    List<ItemForOrderDto> items
) {
    
}
