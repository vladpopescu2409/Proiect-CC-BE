package com.project.HR.Connect.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ArticleDTO {

    private Integer id;

    private String title;

    private String content;

    private Date createdDate;

    /* Path to the profile image of user */
    private String coverImagePath;
}
