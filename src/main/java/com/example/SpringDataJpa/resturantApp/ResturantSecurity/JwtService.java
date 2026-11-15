package com.example.SpringDataJpa.resturantApp.ResturantSecurity;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import com.example.SpringDataJpa.resturantApp.ResturantSecurity.AppUser.CustomUserDetails;
import com.example.SpringDataJpa.resturantApp.ResturantSecurity.appRole.AppRole;
@Service 
public class JwtService {
    private  final JwtEncoder jwtencoder;
        @Value("${security.jwt.expiration}")
        Long expirationtime;
    public  JwtService(JwtEncoder jwtencoder){
        this.jwtencoder=jwtencoder;
    }
    public  String generateToken(CustomUserDetails userdetails){
       
        /*
            {
                "iss": "restaurant-app",
                "sub": "15",
                "email": "abdo@gmail.com",
                "roles": ["CUSTOMER"],
                "iat": 1794650000,
                "exp": 1794650900
            }
         */
          List<String> roles = userdetails.getAppUser().getRoles()
            .stream()
            .map(AppRole::getName)
            .toList();
        Instant now =Instant.now();
        Instant expiresAt=now.plus(expirationtime,ChronoUnit.MILLIS);
        JwtClaimsSet claims=JwtClaimsSet.builder().
        issuer("resturant-app")
        .issuedAt(now)
        .expiresAt(expiresAt)
        .subject(userdetails.getAppUser().getId().toString())
        .claim("username",userdetails.getUsername())
        .claim("roles", roles).build();
        JwsHeader header=JwsHeader.with(MacAlgorithm.HS256).build();
        return  jwtencoder.encode(JwtEncoderParameters.from(header,claims)).getTokenValue();

    }
}
