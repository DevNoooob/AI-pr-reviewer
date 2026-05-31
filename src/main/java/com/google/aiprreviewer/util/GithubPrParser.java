package com.google.aiprreviewer.util;

import com.google.aiprreviewer.model.github.GithubPrInfo;

public class GithubPrParser {

    public static GithubPrInfo parse(String prUrl) {

        String[] split = prUrl.split("/");

        GithubPrInfo info = new GithubPrInfo();

        info.setOwner(split[3]);
        info.setRepo(split[4]);
        info.setPullNumber(Integer.parseInt(split[6]));

        return info;
    }
}
