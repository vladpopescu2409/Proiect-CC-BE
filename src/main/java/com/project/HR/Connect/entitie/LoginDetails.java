package com.project.HR.Connect.entitie;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity(name = "login_details")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoginDetails {

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @Column(unique = true)
    private String email;

    private String password;

    private String role;

    public LoginDetails(LoginDetails loginDetails) {
        this.email = loginDetails.email;
        this.password = loginDetails.password;
        this.role = loginDetails.role;
    }
}
