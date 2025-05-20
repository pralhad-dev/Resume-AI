package com.resumeAi.resume.Service.Impl;


import com.resumeAi.resume.Service.ResumeKafkaProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class ResumeKafkaProducerImpl implements ResumeKafkaProducer {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    private static final String TOPIC = "resume-analysis-topic";

    public void sendResumeId(Integer resumeId) {
        kafkaTemplate.send(TOPIC, String.valueOf(resumeId));
    }
}
