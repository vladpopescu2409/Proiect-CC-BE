package com.project.HR.Connect.entitie;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.sql.Date;

@Entity(name = "user")
@Getter
@Setter
public class User {
    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    private String firstName;

    private String lastName;

    private Date joinDate;

    private Position position;

    private Department department;

    private String phoneNumber;

    /* Path to the profile image of user */
    private String profileImagePath;

    private Integer vacationDays;

    private Integer sickDays;

    @JoinColumn(name = "address_id")
    @OneToOne
    private Address address;


    @JoinColumn(name = "login_details_id")
    @OneToOne
    private LoginDetails loginDetails;

    @JoinColumn(name = "identity_card_id")
    @OneToOne
    private IdentityCard identityCard;

}
