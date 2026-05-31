package com.google.aiprreviewer.client;

import com.google.aiprreviewer.model.github.DiffEntry;
import com.google.aiprreviewer.model.github.PullRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.Map;

@FeignClient(
        name = "github-client",
        url = "https://api.github.com"
)
public interface GithubClient {

    /**
     *
     * @param owner github用户
     * @param repo  github仓库名
     * @param pullNumber 拉取数
     * @return
     */
    @GetMapping("/repos/{owner}/{repo}/pulls/{pullNumber}")
    PullRequest getPullRequest(
            @PathVariable("owner") String owner,
            @PathVariable("repo") String repo,
            @PathVariable("pullNumber") Integer pullNumber
    );

    /**
     *
     * @param owner github用户
     * @param repo  github仓库名
     * @param pullNumber 拉取数
     * @return
     */
    @GetMapping("/repos/{owner}/{repo}/pulls/{pullNumber}/files")
    List<DiffEntry> getPullRequestFiles(
            @PathVariable("owner") String owner,
            @PathVariable("repo") String repo,
            @PathVariable("pullNumber") Integer pullNumber
    );
}
