package com.project.HR.Connect.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class EmailDTO {
    private String emailRecipient;
    private String subject;
    private String body;
}
