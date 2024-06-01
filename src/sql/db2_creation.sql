DROP DATABASE IF EXISTS db2;
CREATE DATABASE db2;

USE db2;

CREATE TABLE `movie` (
	`movie_id`	INT	NOT NULL,
	`movie_name`	varchar(255)	NULL,
	`showtime`	int4	NULL,
	`rating`	varchar(255)	NULL,
	`director`	varchar(255)	NULL,
	`actor`	varchar(255)	NULL,
	`genre`	varchar(255)	NULL,
	`instruction`	varchar(512)	NULL,
	`movie_created_at`	datetime	NULL,
	`grade`	float	NULL
);

CREATE TABLE `schedule` (
	`schedule_id`	INT	NOT NULL,
	`created_at`	datetime	NULL,
	`screening_day`	varchar(255)	NULL,
	`screening_count`	int4	NULL,
	`start_time`	datetime	NULL,
	`end_time`	datetime	NULL,
	`movie_id`	INT	NOT NULL,
	`theater_id`	INT	NOT NULL
);

CREATE TABLE `theater` (
	`theater_id`	INT	NOT NULL,
	`theater_availability`	tinyint(1)	NULL,
	`row_seat`	int4	NULL,
	`column_seat`	int4	NULL,
	`total_seat`	int4	NULL
);

CREATE TABLE `seat` (
	`seat_id`	INT	NOT NULL,
	`seat_availability`	tinyint(1)	NULL,
	`theater_id`	INT	NOT NULL
);

CREATE TABLE `ticket` (
	`ticket_id`	INT	NOT NULL,
	`ticket_availibility`	tinyint(1)	NULL,
	`standard_price`	int4	NULL,
	`sale_price`	int4	NULL,
	`seat_id`	INT	NOT NULL,
	`reservation_id`	INT	NOT NULL,
	`schedule_id`	INT	NOT NULL,
	`theater_id`	INT	NOT NULL
);

CREATE TABLE `reservation` (
	`reservation_id`	INT	NOT NULL,
	`payment_method`	varchar(255)	NULL,
	`payment_amount`	int4	NULL,
	`payment_status`	varchar(255)	NULL,
	`payment_date`	datetime	NULL,
	`member_id`	INT	NOT NULL
);

CREATE TABLE `member` (
	`member_id`	INT	NOT NULL,
	`name`	varchar(255)	NULL,
	`phonenumber`	varchar(255)	NULL,
	`email`	varchar(255)	NULL,
	`role`	varchar(255)	NULL
);


ALTER TABLE `movie` ADD CONSTRAINT `PK_MOVIE` PRIMARY KEY (
	`movie_id`
);

ALTER TABLE `schedule` ADD CONSTRAINT `PK_SCHEDULE` PRIMARY KEY (
	`schedule_id`
);

ALTER TABLE `theater` ADD CONSTRAINT `PK_THEATER` PRIMARY KEY (
	`theater_id`
);

ALTER TABLE `seat` ADD CONSTRAINT `PK_SEAT` PRIMARY KEY (
	`seat_id`
);

ALTER TABLE `ticket` ADD CONSTRAINT `PK_TICKET` PRIMARY KEY (
	`ticket_id`
);

ALTER TABLE `reservation` ADD CONSTRAINT `PK_RESERVATION` PRIMARY KEY (
	`reservation_id`
);

ALTER TABLE `member` ADD CONSTRAINT `PK_MEMBER` PRIMARY KEY (
	`member_id`
);


