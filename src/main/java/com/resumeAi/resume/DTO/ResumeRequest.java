package com.resumeAi.resume.DTO;

import lombok.Data;

@Data
public class ResumeRequest {
    private String name;
    private String email;
    private String resumeText;
}
