package com.example.SpringDataJpa.resturantApp.ResturantSecurity.Dto;

public record LoginRequest(
     String username,
     String password
) {
    
}
