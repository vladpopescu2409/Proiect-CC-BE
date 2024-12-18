package com.project.HR.Connect.service;

import com.project.HR.Connect.entitie.LoginDetails;
import com.project.HR.Connect.entitie.User;
import com.project.HR.Connect.repository.LoginDetailsRepository;
import com.project.HR.Connect.repository.UserRepository;
import com.project.HR.Connect.security.SecurityDetailsWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.util.Pair;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class LoginDetailsService implements UserDetailsService {

    @Autowired
    private LoginDetailsRepository loginDetailsRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;
    public Pair<Boolean, String> add(LoginDetails ld){
        ld.setPassword(passwordEncoder.encode(ld.getPassword()));
        try{
            loginDetailsRepository.save(ld);
            User newUser = new User();
            newUser.setLoginDetails(ld);
            userRepository.save(newUser);
        }
        catch (DataIntegrityViolationException e){
            return Pair.of(false, "User already exists");
        }
        return Pair.of(true, "");
    }

    //Only for security related use, if you need login details please make another function
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return new SecurityDetailsWrapper(loginDetailsRepository.findLoginDetailsByEmail(username));
    }
}
