package com.example.SpringDataJpa.resturantApp.Orders.dto;

public record CustomerForOrdersDto(
    Integer customerId,
    String firstName,
    String lastName,
    String phone
) {
    
}
