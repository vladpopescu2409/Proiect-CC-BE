package com.project.HR.Connect.repository;

import com.project.HR.Connect.entitie.LoginDetails;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LoginDetailsRepository extends JpaRepository<LoginDetails, Integer> {
    LoginDetails findLoginDetailsByEmail(String username);

    LoginDetails findLoginDetailsByEmailAndPassword(String username, String password);
}