package com.project.HR.Connect.security;

import com.project.HR.Connect.entitie.LoginDetails;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;

public class SecurityDetailsWrapper implements UserDetails {
    LoginDetails loginDetails;
    List<SimpleGrantedAuthority> role;

    public SecurityDetailsWrapper(LoginDetails loginDetails){
        this.loginDetails = loginDetails;
        this.role = List.of(new SimpleGrantedAuthority("ROLE_%s".formatted(loginDetails.getRole())));
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return role;
    }

    @Override
    public String getPassword() {
        return loginDetails.getPassword();
    }

    @Override
    public String getUsername() {
        return loginDetails.getEmail();
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
        return true;
    }
}
