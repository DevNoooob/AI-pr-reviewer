package com.google.aiprreviewer.service;

import com.google.aiprreviewer.model.github.DiffEntry;
import com.google.aiprreviewer.model.github.PullRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface AiReviewService {

    String buildReviewPrompt(PullRequest pr, List<DiffEntry> diffEntryList);


    String review(String prompt);

}
