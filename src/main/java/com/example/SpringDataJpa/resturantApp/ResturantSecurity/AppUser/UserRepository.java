package com.example.SpringDataJpa.resturantApp.ResturantSecurity.AppUser;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<Appuser,Long> {
    public Optional <Appuser> findByUsername(String username);
    
}
