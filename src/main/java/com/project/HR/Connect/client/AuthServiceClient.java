package com.project.HR.Connect.client;

import com.project.HR.Connect.entitie.LoginDetails;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@FeignClient(name = "auth-service", url = "http://localhost:8080/auth")
public interface AuthServiceClient {

    @PutMapping("/register") // This endpoint should match your auth-service's login details creation endpoint
    ResponseEntity<LoginDetails> createLoginDetails(@RequestBody LoginDetails loginDetails);

    @PostMapping("/login")
    ResponseEntity<String> authenticateUser(@RequestBody LoginDetails loginDetails);

    @PutMapping("/update-password")
    public ResponseEntity<String> updatePassword(@RequestBody Map<String, String> payload);

    @DeleteMapping
    public ResponseEntity<Void> delete(@RequestParam String email);
}
