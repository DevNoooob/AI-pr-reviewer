package com.google.aiprreviewer.mapper;

import com.google.aiprreviewer.model.ReviewRecord;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ReviewRecordMapper {

    @Insert("""
            INSERT INTO review_record (pr_url, pr_title, review_report, create_time)
            VALUES (#{prUrl}, #{prTitle}, #{reviewReport}, #{createTime})
            """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(ReviewRecord record);

    @Select("""
            SELECT id, pr_url, pr_title, review_report, create_time
            FROM review_record
            ORDER BY create_time DESC, id DESC
            LIMIT 20
            """)
    List<ReviewRecord> selectRecent();
}
