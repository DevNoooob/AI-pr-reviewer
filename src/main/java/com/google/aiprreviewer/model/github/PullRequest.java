package com.google.aiprreviewer.model.github;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class PullRequest {

    private String title;

    private String body;

    private String state;

    private Boolean draft;

    @JsonProperty("diff_url")
    private String diffUrl;

    @JsonProperty("created_at")
    private String createdAt;

    @JsonProperty("updated_at")
    private String updatedAt;

    private Head head;

    private Base base;
}
