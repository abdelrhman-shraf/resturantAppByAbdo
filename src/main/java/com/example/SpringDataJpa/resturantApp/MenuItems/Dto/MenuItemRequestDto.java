package com.example.SpringDataJpa.resturantApp.MenuItems.Dto;
import java.math.BigDecimal;
public record MenuItemRequestDto
(
    String itemName,
    String description,
    BigDecimal price,
    Boolean available,
    Integer categoryId)
     {


} 
    

