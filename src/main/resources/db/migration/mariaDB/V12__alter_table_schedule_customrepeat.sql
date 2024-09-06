--Schedule table
ALTER TABLE schedule ALTER COLUMN repeat_type ENUM('BASIC', 'CUSTOM');
ALTER TABLE schedule ALTER COLUMN repeat_cycle ENUM('DAILY', 'WEEKLY', 'MONTHLY', 'YEARLY');
ALTER TABLE schedule ALTER COLUMN priority ENUM('HIGH', 'MEDIUM', 'LOW');
ALTER TABLE schedule ALTER COLUMN status ENUM('ONGOING', 'DONE');

--CustomRepeatPattern
ALTER TABLE CustomRepeatPattern ALTER COLUMN id RENAME TO custom_repeat_id;
ALTER TABLE CustomRepeatPattern ADD COLUMN schedule_id BIGINT NOT NULL;
ALTER TABLE CustomRepeatPattern ALTER COLUMN frequency ENUM('DAY', 'WEEK', 'MONTH', 'YEAR');
ALTER TABLE CustomRepeatPattern DROP COLUMN daysOfWeek;
ALTER TABLE CustomRepeatPattern DROP COLUMN daysOfMonth;
ALTER TABLE CustomRepeatPattern ALTER COLUMN endDate RENAME TO end_date;
ALTER TABLE CustomRepeatPattern ALTER COLUMN end_date TIMESTAMP NULL;
ALTER TABLE CustomRepeatPattern ALTER COLUMN `interval` RENAME TO repeat_interval;

ALTER TABLE CustomRepeatPattern DROP CONSTRAINT PK_CUSTOMREPEATPATTERN;

ALTER TABLE `CustomRepeatPattern` ADD CONSTRAINT `PK_CUSTOMREPEATPATTERN` PRIMARY KEY (
	`custom_repeat_id`,
	`schedule_id`
);

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