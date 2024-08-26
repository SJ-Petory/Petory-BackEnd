drop table if exists Member;
drop table if exists Notification;
drop table if exists FriendInfo;
drop table if exists FriendState;
drop table if exists Pet;
drop table if exists CareGiver;
drop table if exists Schedule;
drop table if exists PetSchedule;
drop table if exists ScheduleCategory;
drop table if exists CustomRepeatPattern;
drop table if exists Post;
drop table if exists Comment;
drop table if exists Sympathy;
drop table if exists PostImage;
drop table if exists PostCategory;

-- Member table
CREATE TABLE Member (
	`member_id`	BIGINT NOT NULL PRIMARY KEY AUTO_INCREMENT ,
	`name`	VARCHAR(10)	NOT NULL,
	`email`	VARCHAR(50)	NOT NULL,
	`password`	VARCHAR(100) NULL,
	`phone`	VARCHAR(15)	NOT NULL,
	`image`	VARCHAR(100) NULL,
	`status` ENUM('ACTIVE', 'INACTIVE', 'DELETED') NOT NULL,
	`created_at`	TIMESTAMP	NOT NULL,
	`updated_at`	TIMESTAMP	NOT NULL
);

-- Notification table
CREATE TABLE `Notification` (
	`notice_id`	BIGINT NOT NULL AUTO_INCREMENT,
	`member_id` BIGINT NOT NULL,
	`type` VARCHAR(200) NOT NULL,
	`entity_id`	BIGINT NOT NULL,
	`is_read`BOOLEAN NOT NULL,
	`created_at` TIMESTAMP	NOT NULL,
	`updated_at` TIMESTAMP	NOT NULL,
	PRIMARY KEY (`notice_id`, `member_id`),
	CONSTRAINT `FK_Member_TO_Notification_1` FOREIGN KEY (`member_id`) REFERENCES `Member` (`member_id`)
);

-- FriendState table
CREATE TABLE `FriendState` (
	`friend_status_id`	BIGINT NOT NULL AUTO_INCREMENT,
	`status`	VARCHAR(50)	NOT NULL,
	PRIMARY KEY (`friend_status_id`)
);

-- FriendInfo table
CREATE TABLE `FriendInfo` (
	`friend_info_id`	BIGINT NOT NULL AUTO_INCREMENT,
	`member_id`	BIGINT	NOT NULL,
	`friend_status_id`	BIGINT	NOT NULL,
	`friend_id`	BIGINT	NOT NULL,
	`created_at`	TIMESTAMP	NOT NULL,
	`updated_at`	TIMESTAMP	NOT NULL,
	PRIMARY KEY (`friend_info_id`, `member_id`, `friend_status_id`),
	CONSTRAINT `FK_Member_TO_FriendInfo_1` FOREIGN KEY (`member_id`) REFERENCES `Member` (`member_id`),
	CONSTRAINT `FK_FriendState_TO_FriendInfo_1` FOREIGN KEY (`friend_status_id`) REFERENCES `FriendState` (`friend_status_id`)
);

-- Pet table
CREATE TABLE `Pet` (
	`pet_id`	BIGINT NOT NULL AUTO_INCREMENT,
	`member_id`	BIGINT	NOT NULL,
	`pet_name`	VARCHAR(30)	NOT NULL,
	`species`	VARCHAR(200)	NOT NULL,
	`breed`	VARCHAR(200)	NOT NULL,
	`pet_gender`	VARCHAR(200)	NOT NULL,
	`pet_age`	TINYINT	NOT NULL,
	`pet_image`	VARCHAR(100)	NULL,
	`memo`	TEXT	NULL,
	`created_at`	TIMESTAMP	NOT NULL,
	`updated_at`	TIMESTAMP	NOT NULL,
	`status`	VARCHAR(200)	NOT NULL,
	PRIMARY KEY (`pet_id`, `member_id`),
	UNIQUE (`pet_id`),
	CONSTRAINT `FK_Member_TO_Pet_1` FOREIGN KEY (`member_id`) REFERENCES `Member` (`member_id`)
);

-- CareGiver table
CREATE TABLE `CareGiver` (
	`care_giver_id`	BIGINT NOT NULL AUTO_INCREMENT,
	`member_id`	BIGINT	NOT NULL,
	`pet_id`	BIGINT	NOT NULL,
	 PRIMARY KEY (`care_giver_id`, `member_id`, `pet_id`),
	 CONSTRAINT `FK_Member_TO_CareGiver_1` FOREIGN KEY (`member_id`) REFERENCES `Member` (`member_id`),
	 CONSTRAINT `FK_Pet_TO_CareGiver_1` FOREIGN KEY (`pet_id`) REFERENCES `Pet` (`pet_id`)
);

-- ScheduleCategory table
CREATE TABLE `ScheduleCategory` (
	`category_id`	BIGINT NOT NULL AUTO_INCREMENT,
	`member_id`	BIGINT	NOT NULL,
	`category_name`	VARCHAR(50)	NOT NULL,
	PRIMARY KEY (`category_id`, `member_id`),
	CONSTRAINT `FK_Member_TO_ScheduleCategory_1` FOREIGN KEY (`member_id`) REFERENCES `Member` (`member_id`)
);

-- Schedule table
CREATE TABLE `Schedule` (
	`schedule_id`	BIGINT NOT NULL AUTO_INCREMENT,
	`category_id`	BIGINT	NOT NULL,
	`member_id`	BIGINT	NOT NULL,
	`schedule_title`	VARCHAR(20)	NOT NULL,
	`schedule_content`	TEXT	NULL,
	`schedule_at`	TIMESTAMP	NOT NULL,
	`repeat_type`	VARCHAR(200)	NOT NULL,
	`repeat_cycle`	VARCHAR(200)	NULL,
	`notice_yn`	BOOLEAN	NOT NULL,
	`notice_at`	BIGINT	NULL,
	`priority`	VARCHAR(200)	NULL,
	`status`	VARCHAR(200)	NOT NULL,
	`created_at`	TIMESTAMP	NOT NULL,
	`updated_at`	TIMESTAMP	NOT NULL,
	PRIMARY KEY (`schedule_id`, `category_id`, `member_id`),
	UNIQUE (`schedule_id`),
	CONSTRAINT `FK_ScheduleCategory_TO_Schedule_1` FOREIGN KEY (`category_id`, `member_id`) REFERENCES `ScheduleCategory` (`category_id`, `member_id`),
        CONSTRAINT `FK_Member_TO_Schedule_1` FOREIGN KEY (`member_id`) REFERENCES `Member` (`member_id`)
);

-- PetSchedule table
CREATE TABLE `PetSchedule` (
    `pet_schedule_id`	BIGINT NOT NULL AUTO_INCREMENT,
	`pet_id`	BIGINT	NOT NULL,
	`schedule_id`	BIGINT	NOT NULL,
	 PRIMARY KEY (`pet_schedule_id`, `pet_id`, `schedule_id`),
	 CONSTRAINT `FK_Pet_TO_PetSchedule_1` FOREIGN KEY (`pet_id`) REFERENCES `Pet` (`pet_id`),
     CONSTRAINT `FK_Schedule_TO_PetSchedule_1` FOREIGN KEY (`schedule_id`) REFERENCES `Schedule` (`schedule_id`)
);

-- CustomRepeatPattern table
CREATE TABLE `CustomRepeatPattern` (
	`id`	BIGINT NOT NULL AUTO_INCREMENT,
	`frequency`	VARCHAR(100)	NOT NULL,
	`interval`	BIGINT	NULL,
	`daysOfWeek`	VARCHAR(200)	NULL,
	`daysOfMonth`	VARCHAR(255)	NULL,
	`endDate`	VARCHAR(255)	NULL,
	CONSTRAINT `PK_CUSTOMREPEATPATTERN` PRIMARY KEY (`id`)
);

-- PostCategory table
CREATE TABLE `PostCategory` (
	`post_category_id`	BIGINT NOT NULL AUTO_INCREMENT,
	`category_name`	VARCHAR(50)	NOT NULL,
	PRIMARY KEY (`post_category_id`)
);

-- Post table
CREATE TABLE `Post` (
	`post_id`	BIGINT NOT NULL AUTO_INCREMENT,
	`member_id`	BIGINT	NOT NULL,
	`post_category_id`	BIGINT	NOT NULL,
	`post_title`	VARCHAR(50)	NOT NULL,
	`post_content`	TEXT	NOT NULL,
	`created_at`	TIMESTAMP	NOT NULL,
	`updated_at`	TIMESTAMP	NOT NULL,
	`status`	VARCHAR(200)	NOT NULL,
	PRIMARY KEY (`post_id`, `member_id`, `post_category_id`),
	UNIQUE(`post_id`),
	CONSTRAINT `FK_Member_TO_Post_1` FOREIGN KEY (`member_id`) REFERENCES `Member` (`member_id`),
	CONSTRAINT `FK_PostCategory_TO_Post_1` FOREIGN KEY (`post_category_id`) REFERENCES `PostCategory` (`post_category_id`)
);

-- Comment table
CREATE TABLE `Comment` (
	`comment_id`	BIGINT NOT NULL AUTO_INCREMENT,
	`post_id`	BIGINT	NOT NULL,
	`member_id`	BIGINT	NOT NULL,
	`content`	TEXT	NOT NULL,
	CONSTRAINT `PK_COMMENT` PRIMARY KEY (`comment_id`, `post_id`, `member_id`),
	CONSTRAINT `FK_Post_TO_Comment_1` FOREIGN KEY (`post_id`) REFERENCES `Post` (`post_id`),
    CONSTRAINT `FK_Member_TO_Comment_1` FOREIGN KEY (`member_id`) REFERENCES `Member` (`member_id`)
);

-- Sympathy table
CREATE TABLE `Sympathy` (
	`sympathy_id`	BIGINT NOT NULL AUTO_INCREMENT,
	`member_id`	BIGINT	NOT NULL,
	`post_id`	BIGINT	NOT NULL,
	`type`	VARCHAR(200)	NOT NULL,
	PRIMARY KEY (`sympathy_id`,	`member_id`, `post_id`),
	CONSTRAINT `FK_Member_TO_Sympathy_1` FOREIGN KEY (`member_id`) REFERENCES `Member` (`member_id`),
	CONSTRAINT `FK_Post_TO_Sympathy_1` FOREIGN KEY (`post_id`) REFERENCES `Post` (`post_id`)
);

-- PostImage table
CREATE TABLE `PostImage` (
	`post_image_id`	BIGINT NOT NULL AUTO_INCREMENT,
	`post_id`	BIGINT	NOT NULL,
	`image`	VARCHAR(100)	NULL,
	PRIMARY KEY (`post_image_id`, `post_id`),
	CONSTRAINT `FK_Post_TO_Postmage_1` FOREIGN KEY (`post_id`) REFERENCES `Post` (`post_id`)
);