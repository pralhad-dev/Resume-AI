package com.resumeAi.resume.DTO;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class CommonResponse {
    private Timestamp timestamp;
    private String successMessage;
    private String errorMessage;
    private Long count;
    private Integer statusCode;
}
