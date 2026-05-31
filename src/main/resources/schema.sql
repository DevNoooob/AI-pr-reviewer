CREATE TABLE IF NOT EXISTS review_record
(
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    pr_url VARCHAR(500),
    pr_title VARCHAR(255),
    review_report LONGTEXT,
    create_time DATETIME
);
