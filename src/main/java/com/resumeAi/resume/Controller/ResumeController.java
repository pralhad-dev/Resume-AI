package com.resumeAi.resume.Controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.resumeAi.resume.DTO.ResumeResponse;
import com.resumeAi.resume.Entity.Resume;
import com.resumeAi.resume.Repository.ResumeRepository;
import com.resumeAi.resume.Service.Impl.ResumeKafkaProducerImpl;
import com.resumeAi.resume.Service.Impl.ResumeServiceImpl;
import com.resumeAi.resume.Service.OpenAiService;
import com.resumeAi.resume.Service.ResumeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/resumes")
public class ResumeController {
    private final ResumeService service;
    private final OpenAiService openAiService;

    @Autowired
    private ResumeRepository resumeRepository;

    @Autowired
    ResumeKafkaProducerImpl resumeKafkaProducerImpl;

    public ResumeController(ResumeServiceImpl service, OpenAiService openAiService) {
        this.service = service;
        this.openAiService = openAiService;
    }

    // 1. Upload API
    @PostMapping("/upload")
    public ResponseEntity<String> uploadResume(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("File is empty");
        }

        try {
            String content = new String(file.getBytes(), StandardCharsets.UTF_8);
            Resume resume = new Resume();
            resume.setFileName(file.getOriginalFilename());
            resume.setContent(content);
            Resume saved = resumeRepository.save(resume);

            resumeKafkaProducerImpl.sendResumeId(saved.getId());

            return ResponseEntity.ok("Uploaded successfully with ID: " + resume.getId());

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to read file");
        }
    }

    // 2. AI Analyze API
    @GetMapping("/analyze/{id}")
    public ResponseEntity<String> analyzeResume(@PathVariable Long id) {
        Optional<Resume> resumeOpt = resumeRepository.findById(id);

        if (resumeOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Resume not found");
        }

        Resume resume = resumeOpt.get();
        String fileContent = resume.getContent();

        try {
            // Prepare OpenAI Chat Completion request payload
            ObjectMapper mapper = new ObjectMapper();

            Map<String, Object> message = new HashMap<>();
            message.put("role", "user");
            message.put("content", "Please analyze this resume and provide feedback:\n" + fileContent);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", "gpt-3.5-turbo");
            requestBody.put("messages", List.of(message));

            String jsonRequest = mapper.writeValueAsString(requestBody);

            // Call OpenAI
            String aiResponse = openAiService.callOpenAiApiWithRetry(jsonRequest);

            // Save AI feedback
            resume.setAiFeedback(aiResponse);
            resumeRepository.save(resume);

            return ResponseEntity.ok(aiResponse);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error calling AI: " + e.getMessage());
        }
    }


    @GetMapping("/List")
    public ResponseEntity<ResumeResponse> getList() {
        ResumeResponse response = new ResumeResponse();
        try {
            List<Resume> data = service.getAllResumes();
            response.setData(data);
            response.setSuccessMessage("SUCCESS!!");
            response.setStatusCode(HttpStatus.OK.value());

            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            response.setErrorMessage("An error Occurred !!");
            response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());

            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
