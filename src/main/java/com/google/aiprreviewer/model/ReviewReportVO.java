package com.google.aiprreviewer.model;

import lombok.Data;

@Data
public class ReviewReportVO {

    /**
     * PR标题
     */
    private String prTitle;

    /**
     * PR状态
     */
    private String prState;

    /**
     * 来源分支
     */
    private String headRef;

    /**
     * 目标分支
     */
    private String baseRef;

    /**
     * GitHub PR地址
     */
    private String prUrl;

    /**
     * AI生成的Review报告
     */
    private String reviewReport;

    /**
     * 分析时间
     */
    private String analyzeTime;
}


