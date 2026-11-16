package com.example.SpringDataJpa.resturantApp.ResturantSecurity;


import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.SpringDataJpa.resturantApp.ResturantSecurity.Dto.LoginRequest;
import com.example.SpringDataJpa.resturantApp.ResturantSecurity.Dto.LoginResponse;

@RestController 
@RequestMapping ("auth")
public class AuthController {
    private final Authservice authservice;
    public  AuthController(Authservice authservice){
        this.authservice=authservice;
    }
    @PostMapping ("/login")
    public ResponseEntity <LoginResponse> login(@RequestBody  LoginRequest request){
        return  ResponseEntity.ok().body(authservice.login(request));

    } 
    @GetMapping ("/me")
    public  ResponseEntity<String> getuser(@AuthenticationPrincipal Jwt jwt){
        String userid=jwt.getSubject();
        return  ResponseEntity.ok().body(userid);


    }
}
