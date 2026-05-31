package com.google.aiprreviewer.service.impl;

import com.google.aiprreviewer.client.AIClient.DeepSeekClient;
import com.google.aiprreviewer.model.github.DiffEntry;
import com.google.aiprreviewer.model.github.PullRequest;
import com.google.aiprreviewer.service.AiReviewService;
import lombok.RequiredArgsConstructor;
import okhttp3.OkHttp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AiReviewServiceImpl implements AiReviewService {

    @Autowired
    private final DeepSeekClient deepSeekClient;

    private static final int MAX_PATCH_LENGTH = 3000;
    private static final int MAX_TOTAL_PROMPT_LENGTH = 15000;


    @Override
    public String buildReviewPrompt(PullRequest pr, List<DiffEntry> diffEntryList) {
        StringBuilder prompt = new StringBuilder();

        prompt.append("你是一名资深 Java 后端代码评审专家，请根据下面的 GitHub Pull Request 信息和代码 Diff，生成一份专业的代码评审报告。\n\n");

        prompt.append("请重点关注：\n");
        prompt.append("1. PR 的主要变更内容\n");
        prompt.append("2. 是否存在潜在 Bug\n");
        prompt.append("3. 是否存在空指针、异常处理、事务、并发、安全、性能等风险\n");
        prompt.append("4. 代码可读性和可维护性问题\n");
        prompt.append("5. 给出明确的 Review 建议\n\n");

        prompt.append("请按照以下 Markdown 格式输出：\n");
        prompt.append("## 一、PR变更总结\n");
        prompt.append("## 二、风险代码识别\n");
        prompt.append("## 三、潜在问题分析\n");
        prompt.append("## 四、优化建议\n");
        prompt.append("## 五、最终Review结论\n\n");

        prompt.append("====== PR 基本信息 ======\n");
        prompt.append("标题：").append(nullToEmpty(pr.getTitle())).append("\n");
        prompt.append("描述：").append(nullToEmpty(pr.getBody())).append("\n");
        prompt.append("状态：").append(nullToEmpty(pr.getState())).append("\n");

        if (pr.getHead() != null) {
            prompt.append("来源分支：").append(nullToEmpty(pr.getHead().getRef())).append("\n");
        }

        if (pr.getBase() != null) {
            prompt.append("目标分支：").append(nullToEmpty(pr.getBase().getRef())).append("\n");
        }

        prompt.append("\n====== PR文件变更Diff ======\n");

        for (DiffEntry diff : diffEntryList) {

            if (!shouldAnalyze(diff.getFilename())) {
                continue;
            }

            if (prompt.length() >= MAX_TOTAL_PROMPT_LENGTH) {
                prompt.append("\n... PR Diff内容过长，后续文件已省略 ...\n");
                break;
            }

            prompt.append("\n--- 文件：").append(nullToEmpty(diff.getFilename())).append(" ---\n");
            prompt.append("状态：").append(nullToEmpty(diff.getStatus())).append("\n");
            prompt.append("Diff内容：\n");
            prompt.append("```diff\n");
            prompt.append(limitPatch(diff.getPatch()));
            prompt.append("\n```\n");
        }

        return limitTotalPrompt(prompt.toString());
    }

    private boolean shouldAnalyze(String filename) {
        if (filename == null) {
            return false;
        }

        return filename.endsWith(".java")
                || filename.endsWith(".kt")
                || filename.endsWith(".py")
                || filename.endsWith(".js")
                || filename.endsWith(".ts")
                || filename.endsWith(".vue");
    }

    private String limitPatch(String patch) {
        if (patch == null) {
            return "";
        }

        if (patch.length() <= MAX_PATCH_LENGTH) {
            return patch;
        }

        return patch.substring(0, MAX_PATCH_LENGTH)
                + "\n... 当前文件Diff过长，已截断 ...";
    }

    private String limitTotalPrompt(String prompt) {
        if (prompt == null) {
            return "";
        }

        if (prompt.length() <= MAX_TOTAL_PROMPT_LENGTH) {
            return prompt;
        }

        return prompt.substring(0, MAX_TOTAL_PROMPT_LENGTH)
                + "\n... Prompt总长度过长，已截断 ...";
    }

    @Override
    public String review(String prompt) {
        return deepSeekClient.chat(prompt);
    }

    private String nullToEmpty(String value) {
        return value == null ? "" : value;
    }
}
