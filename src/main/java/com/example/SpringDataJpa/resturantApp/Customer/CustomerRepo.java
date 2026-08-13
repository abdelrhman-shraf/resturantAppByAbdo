package com.example.SpringDataJpa.resturantApp.Customer;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CustomerRepo extends JpaRepository<Customer,Integer> {
    public void deleteById(int id);
    @Query(nativeQuery = true,value = "SELECT * FROM customers")
   public Page<Customer> findAll(Pageable pageable);
   public Optional<Customer> findById(int id);
   
}
