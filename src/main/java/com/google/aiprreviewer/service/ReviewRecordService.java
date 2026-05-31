package com.google.aiprreviewer.service;

import com.google.aiprreviewer.model.ReviewRecord;

import java.util.List;

public interface ReviewRecordService {

    void save(ReviewRecord record);

    List<ReviewRecord> listRecent();
}
