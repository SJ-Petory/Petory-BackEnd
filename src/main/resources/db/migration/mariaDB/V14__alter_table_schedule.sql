DROP TABLE custom_repeat_days_of_week;
DROP TABLE custom_repeat_days_of_month;
DROP TABLE customrepeatpattern;


ALTER TABLE Schedule DROP repeat_type;
ALTER TABLE Schedule DROP repeat_cycle;
ALTER TABLE Schedule DROP schedule_at;
ALTER TABLE Schedule ADD repeat_yn BOOLEAN NOT NULL;

CREATE TABLE `SelectDate` (
	`select_id`	BIGINT	NOT NULL AUTO_INCREMENT,
	`schedule_id`	BIGINT	NOT NULL,
	`selected_date`	TIMESTAMP	NOT NULL,
	PRIMARY KEY (`select_id`, `schedule_id`),
	CONSTRAINT `FK_Schedule_TO_SelectDate_1` FOREIGN KEY (`schedule_id`) REFERENCES `Schedule` (`schedule_id`)
);

CREATE TABLE `RepeatPattern` (
	`repeat_pattern_id`	BIGINT	NOT NULL AUTO_INCREMENT,
	`schedule_id`	BIGINT	NOT NULL,
	`frequency`	VARCHAR(100) NOT NULL,
	`repeat_interval`	BIGINT	NULL,
	`start_date`	TIMESTAMP	NOT NULL,
	`end_date`	TIMESTAMP	NOT NULL,
	 PRIMARY KEY (`repeat_pattern_id`, `schedule_id`),
	 UNIQUE(`repeat_pattern_id`),
	 CONSTRAINT `FK_Schedule_TO_RepeatPattern_1` FOREIGN KEY (`schedule_id`) REFERENCES `Schedule` (`schedule_id`)
);

-- custom_days_of table
CREATE TABLE repeat_days_of_week (
    repeat_pattern_id BIGINT NOT NULL,
    day_of_week ENUM('MONDAY', 'TUESDAY', 'WEDNESDAY', 'THURSDAY', 'FRIDAY', 'SATURDAY', 'SUNDAY') NOT NULL,
    CONSTRAINT fk_repeat_week FOREIGN KEY (repeat_pattern_id)
    REFERENCES RepeatPattern (repeat_pattern_id) ON DELETE CASCADE
);

CREATE TABLE repeat_days_of_month (
    repeat_pattern_id BIGINT NOT NULL,
    day_of_month INT NOT NULL,
    CONSTRAINT fk_repeat_month FOREIGN KEY (repeat_pattern_id)
    REFERENCES RepeatPattern (repeat_pattern_id) ON DELETE CASCADE
);