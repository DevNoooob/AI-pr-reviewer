package com.google.aiprreviewer.model.AI;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class ChatRequest {

    private String model;

    private List<Message> messages;

    private Thinking thinking;

    @JsonProperty("reasoning_effort")
    private String reasoningEffort;

    private Boolean stream;

    @Data
    public static class Thinking {
        private String type;
    }
}
