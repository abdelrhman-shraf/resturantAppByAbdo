package com.example.SpringDataJpa.resturantApp;

import java.sql.SQLException;
import java.time.Instant;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.example.SpringDataJpa.resturantApp.CustomExceptions.BadRequestException;
import com.example.SpringDataJpa.resturantApp.CustomExceptions.DuplicateResourceException;
import com.example.SpringDataJpa.resturantApp.CustomExceptions.ResourceNotFoundException;

import jakarta.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(ResourceNotFoundException.class)
    public ProblemDetail resourceNotFoundHandler(ResourceNotFoundException ex,HttpServletRequest request){
        ProblemDetail problemDetail=ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND,ex.getMessage());
        problemDetail.setTitle("Resource Not Found");
        problemDetail.setProperty("timeStamp", Instant.now());
        problemDetail.setProperty("path", request.getRequestURI());
        return problemDetail;

    }
    @ExceptionHandler(DuplicateResourceException.class)
    public ProblemDetail handleDuplicateResource(DuplicateResourceException ex, HttpServletRequest request) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
        problem.setTitle("Resource Already Exists");
        problem.setProperty("timestamp", Instant.now());
        problem.setProperty("path", request.getRequestURI());
        return problem;
        
    }
    @ExceptionHandler(BadRequestException.class)
      public ProblemDetail handleBadRequest(BadRequestException ex, HttpServletRequest request) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
        problem.setTitle("BAD REQUEST ! ");
        problem.setProperty("timestamp", Instant.now());
        problem.setProperty("path", request.getRequestURI());
        return problem;
    }
    @ExceptionHandler(IllegalStateException.class)
      public ProblemDetail handleorderState(IllegalStateException ex, HttpServletRequest request) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
        problem.setTitle("BAD REQUEST ! ");
        problem.setProperty("timestamp", Instant.now());
        problem.setProperty("path", request.getRequestURI());
        return problem;
    }
    @ExceptionHandler(IllegalArgumentException.class)
      public ProblemDetail handleillegalArguments(IllegalArgumentException ex, HttpServletRequest request) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
        problem.setTitle("BAD REQUEST ! ");
        problem.setProperty("timestamp", Instant.now());
        problem.setProperty("path", request.getRequestURI());
        return problem;
    }
    

    @ExceptionHandler(DataIntegrityViolationException.class)
        public ProblemDetail handleDataIntegrityViolation(DataIntegrityViolationException ex, HttpServletRequest request) {
            ProblemDetail problem = ProblemDetail.forStatusAndDetail(
            HttpStatus.CONFLICT, 
            "A record with this information already exists."
        );
        problem.setTitle("Data Conflict");
        problem.setProperty("timestamp", Instant.now());
        problem.setProperty("path", request.getRequestURI());
        return problem;
}
    @ExceptionHandler(SQLException.class)
      public ProblemDetail handleSqlExceptions(SQLException ex, HttpServletRequest request) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, "sql exception ");
        problem.setTitle("SQL EXCEPTION : ");
        problem.setProperty("timestamp", Instant.now());
        problem.setProperty("path", request.getRequestURI());
        return problem;
        
    }


}
