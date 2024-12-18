package com.project.HR.Connect.entitie;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import java.sql.Date;

@Entity(name = "article")
@Getter
@Setter
public class Article {

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    private String title;

    @Column(length = 20000)
    private String content;

    @Column(nullable = false, updatable = false)
    @CreationTimestamp
    private Date createdDate;

    /* Path to the profile image of user */
    private String coverImagePath;

    private String contentType;

    @JoinColumn(name = "author")
    @ManyToOne
    private User createdBy;
}
