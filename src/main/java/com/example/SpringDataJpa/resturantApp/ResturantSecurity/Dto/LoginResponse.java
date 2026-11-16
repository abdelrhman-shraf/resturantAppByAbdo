package com.example.SpringDataJpa.resturantApp.ResturantSecurity.Dto;

public record LoginResponse(
     String accessToken,
    String tokenType,
     long expiresIn
) {
    
}
