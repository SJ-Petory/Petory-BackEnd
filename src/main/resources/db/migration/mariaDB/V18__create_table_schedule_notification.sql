CREATE TABLE `ScheduleNotification` (
    `schedule_notification_id`	BIGINT	NOT NULL,
    `receive_member_id`	BIGINT	NOT NULL,
    `notice_type`	VARCHAR(50)	NOT NULL,
    `entity_id`	BIGINT  NOT NULL,
    `notice_time`	TIMESTAMP	NOT NULL,
    `is_sent`	BOOLEAN	NOT NULL,
    `created_at`	TIMESTAMP	NOT NULL,
    `updated_at`	TIMESTAMP	NOT NULL
);

ALTER TABLE `ScheduleNotification` ADD CONSTRAINT `PK_SCHEDULENOTIFICATION` PRIMARY KEY (
    `schedule_notification_id`
    );

