CREATE TABLE `ScheduleNotificationReceiver` (
`schedule_notification_receiver_id`	BIGINT	NOT NULL,
`schedule_notification_id`	BIGINT	NOT NULL,
`member_id`	BIGINT	NOT NULL,
`created_at`	TIMESTAMP	NOT NULL,
`updated_at`	TIMESTAMP	NOT NULL
);

ALTER TABLE `ScheduleNotificationReceiver` ADD CONSTRAINT `PK_SCHEDULENOTIFICATIONRECEIVER` PRIMARY KEY (
    `schedule_notification_id`, `member_id`);

ALTER TABLE `ScheduleNotificationReceiver` ADD CONSTRAINT `FK_ScheduleNotification_TO_ScheduleNotificationReceiver_1`
    FOREIGN KEY (`schedule_notification_id`)
        REFERENCES `ScheduleNotification` (`schedule_notification_id`);

ALTER TABLE `ScheduleNotificationReceiver` ADD CONSTRAINT `FK_Member_TO_ScheduleNotificationReceiver_1` FOREIGN KEY (`member_id`) REFERENCES `Member` (`member_id`);