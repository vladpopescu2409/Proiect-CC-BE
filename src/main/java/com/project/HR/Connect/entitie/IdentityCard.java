package com.project.HR.Connect.entitie;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.sql.Date;

@Entity(name = "identity_card")
@Getter
@Setter
public class IdentityCard {

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @Column(unique = true)
    private String cnp;
    private Integer number;
    private String series;
    private String issuer;
    private Date issuingDate;

}
