package com.google.aiprreviewer.service;

import com.google.aiprreviewer.model.github.DiffEntry;
import com.google.aiprreviewer.model.github.PullRequest;
import jakarta.validation.constraints.NotBlank;

import java.util.List;

public interface GithubService {

    PullRequest getPullRequest(@NotBlank(message = "PR地址不能为空") String prUrl);

    List<DiffEntry> getPullRequestFiles(@NotBlank(message = "PR地址不能为空") String prUrl);
}
