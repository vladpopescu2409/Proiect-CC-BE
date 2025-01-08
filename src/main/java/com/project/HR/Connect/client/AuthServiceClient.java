package com.project.HR.Connect.client;

import com.project.HR.Connect.entitie.LoginDetails;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "auth-service", url = "http://localhost:8080/auth")
public interface AuthServiceClient {

    @PostMapping("/register") // This endpoint should match your auth-service's login details creation endpoint
    ResponseEntity<LoginDetails> createLoginDetails(@RequestBody LoginDetails loginDetails);

    @PostMapping("/login")
    ResponseEntity<String> authenticateUser(@RequestBody LoginDetails loginDetails);
}