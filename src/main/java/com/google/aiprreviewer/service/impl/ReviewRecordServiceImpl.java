package com.google.aiprreviewer.service.impl;

import com.google.aiprreviewer.mapper.ReviewRecordMapper;
import com.google.aiprreviewer.model.ReviewRecord;
import com.google.aiprreviewer.service.ReviewRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReviewRecordServiceImpl implements ReviewRecordService {

    @Autowired
    private ReviewRecordMapper reviewRecordMapper;

    @Override
    public void save(ReviewRecord record) {
        reviewRecordMapper.insert(record);
    }

    @Override
    public List<ReviewRecord> listRecent() {
        return reviewRecordMapper.selectRecent();
    }
}
