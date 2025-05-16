package com.resumeAi.resume.Service;

import com.resumeAi.resume.Entity.Resume;
import com.resumeAi.resume.Repository.ResumeRepository;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public class ResumeService {
    private final ResumeRepository repository;
    private final OpenAIService openAIService;

    public ResumeService(ResumeRepository repository, OpenAIService openAIService) {
        this.repository = repository;
        this.openAIService = openAIService;
    }

    public Resume uploadAndAnalyze(String fileName, String content) throws IOException {
        String feedback = openAIService.analyzeResume(content);
        Resume resume = new Resume(null, fileName, content, feedback);
        return repository.save(resume);
    }

    public List<Resume> getAllResumes() {
        return repository.findAll();
    }

}
