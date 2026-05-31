package com.google.aiprreviewer.model.github;

import lombok.Data;

@Data
public class DiffEntry {
    String filename;

    String status;

    String patch;
}
