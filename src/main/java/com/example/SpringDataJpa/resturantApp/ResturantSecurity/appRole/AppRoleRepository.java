package com.example.SpringDataJpa.resturantApp.ResturantSecurity.appRole;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface  AppRoleRepository extends JpaRepository<AppRole,Integer> {
    public  Optional<AppRole> findByname(String name);
}
