package com.example.SpringDataJpa.resturantApp.MenuItems.Dto;

import java.math.BigDecimal;

import org.antlr.v4.runtime.misc.NotNull;

public record MenuItemRequestDto
(
    String itemName,
    String description,
    BigDecimal price,
    Boolean available,
    Integer categoryId)
     {


} 
    

