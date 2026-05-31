package com.google.aiprreviewer.service.impl;

import com.google.aiprreviewer.model.ReviewRecord;
import com.google.aiprreviewer.model.ReviewReportVO;
import com.google.aiprreviewer.model.github.DiffEntry;
import com.google.aiprreviewer.model.github.PullRequest;
import com.google.aiprreviewer.service.AiReviewService;
import com.google.aiprreviewer.service.GithubService;
import com.google.aiprreviewer.service.ReviewRecordService;
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

    @Autowired
    ReviewRecordService reviewRecordService;

    @Override
    public ReviewReportVO analyze(@NotBlank(message = "PR url cannot be blank") String prUrl) {

        PullRequest pr = githubService.getPullRequest(prUrl);
        List<DiffEntry> diffEntryList = githubService.getPullRequestFiles(prUrl);

        String prompt = aiReviewService.buildReviewPrompt(pr, diffEntryList);

        String aiResult = aiReviewService.review(prompt);

        LocalDateTime analyzeTime = LocalDateTime.now();

        ReviewReportVO vo = new ReviewReportVO();
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
        vo.setAnalyzeTime(analyzeTime.toString());

        ReviewRecord record = new ReviewRecord();
        record.setPrUrl(prUrl);
        record.setPrTitle(pr.getTitle());
        record.setReviewReport(aiResult);
        record.setCreateTime(analyzeTime);
        reviewRecordService.save(record);

        return vo;
    }

    @Override
    public List<ReviewRecord> history() {
        return reviewRecordService.listRecent();
    }
}
