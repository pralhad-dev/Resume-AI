package com.resumeAi.resume.Service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class ResumeKafkaProducer {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    private static final String TOPIC = "resume-anaalysis-topic";

    public void sendResumeId(Integer resumeId) {
        kafkaTemplate.send(TOPIC, String.valueOf(resumeId));
    }
}
