package com.resumeAi.resume.Service;

import java.io.IOException;

public interface OpenAiService {
    public String analyzeResume(String content) throws IOException;
    public String callOpenAiApiWithRetry(String requestBody) throws Exception;
}
