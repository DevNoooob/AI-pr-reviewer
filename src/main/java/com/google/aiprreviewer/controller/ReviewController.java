package com.google.aiprreviewer.controller;

import com.google.aiprreviewer.common.Result;
import com.google.aiprreviewer.model.ReviewAnalyzeDTO;
import com.google.aiprreviewer.model.ReviewReportVO;
import com.google.aiprreviewer.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController()
@RequestMapping("/api/review")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    @PostMapping("/analyze")
    public Result<ReviewReportVO> analyze(@RequestBody ReviewAnalyzeDTO dto) {
        ReviewReportVO report = reviewService.analyze(dto.getPrUrl());
        return Result.ok(report);
    }


}
