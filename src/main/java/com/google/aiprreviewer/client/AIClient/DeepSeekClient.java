package com.google.aiprreviewer.client.AIClient;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.aiprreviewer.model.AI.ChatRequest;
import com.google.aiprreviewer.model.AI.Message;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;


@Component
public class DeepSeekClient {

    @Value("${deepseek.api-key}")
    private String apiKey;

    private static final String URL =
            "https://api.deepseek.com/chat/completions";

    //延迟超时时间
    private final OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .readTimeout(180, TimeUnit.SECONDS)
            .callTimeout(200, TimeUnit.SECONDS)
            .build();

    private final ObjectMapper objectMapper = new ObjectMapper();

    public String chat(String prompt) {

        try {

            ChatRequest requestBody = buildRequest(prompt);

            String json =
                    objectMapper.writeValueAsString(requestBody);

            RequestBody body = RequestBody.create(
                    json,
                    MediaType.parse("application/json")
            );

            Request request = new Request.Builder()
                    .url(URL)
                    .post(body)
                    .addHeader("Content-Type", "application/json")
                    .addHeader(
                            "Authorization",
                            "Bearer " + apiKey
                    )
                    .build();

            try (Response response = client.newCall(request).execute()) {

                if (!response.isSuccessful()) {
                    String errorBody = response.body() == null ? "" : response.body().string();
                    throw new RuntimeException("DeepSeek API调用失败: " + response.code() + "，" + errorBody);
                }

                String responseBody = response.body().string();

                JsonNode root = objectMapper.readTree(responseBody);

                return root
                        .get("choices")
                        .get(0)
                        .get("message")
                        .get("content")
                        .asText();
            }

        } catch (Exception e) {
            throw new RuntimeException("AI Review调用失败：" + e.getMessage(), e);
        }
    }

    private ChatRequest buildRequest(String prompt) {

        ChatRequest request = new ChatRequest();

        request.setModel("deepseek-v4-pro");

        List<Message> messages = new ArrayList<>();

        messages.add(
                new Message(
                        "system",
                        "你是一名资深Java代码评审专家，请重点关注空指针、异常处理、事务、并发、安全、性能和可维护性问题。"
                )
        );

        messages.add(
                new Message(
                        "user",
                        prompt
                )
        );

        request.setMessages(messages);

        ChatRequest.Thinking thinking =
                new ChatRequest.Thinking();

        thinking.setType("enabled");

        //request.setThinking(thinking);不要开启 thinking，避免超时
        request.setThinking(null);

        //request.setReasoningEffort("high");不要 high，先保证响应速度
        request.setReasoningEffort("low");

        request.setStream(false);

        return request;
    }
}

