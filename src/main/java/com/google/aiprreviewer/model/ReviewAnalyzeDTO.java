package com.google.aiprreviewer.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ReviewAnalyzeDTO {

    @NotBlank(message = "PR地址不能为空")
    private String prUrl;
}
