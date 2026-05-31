package com.google.aiprreviewer.service.impl;

import com.google.aiprreviewer.client.GithubClient;
import com.google.aiprreviewer.model.github.DiffEntry;
import com.google.aiprreviewer.model.github.GithubPrInfo;
import com.google.aiprreviewer.model.github.PullRequest;
import com.google.aiprreviewer.service.GithubService;
import com.google.aiprreviewer.util.GithubPrParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class GithubServiceImpl implements GithubService {

    @Autowired
    GithubClient githubClient;

    @Override
    public PullRequest getPullRequest(String prUrl) {
        GithubPrInfo info = GithubPrParser.parse(prUrl);
        return githubClient.getPullRequest(info.getOwner(), info.getRepo(), info.getPullNumber());
    }

    @Override
    public List<DiffEntry> getPullRequestFiles(String prUrl) {
        GithubPrInfo info = GithubPrParser.parse(prUrl);
        return githubClient.getPullRequestFiles(info.getOwner(), info.getRepo(), info.getPullNumber());
    }
}
