package com.google.aiprreviewer.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ReviewRecord {

    private Long id;

    private String prUrl;

    private String prTitle;

    private String reviewReport;

    private LocalDateTime createTime;
}
