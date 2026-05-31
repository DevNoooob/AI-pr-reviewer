const form = document.querySelector("#reviewForm");
const prUrlInput = document.querySelector("#prUrl");
const submitButton = document.querySelector("#submitButton");
const metaPanel = document.querySelector("#metaPanel");
const reportContent = document.querySelector("#reportContent");
const copyButton = document.querySelector("#copyButton");
const historyList = document.querySelector("#historyList");
const refreshHistoryButton = document.querySelector("#refreshHistoryButton");

const text = {
    enterPrUrl: "\u8bf7\u8f93\u5165 GitHub Pull Request \u5730\u5740\u3002",
    requestFailed: "\u8bf7\u6c42\u5931\u8d25\uff0cHTTP \u72b6\u6001\u7801\uff1a",
    analyzeFailed: "\u5206\u6790\u5931\u8d25\uff0c\u8bf7\u7a0d\u540e\u91cd\u8bd5\u3002",
    configFailed: "\u5206\u6790\u5931\u8d25\uff0c\u8bf7\u68c0\u67e5\u540e\u7aef\u670d\u52a1\u548c API Key \u914d\u7f6e\u3002",
    copied: "\u5df2\u590d\u5236",
    copyFailed: "\u590d\u5236\u5931\u8d25",
    copyMarkdown: "\u590d\u5236 Markdown",
    analyzing: "\u5206\u6790\u4e2d...",
    startAnalyze: "\u5f00\u59cb\u5206\u6790",
    loading: "\u6b63\u5728\u83b7\u53d6 PR Diff \u5e76\u751f\u6210\u8bc4\u5ba1\u62a5\u544a...",
    prTitle: "PR \u6807\u9898",
    state: "\u72b6\u6001",
    headRef: "\u6e90\u5206\u652f",
    baseRef: "\u76ee\u6807\u5206\u652f",
    analyzeTime: "\u5206\u6790\u65f6\u95f4",
    prUrl: "PR \u5730\u5740",
    emptyReport: "\u6682\u65e0\u8bc4\u5ba1\u62a5\u544a\u3002",
    emptyHistory: "\u6682\u65e0\u5386\u53f2\u8bb0\u5f55\u3002",
    untitledPr: "Untitled PR"
};

let latestMarkdown = "";

form.addEventListener("submit", async (event) => {
    event.preventDefault();

    const prUrl = prUrlInput.value.trim();
    if (!prUrl) {
        showError(text.enterPrUrl);
        return;
    }

    setLoading(true);
    copyButton.disabled = true;
    latestMarkdown = "";

    try {
        const response = await fetch("/api/review/analyze", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({ prUrl })
        });

        if (!response.ok) {
            throw new Error(`${text.requestFailed}${response.status}`);
        }

        const result = await response.json();
        if (result.code !== 200 || !result.data) {
            throw new Error(result.message || text.analyzeFailed);
        }

        renderResult(result.data);
        loadHistory();
    } catch (error) {
        showError(error.message || text.configFailed);
    } finally {
        setLoading(false);
    }
});

copyButton.addEventListener("click", async () => {
    if (!latestMarkdown) {
        return;
    }

    const copied = await copyMarkdown(latestMarkdown);
    showCopyStatus(copied);
});

refreshHistoryButton.addEventListener("click", loadHistory);
loadHistory();

function setLoading(isLoading) {
    submitButton.disabled = isLoading;
    submitButton.textContent = isLoading ? text.analyzing : text.startAnalyze;

    if (isLoading) {
        reportContent.className = "report-content loading";
        reportContent.innerHTML = `
            <div>
                <div class="spinner" aria-hidden="true"></div>
                <p>${text.loading}</p>
            </div>
        `;
    }
}

function renderResult(data) {
    latestMarkdown = data.reviewReport || "";
    copyButton.disabled = !latestMarkdown;

    metaPanel.innerHTML = `
        <div class="meta-list">
            ${metaItem(text.prTitle, data.prTitle)}
            ${metaItem(text.state, `<span class="state-pill">${escapeHtml(data.prState || "unknown")}</span>`, true)}
            ${metaItem(text.headRef, data.headRef)}
            ${metaItem(text.baseRef, data.baseRef)}
            ${metaItem(text.analyzeTime, formatTime(data.analyzeTime))}
            ${metaItem(text.prUrl, linkTo(data.prUrl), true)}
        </div>
    `;

    reportContent.className = "report-content markdown";
    reportContent.innerHTML = renderMarkdown(latestMarkdown || text.emptyReport);
}

async function loadHistory() {
    try {
        const response = await fetch("/api/review/history");
        if (!response.ok) {
            throw new Error(`${text.requestFailed}${response.status}`);
        }

        const result = await response.json();
        if (result.code !== 200) {
            throw new Error(result.message || text.analyzeFailed);
        }

        renderHistory(result.data || []);
    } catch (error) {
        historyList.innerHTML = `<p class="history-empty">${escapeHtml(error.message || text.configFailed)}</p>`;
    }
}

function renderHistory(records) {
    if (!records.length) {
        historyList.innerHTML = `<p class="history-empty">${text.emptyHistory}</p>`;
        return;
    }

    historyList.innerHTML = records.map((record, index) => `
        <button class="history-item" type="button" data-index="${index}">
            <p class="history-title">${escapeHtml(record.prTitle || record.prUrl || text.untitledPr)}</p>
            <p class="history-meta">${escapeHtml(formatTime(record.createTime))}</p>
        </button>
    `).join("");

    historyList.querySelectorAll(".history-item").forEach((item) => {
        item.addEventListener("click", () => {
            const record = records[Number(item.dataset.index)];
            renderHistoryRecord(record);
        });
    });
}

function renderHistoryRecord(record) {
    latestMarkdown = record.reviewReport || "";
    copyButton.disabled = !latestMarkdown;

    metaPanel.innerHTML = `
        <div class="meta-list">
            ${metaItem(text.prTitle, record.prTitle)}
            ${metaItem(text.analyzeTime, formatTime(record.createTime))}
            ${metaItem(text.prUrl, linkTo(record.prUrl), true)}
        </div>
    `;

    reportContent.className = "report-content markdown";
    reportContent.innerHTML = renderMarkdown(latestMarkdown || text.emptyReport);
}

function metaItem(label, value, allowHtml = false) {
    const content = allowHtml ? value : escapeHtml(value || "-");
    return `
        <section class="meta-item">
            <p class="meta-label">${label}</p>
            <p class="meta-value">${content || "-"}</p>
        </section>
    `;
}

function linkTo(url) {
    if (!url) {
        return "-";
    }

    const safeUrl = escapeHtml(url);
    return `<a href="${safeUrl}" target="_blank" rel="noreferrer">${safeUrl}</a>`;
}

function formatTime(value) {
    if (!value) {
        return "-";
    }

    const date = new Date(value);
    if (Number.isNaN(date.getTime())) {
        return value;
    }

    return date.toLocaleString("zh-CN", {
        year: "numeric",
        month: "2-digit",
        day: "2-digit",
        hour: "2-digit",
        minute: "2-digit"
    });
}

function showError(message) {
    reportContent.className = "report-content";
    reportContent.innerHTML = `<div class="error">${escapeHtml(message)}</div>`;
}

async function copyMarkdown(markdown) {
    if (navigator.clipboard && window.isSecureContext) {
        try {
            await navigator.clipboard.writeText(markdown);
            return true;
        } catch (error) {
            // Fall through to the legacy copy path below.
        }
    }

    const textarea = document.createElement("textarea");
    textarea.value = markdown;
    textarea.setAttribute("readonly", "");
    textarea.style.position = "fixed";
    textarea.style.top = "-9999px";
    textarea.style.left = "-9999px";
    textarea.style.opacity = "0";
    document.body.appendChild(textarea);

    const selection = document.getSelection();
    const selectedRange = selection && selection.rangeCount > 0
        ? selection.getRangeAt(0)
        : null;

    textarea.select();
    textarea.setSelectionRange(0, textarea.value.length);

    let copied = false;
    try {
        copied = document.execCommand("copy");
    } finally {
        document.body.removeChild(textarea);
        if (selectedRange && selection) {
            selection.removeAllRanges();
            selection.addRange(selectedRange);
        }
    }

    return copied;
}

function showCopyStatus(copied) {
    copyButton.textContent = copied ? text.copied : text.copyFailed;
    copyButton.disabled = true;

    setTimeout(() => {
        copyButton.textContent = text.copyMarkdown;
        copyButton.disabled = !latestMarkdown;
    }, 1400);
}

function renderMarkdown(markdown) {
    const lines = markdown.replace(/\r\n/g, "\n").split("\n");
    const html = [];
    let inCode = false;
    let listType = "";
    let paragraph = [];

    const flushParagraph = () => {
        if (paragraph.length) {
            html.push(`<p>${inlineMarkdown(paragraph.join(" "))}</p>`);
            paragraph = [];
        }
    };

    const closeList = () => {
        if (listType) {
            html.push(`</${listType}>`);
            listType = "";
        }
    };

    for (const line of lines) {
        const trimmed = line.trim();

        if (trimmed.startsWith("```")) {
            flushParagraph();
            closeList();
            if (inCode) {
                html.push("</code></pre>");
                inCode = false;
            } else {
                inCode = true;
                html.push("<pre><code>");
            }
            continue;
        }

        if (inCode) {
            html.push(`${escapeHtml(line)}\n`);
            continue;
        }

        if (!trimmed) {
            flushParagraph();
            closeList();
            continue;
        }

        const heading = trimmed.match(/^(#{1,3})\s+(.+)$/);
        if (heading) {
            flushParagraph();
            closeList();
            const level = heading[1].length;
            html.push(`<h${level}>${inlineMarkdown(heading[2])}</h${level}>`);
            continue;
        }

        const unordered = trimmed.match(/^[-*]\s+(.+)$/);
        if (unordered) {
            flushParagraph();
            if (listType !== "ul") {
                closeList();
                listType = "ul";
                html.push("<ul>");
            }
            html.push(`<li>${inlineMarkdown(unordered[1])}</li>`);
            continue;
        }

        const ordered = trimmed.match(/^\d+\.\s+(.+)$/);
        if (ordered) {
            flushParagraph();
            if (listType !== "ol") {
                closeList();
                listType = "ol";
                html.push("<ol>");
            }
            html.push(`<li>${inlineMarkdown(ordered[1])}</li>`);
            continue;
        }

        if (trimmed.startsWith("> ")) {
            flushParagraph();
            closeList();
            html.push(`<blockquote>${inlineMarkdown(trimmed.slice(2))}</blockquote>`);
            continue;
        }

        paragraph.push(trimmed);
    }

    flushParagraph();
    closeList();

    if (inCode) {
        html.push("</code></pre>");
    }

    return html.join("");
}

function inlineMarkdown(value) {
    return escapeHtml(value)
        .replace(/\*\*(.+?)\*\*/g, "<strong>$1</strong>")
        .replace(/`([^`]+?)`/g, "<code>$1</code>");
}

function escapeHtml(value) {
    return String(value ?? "")
        .replace(/&/g, "&amp;")
        .replace(/</g, "&lt;")
        .replace(/>/g, "&gt;")
        .replace(/"/g, "&quot;")
        .replace(/'/g, "&#39;");
}
