package com.resumeAi.resume.Service.Impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.resumeAi.resume.Service.OpenAiService;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class OpenAIServiceImpl implements OpenAiService {
    @Value("${openai.api.key}")
    private String apiKey;

    @Value("${openai.api.url}")
    private String apiUrl;

    @Value("${openai.model}")
    private String model;

    private final OkHttpClient client = new OkHttpClient();
    private final WebClient webClient;

    public OpenAIServiceImpl(WebClient.Builder webClientBuilder,
                             @Value("${openai.api.key}") String apiKey) {
        this.webClient = webClientBuilder.baseUrl("https://api.openai.com/v1").build();
        this.apiKey = apiKey;
    }

    @Override
    public String analyzeResume(String content) throws IOException {
        ObjectMapper mapper = new ObjectMapper();

        Map<String, Object> message = new HashMap<>();
        message.put("role", "user");
        message.put("content", "Analyze this resume content and suggest improvements:\n\n" + content);

        Map<String, Object> body = new HashMap<>();
        body.put("model", model);
        body.put("messages", List.of(message));

        Request request = new Request.Builder()
                .url(apiUrl)
                .post(RequestBody.create(
                        mapper.writeValueAsString(body),
                        MediaType.get("application/json")))
                .addHeader("Authorization", "Bearer " + apiKey)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

            Map<?, ?> json = mapper.readValue(response.body().string(), Map.class);
            List<?> choices = (List<?>) json.get("choices");
            Map<?, ?> choice = (Map<?, ?>) choices.get(0);
            Map<?, ?> messageObj = (Map<?, ?>) choice.get("message");
            return (String) messageObj.get("content");
        }
    }

    @Override
    public String callOpenAiApiWithRetry(String requestBody) throws Exception {
        int maxAttempts = 3;
        int delay = 2000; // initial delay: 2 seconds

        for (int i = 1; i <= maxAttempts; i++) {
            try {
                return webClient.post()
                        .uri("/chat/completions")
                        .header("Authorization", "Bearer " + apiKey)
                        .header("Content-Type", "application/json")
                        .bodyValue(requestBody)
                        .retrieve()
                        .bodyToMono(String.class)
                        .block();
            } catch (WebClientResponseException.TooManyRequests e) {
                if (i == maxAttempts) throw e;
                System.out.println("Rate limit hit. Retrying in " + delay + " ms...");
                Thread.sleep(delay);
                delay *= 2; // exponential backoff
            }
        }
        throw new RuntimeException("OpenAI request failed after retries.");
    }
}
