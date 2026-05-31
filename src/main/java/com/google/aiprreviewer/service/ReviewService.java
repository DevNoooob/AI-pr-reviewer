package com.google.aiprreviewer.service;

import com.google.aiprreviewer.model.ReviewReportVO;
import jakarta.validation.constraints.NotBlank;

public interface ReviewService {
    ReviewReportVO analyze(@NotBlank(message = "PR地址不能为空") String prUrl);
}
