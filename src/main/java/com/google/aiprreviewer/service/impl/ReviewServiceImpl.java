package com.google.aiprreviewer.service.impl;

import com.google.aiprreviewer.model.ReviewReportVO;
import com.google.aiprreviewer.model.github.DiffEntry;
import com.google.aiprreviewer.model.github.PullRequest;
import com.google.aiprreviewer.service.AiReviewService;
import com.google.aiprreviewer.service.GithubService;
import com.google.aiprreviewer.service.ReviewService;
import jakarta.validation.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReviewServiceImpl implements ReviewService {

    @Autowired
    GithubService githubService;

    @Autowired
    AiReviewService aiReviewService;

    @Override
    public ReviewReportVO analyze(@NotBlank(message = "PR地址不能为空") String prUrl) {

        PullRequest pr = githubService.getPullRequest(prUrl);
        List<DiffEntry> diffEntryList = githubService.getPullRequestFiles(prUrl);

        String prompt = aiReviewService.buildReviewPrompt(pr, diffEntryList);

        String aiResult = aiReviewService.review(prompt);

        ReviewReportVO vo =
                new ReviewReportVO();

        vo.setPrTitle(pr.getTitle());
        vo.setPrState(pr.getState());
        vo.setPrUrl(prUrl);

        if (pr.getHead() != null) {
            vo.setHeadRef(pr.getHead().getRef());
        }

        if (pr.getBase() != null) {
            vo.setBaseRef(pr.getBase().getRef());
        }

        vo.setReviewReport(aiResult);

        vo.setAnalyzeTime(
                LocalDateTime.now().toString()
        );

        return vo;
    }
}
