CREATE TABLE `Species` (
	`species_id`	BIGINT	NOT NULL,
	`species_name`	VARCHAR(50)	NOT NULL,
	PRIMARY KEY (`species_id`)
);

CREATE TABLE `Breed` (
	`breed_id`	BIGINT	NOT NULL,
	`species_id`	BIGINT	NOT NULL,
	`breed_name` VARCHAR(50)	NOT NULL,
	PRIMARY KEY (`breed_id`, `species_id`),
	CONSTRAINT `FK_Species_TO_Breed_1` FOREIGN KEY (`species_id`) REFERENCES `Species` (`species_id`)
);

ALTER TABLE `Pet` ADD `species_id` BIGINT NOT NULL;