package com.resumeAi.resume.Service;

public interface ResumeKafkaConsumer {
    public void consume(String resumeIdStr);
}
