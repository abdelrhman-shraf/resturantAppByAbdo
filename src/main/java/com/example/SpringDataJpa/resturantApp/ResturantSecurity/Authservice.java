package com.example.SpringDataJpa.resturantApp.ResturantSecurity;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.example.SpringDataJpa.resturantApp.ResturantSecurity.AppUser.CustomUserDetails;
import com.example.SpringDataJpa.resturantApp.ResturantSecurity.Dto.LoginRequest;
import com.example.SpringDataJpa.resturantApp.ResturantSecurity.Dto.LoginResponse;

@Service 
public class Authservice {
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    public  Authservice(AuthenticationManager authenticationManager,JwtService jwtService){
        this.authenticationManager=authenticationManager;
        this.jwtService=jwtService;

    }
    public  LoginResponse login(LoginRequest request){
        Authentication authentication=new UsernamePasswordAuthenticationToken(request.username(), request.password());
        authentication=authenticationManager.authenticate(authentication);
        CustomUserDetails userDetails=(CustomUserDetails)authentication.getPrincipal();
        String token= jwtService.generateToken(userDetails);
        return new LoginResponse(token, "bearer",(900000/1000)/60 );



    }


}
