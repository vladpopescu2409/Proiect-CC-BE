package com.project.HR.Connect.controller;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.project.HR.Connect.entitie.LoginDetails;
import com.project.HR.Connect.security.JWTUtils;
import com.project.HR.Connect.security.SecurityDetailsWrapper;
import com.project.HR.Connect.service.LoginDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private LoginDetailsService loginDetailsService;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    JWTUtils jwtUtils;

    @PostMapping
    ResponseEntity<?> login(@RequestBody LoginDetails loginDetails){
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDetails.getEmail(), loginDetails.getPassword()));
        SecurityDetailsWrapper userDetails = (SecurityDetailsWrapper) authentication.getPrincipal();
        var out = new JsonObject();

        out.add("token", new JsonPrimitive(jwtUtils.createJwt(
                userDetails.getUsername(),
                ((SimpleGrantedAuthority) userDetails.getAuthorities().toArray()[0]).getAuthority().substring(5))));

        return ResponseEntity.ok(out.toString());
    }

    // exista 3 utilizatori de test
    /*
    {
    "email": "testadm",
    "password": "testadm"
     }
     {
    "email": "testhr",
    "password": "testhr"
    }
    {
        "email": "testempl",
        "password": "testempl"
    }
    Daca nu exista, comenteaza linia care cere autorizare si se pot crea la liber
    */
    @PutMapping
//    @PreAuthorize("hasRole('admin')")
    ResponseEntity<?> register(@RequestBody LoginDetails loginDetails){
        var registerOutput = loginDetailsService.add(loginDetails);
        if (registerOutput.getFirst()){
            return ResponseEntity.ok(loginDetails);
        }else{
            return ResponseEntity.badRequest().body(registerOutput.getSecond());
        }
    }
}
