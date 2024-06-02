DROP DATABASE IF EXISTS db2;
CREATE DATABASE db2;

USE db2;

CREATE TABLE `movie` (
	`movie_id` INT NOT NULL AUTO_INCREMENT,
	`movie_name` VARCHAR(255) NULL,
	`showtime` INT NULL,
	`rating` VARCHAR(255) NULL,
	`director` VARCHAR(255) NULL,
	`actor` VARCHAR(255) NULL,
	`genre` VARCHAR(255) NULL,
	`instruction` VARCHAR(512) NULL,
	`movie_created_at` DATETIME NULL,
	`grade` FLOAT NULL,
	PRIMARY KEY (`movie_id`)
);

CREATE TABLE `theater` (
	`theater_id` VARCHAR(50) NOT NULL,
	`theater_availability` TINYINT(1) NULL,
	`row_seat` INT NULL,
	`column_seat` INT NULL,
	`total_seat` INT NULL,
	PRIMARY KEY (`theater_id`)
);

CREATE TABLE `schedule` (
	`schedule_id` INT NOT NULL AUTO_INCREMENT,
	`created_at` DATETIME NULL,
	`screening_day` VARCHAR(255) NULL,
	`screening_count` INT NULL,
	`start_time` DATETIME NULL,
	`end_time` DATETIME NULL,
	`movie_id` INT NOT NULL,
	`theater_id` VARCHAR(50) NOT NULL,
	PRIMARY KEY (`schedule_id`),
	FOREIGN KEY (`movie_id`) REFERENCES `movie`(`movie_id`),
	FOREIGN KEY (`theater_id`) REFERENCES `theater`(`theater_id`)
);



CREATE TABLE `seat` (
	`seat_id` INT NOT NULL AUTO_INCREMENT,
	`theater_id` VARCHAR(50) NOT NULL,
	`row` INT NULL,
	`column` INT NULL,
	`seat_availability`	tinyint(1)	NOT NULL,
	PRIMARY KEY (`seat_id`),
	FOREIGN KEY (`theater_id`) REFERENCES `theater`(`theater_id`)
);

CREATE TABLE `member` (
	`member_id`	INT	NOT NULL,
	`name`	varchar(255)	NULL,
	`phonenumber`	varchar(255)	NULL,
	`email`	varchar(255)	NULL,
	`role`	varchar(255)	NULL,
    PRIMARY KEY (`member_id`)
);

CREATE TABLE `reservation` (
	`reservation_id` INT NOT NULL AUTO_INCREMENT,
	`payment_method`	varchar(255) NOT NULL,
	`payment_amount`	int4 NOT NULL,
	`payment_status`	varchar(255)	NOT NULL,
	`payment_date`	datetime NOT NULL,
	`member_id` INT NOT NULL,
	PRIMARY KEY (`reservation_id`),
	FOREIGN KEY (`member_id`) REFERENCES `member`(`member_id`)
);

CREATE TABLE `ticket` (
	`ticket_id` INT NOT NULL AUTO_INCREMENT,
	`ticket_availibility`	tinyint(1)	NOT NULL,
	`standard_price`	int4	NOT NULL,
	`sale_price`	int4	NOT NULL,
	`seat_id` INT NOT NULL,
	`reservation_id` INT NOT NULL,
	`schedule_id` INT NOT NULL,
	`theater_id` VARCHAR(50) NOT NULL,
	PRIMARY KEY (`ticket_id`),
	FOREIGN KEY (`reservation_id`) REFERENCES `reservation`(`reservation_id`),
	FOREIGN KEY (`schedule_id`) REFERENCES `schedule`(`schedule_id`),
	FOREIGN KEY (`seat_id`) REFERENCES `seat`(`seat_id`),
	FOREIGN KEY (`theater_id`) REFERENCES `theater`(`theater_id`)
);
