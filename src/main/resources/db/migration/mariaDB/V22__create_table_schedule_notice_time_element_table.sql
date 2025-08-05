CREATE TABLE schedule_notification_notice_times
(
    schedule_notification_id BIGINT    NOT NULL,
    notice_time              TIMESTAMP NOT NULL,
    -- JPA는 컬렉션 인덱스 없이 관리하므로 id는 필요 없음
    FOREIGN KEY (schedule_notification_id) REFERENCES schedulenotification (schedule_notification_id)
);

CREATE INDEX idx_notice_time
    ON schedule_notification_notice_times (notice_time);