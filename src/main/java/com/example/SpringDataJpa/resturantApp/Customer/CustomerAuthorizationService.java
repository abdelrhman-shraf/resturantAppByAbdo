package com.example.SpringDataJpa.resturantApp.Customer;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import com.example.SpringDataJpa.resturantApp.ResturantSecurity.AppUser.UserRepository;

@Service 
public class CustomerAuthorizationService  {
    private final UserRepository userRepository;

    public CustomerAuthorizationService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
     public boolean canUpdateCustomer(
            Authentication authentication, Integer customerId) {

        
        // Admin can update any customer
        if (authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            return true;
        }

        // Only customers can proceed beyond this point
        if (authentication.getAuthorities().stream()
                .noneMatch(a -> a.getAuthority().equals("ROLE_CUSTOMER"))) {
            return false;
        }

                 if (authentication.getAuthorities().stream().
                 anyMatch(a -> a.getAuthority().equals("ROLE_CUSTOMER")) &&
                  customerId==null) {
            return  true;
        }
       

        // Get AppUser ID from JWT subject
        Jwt jwt = (Jwt) authentication.getPrincipal();
        Integer userId = Integer.valueOf(jwt.getSubject());

        // Check whether this AppUser owns the requested customer
        return userRepository.findById(userId)
                .map(user -> customerId.equals(user.getCustomerId()))
                .orElse(false);
    }
}
