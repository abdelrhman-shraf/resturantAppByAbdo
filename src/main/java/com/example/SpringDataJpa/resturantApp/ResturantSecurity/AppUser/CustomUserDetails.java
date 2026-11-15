package com.example.SpringDataJpa.resturantApp.ResturantSecurity.AppUser;

import java.util.Collection;
import java.util.Set;

import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.example.SpringDataJpa.resturantApp.ResturantSecurity.appRole.AppRole;

public class CustomUserDetails  implements UserDetails  {
    private final Appuser appuser;
    public  CustomUserDetails(Appuser appuser){
        this.appuser=appuser;
    }
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return  appuser.getRoles().stream().
        map(role ->
             new SimpleGrantedAuthority( role.getName())).
            toList();
    }

    @Override
    public @Nullable String getPassword() {
        return  appuser.getPassword();
    }

    @Override
    public String getUsername() {
        return  appuser.getUsername();
    } 
     @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return appuser.isEnabled();
    }
    public Appuser getAppUser() {
        return appuser;
    }

    
}
