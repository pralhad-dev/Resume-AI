package com.resumeAi.resume.DTO;

import com.resumeAi.resume.Entity.Resume;
import lombok.Data;

import java.util.List;

@Data
public class ResumeResponse extends CommonResponse {
    private List<Resume> data;
}
