package com.resumeAi.resume.Service.Impl;

import com.resumeAi.resume.Entity.Resume;
import com.resumeAi.resume.Repository.ResumeRepository;
import com.resumeAi.resume.Service.ResumeService;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public class ResumeServiceImpl implements ResumeService {
    private final ResumeRepository repository;
    private final OpenAIServiceImpl openAIServiceImpl;

    public ResumeServiceImpl(ResumeRepository repository, OpenAIServiceImpl openAIServiceImpl) {
        this.repository = repository;
        this.openAIServiceImpl = openAIServiceImpl;
    }

    public Resume uploadAndAnalyze(String fileName, String content) throws IOException {
        String feedback = openAIServiceImpl.analyzeResume(content);
        Resume resume = new Resume(null, fileName, content, feedback);
        return repository.save(resume);
    }

    public List<Resume> getAllResumes() {
        return repository.findAll();
    }

}
