-- Schedule table
-- ALTER TABLE schedule MODIFY repeat_type ENUM('BASIC', 'CUSTOM');
-- ALTER TABLE schedule MODIFY repeat_cycle ENUM('DAILY', 'WEEKLY', 'MONTHLY', 'YEARLY');
-- ALTER TABLE schedule MODIFY priority ENUM('HIGH', 'MEDIUM', 'LOW');
-- ALTER TABLE schedule MODIFY status ENUM('ONGOING', 'DONE');

-- CustomRepeatPattern
ALTER TABLE CustomRepeatPattern change id custom_repeat_id bigint;
ALTER TABLE CustomRepeatPattern ADD schedule_id BIGINT NOT NULL;
ALTER TABLE CustomRepeatPattern MODIFY frequency varchar(30);
-- ENUM('DAY', 'WEEK', 'MONTH', 'YEAR');
ALTER TABLE CustomRepeatPattern DROP daysOfWeek;
ALTER TABLE CustomRepeatPattern DROP daysOfMonth;
ALTER TABLE CustomRepeatPattern CHANGE endDate end_date TIMESTAMP NULL;
ALTER TABLE CustomRepeatPattern CHANGE `interval` repeat_interval bigint;

-- ALTER TABLE CustomRepeatPattern DROP CONSTRAINT PK_CUSTOMREPEATPATTERN;


ALTER TABLE `CustomRepeatPattern` ADD CONSTRAINT `FK_Schedule_TO_CustomRepeatPattern_1` FOREIGN KEY (`schedule_id`)
REFERENCES `Schedule` (`schedule_id`) ON DELETE CASCADE;

ALTER TABLE CustomRepeatPattern ADD CONSTRAINT unique_id UNIQUE (custom_repeat_id);

-- custom_days_of table
CREATE TABLE custom_repeat_days_of_week (
    custom_repeat_id BIGINT NOT NULL,
    day_of_week ENUM('MONDAY', 'TUESDAY', 'WEDNESDAY', 'THURSDAY', 'FRIDAY', 'SATURDAY', 'SUNDAY') NOT NULL,
    CONSTRAINT fk_custom_repeat FOREIGN KEY (custom_repeat_id)
    REFERENCES CustomRepeatPattern (custom_repeat_id) ON DELETE CASCADE
);

CREATE TABLE custom_repeat_days_of_month (
    custom_repeat_id BIGINT NOT NULL,
    day_of_month INT NOT NULL,
    CONSTRAINT fk_custom_repeat_month FOREIGN KEY (custom_repeat_id)
    REFERENCES CustomRepeatPattern (custom_repeat_id) ON DELETE CASCADE
);