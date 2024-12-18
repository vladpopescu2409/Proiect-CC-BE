package com.project.HR.Connect.entitie;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.sql.Date;

@Entity(name = "request")
@Getter
@Setter
public class Request {
    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    private RequestType type;

    private String details;

    private Date requestDate;

    private Date finishDate;

    private Status status;

    @JoinColumn(name = "requester_id")
    @ManyToOne
    private User requester;

    @JoinColumn(name = "responder_id")
    @ManyToOne
    private User responder;

}