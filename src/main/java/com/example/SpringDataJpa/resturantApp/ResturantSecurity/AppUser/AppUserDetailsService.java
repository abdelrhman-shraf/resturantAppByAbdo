package com.example.SpringDataJpa.resturantApp.ResturantSecurity.AppUser;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
@Service 
public class AppUserDetailsService implements UserDetailsService {
    UserRepository userRepository;
    public  AppUserDetailsService(UserRepository userRepository){
        this.userRepository=userRepository;
    }
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Appuser appuser ;
        appuser= userRepository.findByUsername(username)
        .orElseThrow( ()-> new UsernameNotFoundException("User with  username :  " + username +" doesn't exist"));
        return  new CustomUserDetails(appuser);

    }
    public  Appuser loadUserById(Integer id){
        Appuser user;
        user=userRepository.
        findById(id).orElseThrow( ()-> new UsernameNotFoundException("User with  id :  " + id +" doesn't exist"));
        return  user;
    }

}
