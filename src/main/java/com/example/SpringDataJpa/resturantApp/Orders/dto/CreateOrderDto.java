package com.example.SpringDataJpa.resturantApp.Orders.dto;

import java.util.List;

import com.example.SpringDataJpa.resturantApp.MenuItems.MenuItem;
import com.example.SpringDataJpa.resturantApp.OrderDetails.dto.OrderDetailsRequestDto;
import com.example.SpringDataJpa.resturantApp.Orders.Enums.PaymentMethodEnum;

public record CreateOrderDto(
    Integer customerId,
    PaymentMethodEnum paymentMethod,
    List<OrderDetailsRequestDto> orderedItems
) {
    
}
