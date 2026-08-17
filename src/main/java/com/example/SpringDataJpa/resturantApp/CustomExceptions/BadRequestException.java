package com.example.SpringDataJpa.resturantApp.CustomExceptions;

public class BadRequestException extends RuntimeException {
    public BadRequestException(String message) {
        super(message);
    }
}