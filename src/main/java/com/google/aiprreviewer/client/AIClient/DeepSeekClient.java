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


@Component
public class DeepSeekClient {

    @Value("${deepseek.api-key}")
    private String apiKey;

    private static final String URL =
            "https://api.deepseek.com/chat/completions";

    private final OkHttpClient client = new OkHttpClient();

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

            Response response =
                    client.newCall(request).execute();

            if (!response.isSuccessful()) {
                throw new RuntimeException(
                        "DeepSeek API调用失败: "
                                + response.code()
                );
            }

            String responseBody =
                    response.body().string();

            JsonNode root =
                    objectMapper.readTree(responseBody);

            return root
                    .get("choices")
                    .get(0)
                    .get("message")
                    .get("content")
                    .asText();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private ChatRequest buildRequest(String prompt) {

        ChatRequest request = new ChatRequest();

        request.setModel("deepseek-v4-pro");

        List<Message> messages = new ArrayList<>();

        messages.add(
                new Message(
                        "system",
                        "你是一名资深Java代码评审专家"
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

        request.setThinking(thinking);

        request.setReasoningEffort("high");

        request.setStream(false);

        return request;
    }
}

