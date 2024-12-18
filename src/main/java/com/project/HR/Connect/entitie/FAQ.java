package com.project.HR.Connect.entitie;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity(name = "faq")
@Getter
@Setter
public class FAQ {
    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @Column(unique = true)
    private String title;

    @Column(length = 21073)
    private String content;

    // am ales orderNumber in loc de order pentru ca order e cuvant rezervat
    private Integer orderNumber;

    private String faqFilePath;

    private Category category;
}
