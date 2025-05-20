package com.resumeAi.resume.Service;

import com.resumeAi.resume.Entity.Resume;

import java.io.IOException;
import java.util.List;

public interface ResumeService {
    public Resume uploadAndAnalyze(String fileName, String content) throws IOException;

    public List<Resume> getAllResumes();
}
