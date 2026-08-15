package com.example.SpringDataJpa.resturantApp.MenuItems;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface MenuItemRepo extends JpaRepository<MenuItem,Integer>,
JpaSpecificationExecutor<MenuItem> {
    
    public Page <MenuItem> findAll(Pageable pageable);
    
    
}
