-- 1. root/1234 관리자 계정 
ALTER USER 'root'@'localhost' IDENTIFIED BY '1234';

-- 2. root 계정에 모든 권한 부여
GRANT ALL PRIVILEGES ON *.* TO 'root'@'localhost';

-- 3. member 테이블에 관리자 계정 추가
-- 관리자 계정 name이 존재하지 않으면 insert
INSERT INTO `member` (`member_id`, `name`, `phonenumber`, `email`, `role`)
SELECT 0, 'root', '01073947182', 'admin@example.com', 'ADMIN'
WHERE (SELECT COUNT(*) FROM `member` WHERE `name` = 'root') = 0;

-- procedure 함수 초기화
DROP PROCEDURE IF EXISTS InitializeDatabase;

-- 4. 데이터베이스 초기화 함수 구현
DELIMITER //
CREATE PROCEDURE InitializeDatabase()
BEGIN
    DECLARE admin_role VARCHAR(255);
    
    -- 관리자의 역할(role) 확인
    SELECT role INTO admin_role FROM member WHERE role = 'ADMIN' LIMIT 1;

    -- 관리자일 경우에만 테이블 초기화
    IF admin_role = 'ADMIN' THEN
        -- 초기화 전 모든 테이블 삭제
        DROP TABLE IF EXISTS `ticket`;
        DROP TABLE IF EXISTS `reservation`;
        DROP TABLE IF EXISTS `schedule`;
        DROP TABLE IF EXISTS `seat`;
        DROP TABLE IF EXISTS `theater`;
        DROP TABLE IF EXISTS `movie`;

        -- 영화 테이블 생성
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
        
        INSERT INTO `movie` (`movie_name`, `showtime`, `rating`, `director`, `actor`, `genre`, `instruction`, `movie_created_at`, `grade`)
        VALUES
        ('바람막이', 120, 'C', '이효성', '송철수', '액션', '송철수의 연기가 돋보이는 액션 영화', '2023-01-01', 8.5),
        ('모 아니면 도', 110, 'B', '김현수', '김현수', '코미디', '배꼽이 빠지게 웃을 수 있는 코미디 영화', '2023-02-01', 9.8),
        ('서프라이즈', 180, 'A', '홍영희', '전지민', '드라마', '전지민의 감정 이입을 통해 공감할 수 있는 인상적인 영화', '2023-03-01', 6.5),
        ('수첩', 95, 'C', '장지선', '박태리', '로맨틱코미디', 'MZ 세대 기준 로맨틱코미디 추천 영화 1위', '2023-04-01', 8.9),
        ('환경과 유전', 130, 'A', '이수민', '김유정', '다큐멘터리', '사색에 잠기고 싶다면 봐야하는 영화', '2023-05-01', 5.5),
        ('움이의 세계', 105, 'C', '김혁', '김움', '애니메이션', '일상에 지친 당신에게 추천하는 감성 애니메이션', '2023-06-01', 7.3),
        ('베이징의 연인', 140, 'B', '김수정', '신보윤', '로맨스', '꿈과 사랑이 가득한 따뜻한 로맨스 영화', '2023-07-01', 8.2),
        ('어둠', 175, 'B', '김길동', '이수헌', '호러', '여름철 섬뜩해지고 싶다면 강추', '2023-08-01', 9.1),
        ('판타지', 100, 'C', '이수현', '이동욱', '판타지', '상상력을 자극하는 환상적인 판타지 영화', '2023-09-01', 7.7),
        ('노래', 125, 'B', '이철', '전혜진', '가족', '가족과 함께 보기 좋은 따뜻한 가족 영화', '2023-10-01', 8.6),
        ('신비', 135, 'A', '임빈', '박동빈', '미스터리', '흥미진진한 미스터리 영화', '2023-11-01', 6.9),
        ('서스펜스', 115, 'A', '조은미', '전도연', '스릴러', '긴장감과 반전이 있는 스릴러 영화', '2023-12-01', 5.8);
        
        -- 상영관 테이블 생성
		CREATE TABLE `theater` (
			`theater_id` VARCHAR(50) NOT NULL,
			`theater_availability` TINYINT(1) NULL,
			`row_seat` INT NULL,
			`column_seat` INT NULL,
			`total_seat` INT NULL,
			PRIMARY KEY (`theater_id`)
		);
        
		INSERT INTO `theater` (`theater_id`, `theater_availability`, `row_seat`, `column_seat`, `total_seat`)
		VALUES
		('A', 1, 10, 10, 100), 
		('B', 1, 10, 12, 120), 
		('C', 1, 8, 8, 64),
		('D', 1, 8, 10, 80),  
		('E', 1, 6, 8, 48),   
		('F', 1, 7, 9, 63),   
		('G', 1, 9, 11, 99),  
		('H', 1, 10, 10, 100),
		('I', 1, 8, 12, 96),  
		('J', 1, 6, 10, 60),  
		('K', 1, 7, 11, 77),  
		('L', 1, 9, 9, 81),  
		('M', 1, 10, 8, 80),  
		('N', 1, 8, 10, 80),  
		('O', 1, 7, 12, 84);
        
        -- 상영일정 테이블 생성
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
			FOREIGN KEY (`movie_id`) REFERENCES `movie`(`movie_id`) ON DELETE CASCADE,
			FOREIGN KEY (`theater_id`) REFERENCES `theater`(`theater_id`)
		);
        
        INSERT INTO `schedule` (`created_at`, `screening_day`, `screening_count`, `start_time`, `end_time`, `movie_id`, `theater_id`)
        VALUES
        ('2024-01-01', '월요일', 2, '2024-02-01 10:00:00', DATE_ADD('2024-02-01 10:00:00', INTERVAL (SELECT `showtime` FROM `movie` WHERE `movie_name` = '바람막이') MINUTE), (SELECT `movie_id` FROM `movie` WHERE `movie_name` = '바람막이'), 'A'),
        ('2024-01-02', '화요일', 3, '2024-02-02 13:00:00', DATE_ADD('2024-02-02 13:00:00', INTERVAL (SELECT `showtime` FROM `movie` WHERE `movie_name` = '모 아니면 도') MINUTE), (SELECT `movie_id` FROM `movie` WHERE `movie_name` = '모 아니면 도'), 'B'),
        ('2024-01-03', '수요일', 4, '2024-03-01 09:00:00', DATE_ADD('2024-03-01 09:00:00', INTERVAL (SELECT `showtime` FROM `movie` WHERE `movie_name` = '서프라이즈') MINUTE), (SELECT `movie_id` FROM `movie` WHERE `movie_name` = '서프라이즈'), 'C'),
        ('2024-01-04', '목요일', 5, '2024-04-01 11:00:00', DATE_ADD('2024-04-01 11:00:00', INTERVAL (SELECT `showtime` FROM `movie` WHERE `movie_name` = '수첩') MINUTE), (SELECT `movie_id` FROM `movie` WHERE `movie_name` = '수첩'), 'A'),
        ('2024-01-05', '금요일', 2, '2024-05-01 15:00:00', DATE_ADD('2024-05-01 15:00:00', INTERVAL (SELECT `showtime` FROM `movie` WHERE `movie_name` = '환경과 유전') MINUTE), (SELECT `movie_id` FROM `movie` WHERE `movie_name` = '환경과 유전'), 'B'),
        ('2024-01-01', '월요일', 2, '2024-02-01 15:00:00', DATE_ADD('2024-02-01 15:00:00', INTERVAL (SELECT `showtime` FROM `movie` WHERE `movie_name` = '움이의 세계') MINUTE), (SELECT `movie_id` FROM `movie` WHERE `movie_name` = '움이의 세계'), 'D'),
        ('2024-01-02', '화요일', 3, '2024-02-02 09:00:00', DATE_ADD('2024-02-02 09:00:00', INTERVAL (SELECT `showtime` FROM `movie` WHERE `movie_name` = '베이징의 연인') MINUTE), (SELECT `movie_id` FROM `movie` WHERE `movie_name` = '베이징의 연인'), 'F'),
        ('2024-01-03', '수요일', 4, '2024-03-01 17:00:00', DATE_ADD('2024-03-01 17:00:00', INTERVAL (SELECT `showtime` FROM `movie` WHERE `movie_name` = '어둠') MINUTE), (SELECT `movie_id` FROM `movie` WHERE `movie_name` = '어둠'), 'C'),
        ('2024-01-04', '목요일', 5, '2024-04-01 18:00:00', DATE_ADD('2024-04-01 18:00:00', INTERVAL (SELECT `showtime` FROM `movie` WHERE `movie_name` = '판타지') MINUTE), (SELECT `movie_id` FROM `movie` WHERE `movie_name` = '판타지'), 'E'),
        ('2024-01-05', '금요일', 2, '2024-05-01 10:00:00', DATE_ADD('2024-05-01 10:00:00', INTERVAL (SELECT `showtime` FROM `movie` WHERE `movie_name` = '노래') MINUTE), (SELECT `movie_id` FROM `movie` WHERE `movie_name` = '노래'), 'B'),
        ('2024-01-01', '월요일', 2, '2024-02-01 20:00:00', DATE_ADD('2024-02-01 20:00:00', INTERVAL (SELECT `showtime` FROM `movie` WHERE `movie_name` = '신비') MINUTE), (SELECT `movie_id` FROM `movie` WHERE `movie_name` = '신비'), 'A'),
        ('2024-01-02', '화요일', 3, '2024-02-02 18:00:00', DATE_ADD('2024-02-02 18:00:00', INTERVAL (SELECT `showtime` FROM `movie` WHERE `movie_name` = '서스펜스') MINUTE), (SELECT `movie_id` FROM `movie` WHERE `movie_name` = '서스펜스'), 'B'),
        
        ('2024-01-08', '월요일', 3, '2024-02-01 10:00:00', DATE_ADD('2024-02-01 10:00:00', INTERVAL (SELECT `showtime` FROM `movie` WHERE `movie_name` = '바람막이') MINUTE), (SELECT `movie_id` FROM `movie` WHERE `movie_name` = '바람막이'), 'F'),
        ('2024-01-09', '화요일', 4, '2024-02-02 13:00:00', DATE_ADD('2024-02-02 13:00:00', INTERVAL (SELECT `showtime` FROM `movie` WHERE `movie_name` = '모 아니면 도') MINUTE), (SELECT `movie_id` FROM `movie` WHERE `movie_name` = '모 아니면 도'), 'D'),
        ('2024-01-10', '수요일', 5, '2024-03-01 09:00:00', DATE_ADD('2024-03-01 09:00:00', INTERVAL (SELECT `showtime` FROM `movie` WHERE `movie_name` = '서프라이즈') MINUTE), (SELECT `movie_id` FROM `movie` WHERE `movie_name` = '서프라이즈'), 'C'),
        ('2024-01-11', '목요일', 6, '2024-04-01 11:00:00', DATE_ADD('2024-04-01 11:00:00', INTERVAL (SELECT `showtime` FROM `movie` WHERE `movie_name` = '수첩') MINUTE), (SELECT `movie_id` FROM `movie` WHERE `movie_name` = '수첩'), 'A'),
        ('2024-01-12', '금요일', 3, '2024-05-01 15:00:00', DATE_ADD('2024-05-01 15:00:00', INTERVAL (SELECT `showtime` FROM `movie` WHERE `movie_name` = '환경과 유전') MINUTE), (SELECT `movie_id` FROM `movie` WHERE `movie_name` = '환경과 유전'), 'D'),
        ('2024-01-08', '월요일', 3, '2024-02-01 15:00:00', DATE_ADD('2024-02-01 15:00:00', INTERVAL (SELECT `showtime` FROM `movie` WHERE `movie_name` = '움이의 세계') MINUTE), (SELECT `movie_id` FROM `movie` WHERE `movie_name` = '움이의 세계'), 'A'),
        ('2024-01-09', '화요일', 4, '2024-02-01 19:00:00', DATE_ADD('2024-02-01 19:00:00', INTERVAL (SELECT `showtime` FROM `movie` WHERE `movie_name` = '베이징의 연인') MINUTE), (SELECT `movie_id` FROM `movie` WHERE `movie_name` = '베이징의 연인'), 'B'),
        ('2024-01-10', '수요일', 5, '2024-03-01 14:00:00', DATE_ADD('2024-03-01 14:00:00', INTERVAL (SELECT `showtime` FROM `movie` WHERE `movie_name` = '어둠') MINUTE), (SELECT `movie_id` FROM `movie` WHERE `movie_name` = '어둠'), 'F'),
        ('2024-01-11', '목요일', 6, '2024-04-01 15:00:00', DATE_ADD('2024-04-01 15:00:00', INTERVAL (SELECT `showtime` FROM `movie` WHERE `movie_name` = '판타지') MINUTE), (SELECT `movie_id` FROM `movie` WHERE `movie_name` = '판타지'), 'A'), 
        ('2024-01-12', '금요일', 3, '2024-05-01 07:00:00', DATE_ADD('2024-05-01 07:00:00', INTERVAL (SELECT `showtime` FROM `movie` WHERE `movie_name` = '노래') MINUTE), (SELECT `movie_id` FROM `movie` WHERE `movie_name` = '노래'), 'B'), 
        ('2024-01-08', '월요일', 3, '2024-02-01 21:00:00', DATE_ADD('2024-02-01 21:00:00', INTERVAL (SELECT `showtime` FROM `movie` WHERE `movie_name` = '신비') MINUTE), (SELECT `movie_id` FROM `movie` WHERE `movie_name` = '신비'), 'C'), 
        ('2024-01-09', '화요일', 4, '2024-02-03 08:00:00', DATE_ADD('2024-02-03 08:00:00', INTERVAL (SELECT `showtime` FROM `movie` WHERE `movie_name` = '서스펜스') MINUTE), (SELECT `movie_id` FROM `movie` WHERE `movie_name` = '서스펜스'), 'B'); 
        
      
        
        -- 좌석 테이블 생성
		CREATE TABLE `seat` (
			`seat_id` INT NOT NULL AUTO_INCREMENT,
			`theater_id` VARCHAR(50) NOT NULL,
			`row` INT NULL,
			`column` INT NULL,
			`seat_availability`	tinyint(1)	NOT NULL,
			PRIMARY KEY (`seat_id`),
			FOREIGN KEY (`theater_id`) REFERENCES `theater`(`theater_id`)
		);
        
        INSERT INTO `seat` (`theater_id`, `row`, `column`, `seat_availability`)
		VALUES
		('A', 1, 1, 1), ('A', 1, 2, 1), ('A', 1, 3, 1), ('A', 1, 4, 1), ('A', 1, 5, 0),
		('A', 2, 1, 0), ('A', 2, 2, 0), ('A', 2, 3, 1), ('A', 2, 4, 0), ('A', 2, 5, 0),
        
		('B', 1, 1, 0), ('B', 1, 2, 1), ('B', 1, 3, 1), ('B', 1, 4, 1), ('B', 1, 5, 1),
		('B', 2, 1, 1), ('B', 2, 2, 1), ('B', 2, 3, 1), ('B', 2, 4, 0), ('B', 2, 5, 0),
        
		('C', 1, 1, 1), ('C', 1, 2, 1), ('C', 1, 3, 1), ('C', 1, 4, 1), ('C', 1, 5, 0),
		('C', 2, 1, 0), ('C', 2, 2, 0), ('C', 2, 3, 0), ('C', 2, 4, 1), ('C', 2, 5, 0),
        
        ('D', 1, 1, 0), ('D', 1, 2, 1), ('D', 1, 3, 1), ('D', 1, 4, 0), ('D', 1, 5, 0),
		('D', 2, 1, 0), ('D', 2, 2, 0), ('D', 2, 3, 1), ('D', 2, 4, 0), ('D', 2, 5, 0),
        
        ('E', 1, 1, 0), ('E', 1, 2, 0), ('E', 1, 3, 0), ('E', 1, 4, 0), ('E', 1, 5, 0),
		('E', 2, 1, 0), ('E', 2, 2, 0), ('E', 2, 3, 1), ('E', 2, 4, 0), ('E', 2, 5, 0),
        
        ('F', 1, 1, 0), ('F', 1, 2, 0), ('F', 1, 3, 0), ('F', 1, 4, 0), ('F', 1, 5, 1),
		('F', 2, 1, 1), ('F', 2, 2, 1), ('F', 2, 3, 0), ('F', 2, 4, 0), ('F', 2, 5, 0),
        
        ('G', 1, 1, 0), ('G', 1, 2, 0), ('G', 1, 3, 0), ('G', 1, 4, 0), ('G', 1, 5, 0),
		('G', 2, 1, 0), ('G', 2, 2, 0), ('G', 2, 3, 0), ('G', 2, 4, 0), ('G', 2, 5, 0);
        
        -- member 테이블 insert
        INSERT INTO `member` (`member_id`, `name`, `phonenumber`, `email`, `role`)
	    VALUES
	   (1, '김철수', '01012345671', 'chulsoo@example.com', 'USER'),
	   (2, '이영희', '01012345672', 'younghee@example.com', 'USER'),
	   (3, '박민수', '01012345673', 'minsoo@example.com', 'USER'),
	   (4, '최지은', '01012345674', 'jieun@example.com', 'USER'),
	   (5, '정현우', '01012345675', 'hyunwoo@example.com', 'USER'),
	   (6, '문수진', '01012345676', 'sujin@example.com', 'USER'),
	   (7, '장준혁', '01012345677', 'junhyuk@example.com', 'USER'),
	   (8, '서하나', '01012345678', 'hana@example.com', 'USER'),
	   (9, '윤세준', '01012345679', 'sejun@example.com', 'USER'),
	   (10, '한지민', '01012345670', 'jimin@example.com', 'USER'),
	   (11, '백은우', '01012345681', 'eunwoo@example.com', 'USER'),
	   (12, '황지영', '01012345682', 'jieyong@example.com', 'USER');
        
        
        
        -- 예매 테이블 생성
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

		INSERT INTO `reservation` (`member_id`, `payment_method`, `payment_amount`, `payment_date`, `payment_status`)
		VALUES
		(1, '현금', 10000, '2024-01-15 09:00:00', 'CONFIRMED'),
		(2, '카드 결제', 12000, '2024-01-18 10:00:00', 'CONFIRMED'),
		(3, '휴대폰 결제', 15000, '2024-02-03 11:00:00', 'PENDING'),
		(4, '현금', 8000, '2024-03-04 12:00:00', 'CONFIRMED'),
		(5, '적립금 결제', 8500, '2024-04-05 13:00:00', 'PENDING'),
		(6, '카드 결제', 12500, '2024-01-06 14:00:00', 'CONFIRMED'),
		(7, '현금', 15000, '2024-01-07 15:00:00', 'CONFIRMED'),
		(8, '휴대폰 결제', 9000, '2024-02-08 16:00:00', 'PENDING'),
		(9, '현금', 8400, '2024-03-09 17:00:00', 'CONFIRMED'),
		(10, '적립금 결제', 14500, '2024-04-10 18:00:00', 'CONFIRMED'),
		(11, '카드 결제', 9900, '2024-01-11 19:00:00', 'PENDING'),
		(12, '현금', 13500, '2024-01-12 20:00:00', 'CONFIRMED'),
        
        (1, '휴대폰 결제', 15000, '2024-01-14 09:00:00', 'CONFIRMED'),
		(2, '카드 결제', 12000, '2024-01-17 10:00:00', 'CONFIRMED'),
		(3, '적립금 결제', 14000, '2024-03-28 11:00:00', 'CONFIRMED'),
		(4, '현금', 14000, '2024-03-04 12:00:00', 'CONFIRMED'),
		(5, '적립금 결제', 17000, '2024-02-05 13:00:00', 'PENDING'),
		(6, '휴대폰 결제', 16000, '2024-01-28 14:00:00', 'CONFIRMED'),
		(7, '카드 결제', 12000, '2024-01-10 15:00:00', 'CONFIRMED'),
        (8, '휴대폰 결제', 13200, '2024-04-08 16:00:00', 'PENDING'),
		(9, '카드 결제', 13000, '2024-03-09 17:00:00', 'CONFIRMED'),
		(10, '휴대폰 결제', 14000, '2024-02-10 18:00:00', 'PENDING'),
        (11, '휴대폰 결제', 13800, '2024-01-11 19:00:00', 'CONFIRMED'),
		(12, '현금', 15600, '2024-01-12 20:00:00', 'PENDING');
        
        -- 예매 테이블 생성
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
			FOREIGN KEY (`reservation_id`) REFERENCES `reservation`(`reservation_id`) ON DELETE CASCADE,
			FOREIGN KEY (`schedule_id`) REFERENCES `schedule`(`schedule_id`) ON DELETE CASCADE,
			FOREIGN KEY (`seat_id`) REFERENCES `seat`(`seat_id`),
			FOREIGN KEY (`theater_id`) REFERENCES `theater`(`theater_id`)
		);
        
		INSERT INTO `ticket` (`reservation_id`, `ticket_availibility`, `standard_price`, `sale_price`, `schedule_id`, `seat_id`, `theater_id`)
		VALUES
		(1, 1, 15000, 10000, 1, 1, 'A'),
		(2, 1, 15000, 12000, 2, 12, 'B'),
		(3, 0, 15000, 15000, 3, 23, 'C'),
		(4, 1, 15000, 8000, 4, 8, 'A'),
		(5, 0, 15000, 8500, 5, 18, 'B'),
		(6, 1, 15000, 12500, 6, 32, 'D'),
		(7, 1, 15000, 15000, 7, 55, 'F'),
		(8, 0, 15000, 9000, 8, 24, 'C'),
		(9, 1, 15000, 8400, 9, 48, 'E'),
		(10, 1, 15000, 14500, 10, 17, 'B'),
		(11, 0, 15000, 9000, 11, 29, 'C'),
		(12, 1, 15000, 13500, 12, 13, 'B'),
        
        (13, 1, 15000, 15000, 24, 15, 'B'), 
		(14, 1, 15000, 12000, 23, 22, 'C'),
		(15, 1, 15000, 14000, 22, 14, 'B'), 
		(16, 1, 15000, 14000, 21, 4, 'A'), 
		(17, 0, 17000, 17000, 20, 57, 'F'), 
		(18, 1, 16000, 16000, 19, 16, 'B'), 
		(19, 1, 15000, 12000, 18, 2, 'A'), 
		(20, 0, 15000, 13200, 17, 33, 'D'), 
		(21, 1, 15000, 13000, 16, 3, 'A'), 
		(22, 0, 15000, 14000, 15, 21, 'C'), 
		(23, 1, 15000, 13000, 14, 38, 'D'), 
		(24, 0, 15000, 15600, 13, 56, 'F'); 

    END IF;
END//
DELIMITER ;

-- 절차 호출
CALL InitializeDatabase();
