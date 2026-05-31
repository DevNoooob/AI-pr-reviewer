package com.google.aiprreviewer.controller;

import com.google.aiprreviewer.common.Result;
import com.google.aiprreviewer.model.ReviewAnalyzeDTO;
import com.google.aiprreviewer.model.ReviewRecord;
import com.google.aiprreviewer.model.ReviewReportVO;
import com.google.aiprreviewer.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/review")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    @PostMapping("/analyze")
    public Result<ReviewReportVO> analyze(@RequestBody ReviewAnalyzeDTO dto) {
        ReviewReportVO report = reviewService.analyze(dto.getPrUrl());
        return Result.ok(report);
    }

    @GetMapping("/history")
    public Result<List<ReviewRecord>> history() {
        return Result.ok(reviewService.history());
    }
}
