package com.example.SpringDataJpa.resturantApp.ResturantSecurity;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.example.SpringDataJpa.resturantApp.ResturantSecurity.AppUser.AppUserDetailsService;

@Configuration 
public class SecurityConfig {

    @Bean
    public  PasswordEncoder passwordEncoder(){
        return new  BCryptPasswordEncoder();
    }
    @Bean 
    public DaoAuthenticationProvider authenticationProvider(AppUserDetailsService service
        ,PasswordEncoder passwordEncoder){
            DaoAuthenticationProvider provider =new DaoAuthenticationProvider(service);
            provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }
    @Bean 
    public  AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception{
        return  configuration.getAuthenticationManager();
    }
    @Bean 
    public  SecurityFilterChain filterChain (HttpSecurity httpSecurity ,
         DaoAuthenticationProvider daoAuthenticationProvider){

            httpSecurity.csrf(csrf -> csrf.disable()).
            authenticationProvider(daoAuthenticationProvider).
            authorizeHttpRequests(auth -> auth.requestMatchers(HttpMethod.POST ,"/customers/register").permitAll()
            .anyRequest().authenticated() 
             ).httpBasic(Customizer.withDefaults())
             .sessionManagement(session ->
                session.sessionCreationPolicy(
                    SessionCreationPolicy.STATELESS));
                    return  httpSecurity.build();

    }
}
