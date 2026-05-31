package com.google.aiprreviewer.model.github;

import lombok.Data;

@Data
public class GithubPrInfo {

    String owner;
    String repo;
    Integer pullNumber;

}
