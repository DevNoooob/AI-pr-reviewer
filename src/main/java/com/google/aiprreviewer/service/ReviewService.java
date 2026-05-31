package com.google.aiprreviewer.service;

import com.google.aiprreviewer.model.ReviewRecord;
import com.google.aiprreviewer.model.ReviewReportVO;
import jakarta.validation.constraints.NotBlank;

import java.util.List;

public interface ReviewService {

    ReviewReportVO analyze(@NotBlank(message = "PR url cannot be blank") String prUrl);

    List<ReviewRecord> history();
}
