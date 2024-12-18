package com.project.HR.Connect.repository;

import com.project.HR.Connect.entitie.LoginDetails;
import com.project.HR.Connect.entitie.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    User findUserByLoginDetails(LoginDetails ld);

    @Query("SELECT COUNT(id) FROM user id")
    Long countUsers();

    User findUserByLoginDetailsEmail(String email);

}