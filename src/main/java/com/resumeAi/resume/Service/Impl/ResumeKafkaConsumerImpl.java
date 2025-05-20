package com.resumeAi.resume.Service.Impl;

import com.resumeAi.resume.Entity.Resume;
import com.resumeAi.resume.Repository.ResumeRepository;
import com.resumeAi.resume.Service.ResumeKafkaConsumer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class ResumeKafkaConsumerImpl implements ResumeKafkaConsumer {

    @Autowired
    private ResumeRepository resumeRepository;

    @Autowired
    private OpenAIServiceImpl openAIServiceImpl;

    @KafkaListener(topics = "resume-analysis-topic", groupId = "resume-group")
    @Override
    public void consume(String resumeIdStr) {
        Long resumeId = Long.parseLong(resumeIdStr);
        Optional<Resume> resumeOpt = resumeRepository.findById(resumeId);

        if (resumeOpt.isPresent()) {
            Resume resume = resumeOpt.get();
            String content = resume.getContent();

            String escapedContent = content.replace("\"", "\\\"");
            String requestBody = """
                    {
                      "model": "gpt-3.5-turbo",
                      "messages": [
                        {
                          "role": "user",
                          "content": "Please analyze this resume and provide feedback: %s"
                        }
                      ]
                    }
                    """.formatted(escapedContent);

            try {
                String aiResponse = openAIServiceImpl.callOpenAiApiWithRetry(requestBody);
                resume.setAiFeedback(aiResponse);
                resumeRepository.save(resume);
            } catch (Exception e) {
                // log error
            }
        }
    }
}
