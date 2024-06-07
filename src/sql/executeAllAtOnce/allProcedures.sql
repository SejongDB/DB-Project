-- 1. 관리자 모든 테이블 CRUD procedure
USE db2;

DROP PROCEDURE IF EXISTS CreateMember;
DELIMITER //
CREATE PROCEDURE CreateMember(
    IN input_admin_name VARCHAR(255), 
    IN input_user_name VARCHAR(255), 
    IN input_user_phonenumber VARCHAR(255),
    IN input_user_email VARCHAR(255),
    OUT output_created_user_id INT
)
BEGIN
    IF input_admin_name = 'root' THEN -- 관리자만 해당 쿼리를 실행할 수 있도록
        INSERT INTO `member` (`name`, `phonenumber`, `email`, `role`)
        VALUES (input_user_name, input_user_phonenumber, input_user_email, 'USER');
        SET output_created_user_id = LAST_INSERT_ID();
    ELSE
        SET output_created_user_id = NULL;
        SELECT '관리자만 새로운 사용자 정보를 추가할 수 있습니다.' AS Message;
    END IF;
END //
DELIMITER ;

DROP PROCEDURE IF EXISTS DeleteMember;
DELIMITER //
CREATE PROCEDURE DeleteMember(
    IN target_member_id INT
)
BEGIN
    DELETE FROM ticket WHERE reservation_id IN (SELECT reservation_id FROM reservation WHERE member_id = target_member_id);
    DELETE FROM reservation WHERE member_id = target_member_id;
    DELETE FROM member WHERE member_id = target_member_id;
END //
DELIMITER ;

DROP PROCEDURE IF EXISTS UpdateMember;
DELIMITER //
CREATE PROCEDURE UpdateMember(
    IN input_admin_name VARCHAR(255),
    IN target_member_id INT,
    IN input_new_user_name VARCHAR(255), 
    IN input_new_user_phonenumber VARCHAR(255),
    IN input_new_user_email VARCHAR(255)
)
BEGIN
    IF input_admin_name = 'root' THEN 
        UPDATE member
        SET name = input_new_user_name, 
            phonenumber = input_new_user_phonenumber, 
            email = input_new_user_email
        WHERE member_id = target_member_id;
    ELSE
        SELECT '관리자만 새로운 사용자 정보를 추가할 수 있습니다.' AS Message;
    END IF;
END //
DELIMITER ;

DROP PROCEDURE IF EXISTS CreateMovie;
DELIMITER //
CREATE PROCEDURE CreateMovie(
    IN input_admin_name VARCHAR(255), 
    IN input_movie_name VARCHAR(255), 
    IN input_showtime INT,
    IN input_rating VARCHAR(255),
	IN input_director VARCHAR(255) ,
	IN input_actor VARCHAR(255) ,
	IN input_genre VARCHAR(255) ,
	IN input_instruction VARCHAR(512) ,
	IN input_movie_created_at DATETIME ,
	IN input_grade FLOAT,
    OUT output_created_movie_id INT
)
BEGIN
    IF input_admin_name = 'root' THEN 
        INSERT INTO `movie` (`movie_name`, `showtime`, `rating`, `director`, `actor`, `genre`, `instruction`, `movie_created_at`, `grade`)
        VALUES
        (input_movie_name, input_showtime, input_rating, input_director, input_actor, input_genre, input_instruction, input_movie_created_at, input_grade);
        SET output_created_movie_id = LAST_INSERT_ID();
    ELSE
        SET output_created_movie_id = NULL;
        SELECT '관리자만 새로운 사용자 영화를 추가할 수 있습니다.' AS Message;
    END IF;
END //
DELIMITER ;

DROP PROCEDURE IF EXISTS DeleteMovie;
DELIMITER //
CREATE PROCEDURE DeleteMovie(IN member_id INT, IN selected_movie_id INT)
BEGIN
    DECLARE admin_role VARCHAR(255);
    SELECT role INTO admin_role 
    FROM member 
    WHERE member_id = member_id
    LIMIT 1;
    IF admin_role = 'ADMIN' THEN
        IF EXISTS (SELECT * FROM schedule WHERE movie_id = selected_movie_id) THEN
            SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = '해당 영화에 스케줄이 있어 삭제할 수 없습니다.';
        ELSE
            DELETE FROM movie WHERE movie_id = selected_movie_id;
        END IF;
    ELSE
        SIGNAL SQLSTATE '45000' 
        SET MESSAGE_TEXT = '권한이 없습니다. ADMIN 역할만 해당 작업을 수행할 수 있습니다.';
    END IF;
END//
DELIMITER ;

DROP PROCEDURE IF EXISTS UpdateMovie;
DELIMITER //
CREATE PROCEDURE UpdateMovie(
    IN input_admin_name VARCHAR(255), 
    IN input_movie_id INT,
    IN input_movie_name VARCHAR(255), 
    IN input_showtime INT,
    IN input_rating VARCHAR(255),
	IN input_director VARCHAR(255),
	IN input_actor VARCHAR(255),
	IN input_genre VARCHAR(255),
	IN input_instruction VARCHAR(512),
	IN input_movie_created_at DATETIME,
	IN input_grade FLOAT
)
BEGIN
    IF input_admin_name = 'root' THEN 
        IF (SELECT rating FROM movie WHERE movie_id = input_movie_id) = 'A' THEN 
            UPDATE `movie`
            SET 
                movie_name = COALESCE(input_movie_name, movie_name),
                showtime = COALESCE(input_showtime, showtime),
                director = COALESCE(input_director, director),
                actor = COALESCE(input_actor, actor),
                genre = COALESCE(input_genre, genre),
                instruction = COALESCE(input_instruction, instruction),
                movie_created_at = COALESCE(input_movie_created_at, movie_created_at),
                grade = COALESCE(input_grade, grade)
            WHERE movie_id = input_movie_id;
        ELSEIF (SELECT grade FROM movie WHERE movie_id = input_movie_id) <= 7 THEN 
            UPDATE `movie`
            SET 
                movie_name = COALESCE(input_movie_name, movie_name),
                showtime = COALESCE(input_showtime, showtime),
                rating = COALESCE(input_rating, rating),
                director = COALESCE(input_director, director),
                actor = COALESCE(input_actor, actor),
                genre = COALESCE(input_genre, genre),
                instruction = COALESCE(input_instruction, instruction),
                movie_created_at = COALESCE(input_movie_created_at, movie_created_at),
                grade = COALESCE(input_grade, grade)
            WHERE movie_id = input_movie_id;
        ELSE
            SELECT '영화 정보는 상영등급이 A일 때와 평점이 7 이하일 경우에만 수정 가능합니다.' AS Message;
        END IF;
    ELSE
        SELECT '관리자만 영화를 수정할 수 있습니다.' AS Message;
    END IF;
END //
DELIMITER ;

DROP PROCEDURE IF EXISTS CreateReservation;
DELIMITER //
CREATE PROCEDURE CreateReservation(
    IN input_admin_name VARCHAR(255), 
	IN input_payment_method	varchar(255),
	IN input_payment_amount	int4,
	IN input_payment_status	varchar(255),
	IN input_payment_date	datetime ,
	IN input_member_id INT,
    OUT output_created_reservation_id INT
)
BEGIN
    IF input_admin_name = 'root' THEN 
        INSERT INTO `reservation` (`member_id`, `payment_method`, `payment_amount`, `payment_date`, `payment_status`)
		VALUES
		(input_member_id, input_payment_method, input_payment_amount, input_payment_date, input_payment_status);
        SET output_created_reservation_id = LAST_INSERT_ID();
        
    ELSE
        SET output_created_reservation_id = NULL;
        SELECT '관리자만 유저의 예매 생성을 관리할 수 있습니다.' AS Message;
    END IF;
END //
DELIMITER ;

DROP PROCEDURE IF EXISTS DeleteReservation;
DELIMITER //
CREATE PROCEDURE DeleteReservation(
    IN input_admin_name VARCHAR(255), 
    IN input_reservation_id INT
)
BEGIN
    IF input_admin_name = 'root' THEN
        IF EXISTS (SELECT 1 FROM reservation WHERE reservation_id = input_reservation_id) THEN
            DELETE FROM ticket WHERE reservation_id = input_reservation_id;
            DELETE FROM reservation WHERE reservation_id = input_reservation_id;
        ELSE
            SELECT '삭제하려는 예약이 존재하지 않습니다.' AS Message;
        END IF;
    ELSE
        SELECT '관리자만 예약을 삭제할 수 있습니다.' AS Message;
    END IF;
END //
DELIMITER ;

DROP PROCEDURE IF EXISTS UpdateReservation;
DELIMITER //
CREATE PROCEDURE UpdateReservation(
    IN input_admin_name VARCHAR(255), 
    IN input_reservation_id INT,
    IN input_payment_method VARCHAR(255),
    IN input_payment_amount INT,
    IN input_payment_status VARCHAR(255),
    IN input_member_id INT
)
BEGIN
    IF input_admin_name = 'root' THEN 
        IF (SELECT payment_status FROM reservation WHERE reservation_id = input_reservation_id) = 'PENDING' THEN 
            UPDATE `reservation`
            SET 
                payment_method = COALESCE(input_payment_method, payment_method), 
                payment_amount = COALESCE(input_payment_amount, payment_amount),
                payment_status = COALESCE(input_payment_status, payment_status),
                payment_date = now(), 
                member_id = COALESCE(input_member_id, member_id)
            WHERE reservation_id = input_reservation_id;
        ELSE
            SELECT '지불 대기 상태인 예약 내역만 갱신할 수 있습니다.' AS Message;
        END IF;
    ELSE
        SELECT '관리자만 예매를 수정할 수 있습니다.' AS Message;
    END IF;
END //
DELIMITER ;

DROP PROCEDURE IF EXISTS CreateSchedule;
DELIMITER //
CREATE PROCEDURE CreateSchedule(
    IN input_admin_name VARCHAR(255), 
	IN input_created_at DATETIME,
	IN input_screening_day VARCHAR(255),
	IN input_screening_count INT,
	IN input_start_time DATETIME,
	IN input_movie_id INT,
	IN input_theater_id VARCHAR(50),
    OUT output_created_schedule_id INT
)
BEGIN
    IF input_admin_name = 'root' THEN 
        INSERT INTO `schedule` (`created_at`, `screening_day`, `screening_count`, `start_time`, `end_time`, `movie_id`, `theater_id`)
        VALUES
        (input_created_at, input_screening_day, input_screening_count, input_start_time, DATE_ADD(input_start_time, INTERVAL (SELECT `showtime` FROM `movie` WHERE `movie_id` = input_movie_id) MINUTE), input_movie_id, input_theater_id);
        
        SET output_created_schedule_id = LAST_INSERT_ID();
        
    ELSE
        SET output_created_schedule_id = NULL;
        SELECT '관리자만 새로운 상영일정을 추가할 수 있습니다.' AS Message;
    END IF;
END //
DELIMITER ;

DROP PROCEDURE IF EXISTS deleteSchedule;
DELIMITER //
CREATE PROCEDURE deleteSchedule(
    IN input_admin_name VARCHAR(255),
    IN input_schedule_id INT
)
BEGIN
    IF input_admin_name = 'root' THEN
        DELETE FROM reservation
        WHERE reservation_id IN (
            SELECT t.reservation_id
            FROM ticket t
            WHERE t.schedule_id = input_schedule_id
        );
        DELETE FROM seat
        WHERE seat_id IN (
            SELECT t.seat_id
            FROM ticket t
            WHERE t.schedule_id = input_schedule_id
        );
        DELETE FROM ticket
        WHERE schedule_id = input_schedule_id;
        DELETE FROM schedule
        WHERE schedule_id = input_schedule_id;
        
        SELECT '상영일정 및 관련된 티켓, 좌석, 예매 정보가 성공적으로 삭제되었습니다.' AS Message;
    ELSE
        SELECT '관리자만 상영일정을 삭제할 수 있습니다.' AS Message;
    END IF;
END //
DELIMITER ;

DROP PROCEDURE IF EXISTS UpdateSchedule;
DELIMITER //
CREATE PROCEDURE UpdateSchedule(
    IN input_admin_name VARCHAR(255),
    IN input_schedule_id INT,
    IN input_screening_day VARCHAR(255),
    IN input_screening_count INT,
    IN input_start_time DATETIME
)
BEGIN
    DECLARE temporary_time DATETIME DEFAULT '2022-12-31 00:00:00';
    DECLARE movie_showtime INT;

    IF input_admin_name = 'root' THEN
        IF (SELECT start_time FROM schedule WHERE schedule_id = input_schedule_id) < temporary_time THEN
            SELECT '특정 시점(2022-12-31) 이후에 상영을 시작하는 스케쥴만 수정할 수 있습니다.' AS Message;
        ELSE
            SET movie_showtime = (SELECT `showtime` FROM `movie` WHERE `movie_id` = (SELECT movie_id FROM schedule WHERE schedule_id = input_schedule_id));
            UPDATE `schedule`
            SET `screening_day` = input_screening_day,
                `screening_count` = input_screening_count,
                `start_time` = input_start_time,
                `end_time` = DATE_ADD(input_start_time, INTERVAL movie_showtime MINUTE)
            WHERE `schedule_id` = input_schedule_id;
            
            SELECT '상영일정이 성공적으로 업데이트되었습니다.' AS Message;
        END IF;
    ELSE
        SELECT '관리자만 상영일정을 수정할 수 있습니다.' AS Message;
    END IF;
END //
DELIMITER ;

DROP PROCEDURE IF EXISTS CreateSeat;
DELIMITER //
CREATE PROCEDURE CreateSeat(
    IN input_admin_name VARCHAR(255), 
	IN input_theater_id VARCHAR(255),
	IN input_row INT,
	IN input_column INT,
	IN input_seat_availability	tinyint(1),
    OUT output_created_seat_id INT
)
BEGIN
    IF input_admin_name = 'root' THEN 
        INSERT INTO `seat` (`theater_id`, `row`, `column`, `seat_availability`)
		VALUES
		(input_theater_id, input_row, input_column, input_seat_availability);
        SET output_created_seat_id = LAST_INSERT_ID();
    ELSE
        SET output_created_seat_id = NULL;
        SELECT '관리자만 새로운 좌석를 추가할 수 있습니다.' AS Message;
    END IF;
END //
DELIMITER ;

DROP PROCEDURE IF EXISTS DeleteSeat;
DELIMITER //
CREATE PROCEDURE DeleteSeat(
    IN input_admin_name VARCHAR(255),
    IN input_seat_id INT
)
BEGIN
    DECLARE seat_availability_value TINYINT(1);
    DECLARE seat_ticket_connected_id INT;
    IF input_admin_name = 'root' THEN
        SELECT seat_availability INTO seat_availability_value FROM seat WHERE seat_id = input_seat_id;
        IF seat_availability_value = 1 THEN
            SELECT '회원에 의해 이미 예매된 좌석은 삭제할 수 없습니다.' AS Message;
        ELSE
            SELECT ticket_id INTO seat_ticket_connected_id FROM ticket WHERE seat_id = input_seat_id;
            DELETE FROM ticket WHERE seat_id = input_seat_id;
            DELETE FROM reservation WHERE reservation_id IN (SELECT reservation_id FROM ticket WHERE ticket_id = seat_ticket_connected_id);
            DELETE FROM seat WHERE seat_id = input_seat_id;
            SELECT '좌석 및 좌석에 따른 티켓, 예약이 성공적으로 삭제되었습니다.' AS Message;
        END IF;
    ELSE
        SELECT '관리자만 좌석 정보를 삭제할 수 있습니다.' AS Message;
    END IF;
END //
DELIMITER ;

DROP PROCEDURE IF EXISTS UpdateSeat;
DELIMITER //
CREATE PROCEDURE UpdateSeat(
    IN input_admin_name VARCHAR(255),
    IN input_seat_id INT,
    IN input_row INT,
    IN input_column INT,
    IN input_seat_availability TINYINT(1)
)
BEGIN
    DECLARE existing_theater_id VARCHAR(50);
    IF input_admin_name = 'root' THEN 
        SELECT theater_id INTO existing_theater_id FROM seat WHERE seat_id = input_seat_id;
        IF (SELECT seat_availability FROM seat WHERE seat_id = input_seat_id) = 1 THEN
            SELECT '회원에 의해 이미 발권된 티켓의 좌석 정보는 수정할 수 없습니다.' AS Message;
        ELSEIF EXISTS (
            SELECT 1 
            FROM seat 
            WHERE theater_id = existing_theater_id 
            AND `row` = input_row 
            AND `column` = input_column 
            AND seat_id != input_seat_id
        ) THEN
            SELECT '이미 다른 회원이 지정한 좌석입니다.' AS Message;
        ELSE
            UPDATE `seat`
            SET 
                `row` = COALESCE(input_row, `row`),
                `column` = COALESCE(input_column, `column`),
                seat_availability = COALESCE(input_seat_availability, seat_availability)
            WHERE seat_id = input_seat_id;
            SELECT '회원의 좌석 정보가 성공적으로 업데이트되었습니다.' AS Message;
        END IF;
    ELSE
        SELECT '관리자만 좌석 정보를 수정할 수 있습니다.' AS Message;
    END IF;
END //
DELIMITER ;

DROP PROCEDURE IF EXISTS CreateTheater;
DELIMITER //
CREATE PROCEDURE CreateTheater(
    IN input_admin_name VARCHAR(255), 
    IN input_theater_id VARCHAR(50),
	IN input_theater_availability TINYINT(1),
	IN input_row_seat INT,
	IN input_column_seat INT,
	IN input_total_seat INT
)
BEGIN
    IF input_admin_name = 'root' THEN 
        INSERT INTO `theater` (`theater_id`, `theater_availability`, `row_seat`, `column_seat`, `total_seat`)
		VALUES
		(input_theater_id, input_theater_availability, input_row_seat, input_column_seat, input_total_seat);
    ELSE
        SELECT '관리자만 새로운 상영관을 추가할 수 있습니다.' AS Message;
    END IF;
END //
DELIMITER ;

DROP PROCEDURE IF EXISTS DeleteTheater;
DELIMITER //
CREATE PROCEDURE DeleteTheater(
    IN input_admin_name VARCHAR(255), 
    IN input_theater_id VARCHAR(50)
)
BEGIN
    IF input_admin_name = 'root' THEN
        IF EXISTS (SELECT 1 FROM schedule WHERE theater_id = input_theater_id) THEN
            SELECT '상영관에 관련된 스케줄이 있어 삭제할 수 없습니다.' AS Message;
        ELSEIF EXISTS (SELECT 1 FROM ticket WHERE theater_id = input_theater_id) THEN
            SELECT '상영관에 관련된 티켓이 있어 삭제할 수 없습니다.' AS Message;
        ELSEIF EXISTS (SELECT 1 FROM seat WHERE theater_id = input_theater_id) THEN
            SELECT '상영관에 관련된 좌석이 있어 삭제할 수 없습니다.' AS Message;
        ELSE
            DELETE FROM theater WHERE theater_id = input_theater_id;
            SELECT '상영관이 성공적으로 삭제되었습니다.' AS Message;
        END IF;
    ELSE
        SELECT '관리자만 상영관을 삭제할 수 있습니다.' AS Message;
    END IF;
END //
DELIMITER ;

DROP PROCEDURE IF EXISTS UpdateTheaterByMovie;
DELIMITER //
CREATE PROCEDURE UpdateTheaterByMovie(IN member_id INT, IN selected_movie_id INT)
BEGIN
    DECLARE admin_role VARCHAR(255);
    SELECT role INTO admin_role 
    FROM member 
    WHERE member_id = member_id
    LIMIT 1;
    IF admin_role = 'ADMIN' THEN
        UPDATE schedule s
        JOIN movie m 
        SET s.theater_id = 'G'
        WHERE m.movie_id = s.movie_id and m.movie_id = selected_movie_id;
    ELSE
        SIGNAL SQLSTATE '45000' 
        SET MESSAGE_TEXT = '권한이 없습니다. ADMIN 역할만 해당 작업을 수행할 수 있습니다.';
    END IF;
END//
DELIMITER ;

DROP PROCEDURE IF EXISTS CreateTicket;
DELIMITER //
CREATE PROCEDURE CreateTicket(
    IN input_admin_name VARCHAR(255), 
	IN input_standard_price	int4,
	IN input_seat_id INT, 
	IN input_reservation_id INT,
	IN input_schedule_id INT,
    OUT output_created_ticket_id INT
)
BEGIN
    IF input_admin_name = 'root' THEN 
        INSERT INTO `ticket` (`reservation_id`, `ticket_availibility`, `standard_price`, `sale_price`, `schedule_id`, `seat_id`, `theater_id`)
		VALUES
		(input_reservation_id, 
        1, 
        input_standard_price, 
        (select r.payment_amount from reservation r where r.reservation_id = input_reservation_id), 
        input_schedule_id, 
        input_seat_id, 
        (select s.theater_id from schedule s where s.schedule_id = input_schedule_id));
        
        SET output_created_ticket_id = LAST_INSERT_ID();
        
    ELSE
        SET output_created_ticket_id = NULL;
        SELECT '관리자만 새로운 티켓를 발권할 수 있습니다.' AS Message;
    END IF;
END //
DELIMITER ;

DROP PROCEDURE IF EXISTS deleteTicket;
DELIMITER //
CREATE PROCEDURE deleteTicket(
    IN input_admin_name VARCHAR(255), 
    IN input_ticket_id INT,
    OUT output_deleted_ticket_id INT
)
BEGIN
    DECLARE reservation_id INT;
    DECLARE seat_id INT;
    IF input_admin_name = 'root' THEN 
        SELECT reservation_id, seat_id INTO reservation_id, seat_id 
        FROM ticket 
        WHERE ticket_id = input_ticket_id;
        DELETE FROM ticket WHERE ticket_id = input_ticket_id;
        DELETE FROM reservation WHERE reservation_id = reservation_id;
        DELETE FROM seat WHERE seat_id = seat_id;
        SET output_deleted_ticket_id = input_ticket_id;
    ELSE
        SET output_deleted_ticket_id = NULL;
        SELECT '관리자만 티켓을 삭제할 수 있습니다.' AS Message;
    END IF;

END //
DELIMITER ;

DROP PROCEDURE IF EXISTS updateTicket;
DELIMITER //
CREATE PROCEDURE updateTicket(
    IN input_admin_name VARCHAR(255), 
    IN input_ticket_id INT,
    OUT output_updated_ticket_id INT
)
BEGIN
    DECLARE payment_amount INT;
    IF input_admin_name = 'root' THEN 
        IF ((SELECT ticket_availibility FROM ticket WHERE ticket_id = input_ticket_id) = 1) THEN 
            UPDATE `ticket`
            SET `ticket_availibility` = 0 
            WHERE `ticket_id` = input_ticket_id;
        ELSE 
            UPDATE `ticket`
            SET `ticket_availibility` = 1
            WHERE `ticket_id` = input_ticket_id;
        END IF;
        SET output_updated_ticket_id = input_ticket_id;
    ELSE
        SET output_updated_ticket_id = NULL;
        SELECT '관리자만 티켓을 수정할 수 있습니다.' AS Message;
    END IF;
END //
DELIMITER ;

drop procedure if exists ExecuteAdminAllTablesReadQuery;
DELIMITER //
CREATE PROCEDURE ExecuteAdminAllTablesReadQuery (IN user_id INT)
BEGIN
    DECLARE user_role VARCHAR(255);
    SELECT role INTO user_role FROM member WHERE member_id = user_id LIMIT 1;
    IF user_role = 'ADMIN' THEN
        SELECT 
			m.movie_id as 영화번호,
            m.movie_name as 영화명,
			m.showtime as 상영시간,
			m.rating as 상영등급,
			m.director as 감독명,
			m.actor as 배우명,
			m.genre as 장르,
			m.instruction as 영화소개,
			m.movie_created_at as 개봉일자,
			m.grade as 평점정보
        FROM movie m;
        
        SELECT 
			m.member_id AS 회원아이디,
			m.name AS 고객명,
			m.phonenumber AS 휴대폰번호,
			m.email AS 전자메일주소,
			m.role AS 역할
		FROM member m;

        SELECT 
			r.reservation_id AS 예매번호,
			r.payment_method AS 결제방법,
			r.payment_amount AS 결제금액,
			r.payment_status AS 결제상태,
			r.payment_date AS 결제일자,
			r.member_id AS 회원아이디
		FROM reservation r;
        
        SELECT 
			s.schedule_id AS 상영일정번호,
			s.created_at AS 상영시작일,
			s.screening_day AS 상영요일,
			s.screening_count AS 상영회차,
			s.start_time AS 상영시작시간,
			s.end_time AS 상영종료시간,
			s.movie_id AS 영화번호,
			s.theater_id AS 상영관번호
		FROM schedule s;

        SELECT 
			s.seat_id AS 좌석번호,
			s.theater_id AS 상영관번호,
			s.row AS 행위치,
			s.column AS 열위치,
			s.seat_availability AS 좌석사용여부
		FROM seat s;
        
        SELECT 
			t.theater_id AS 상영관번호,
			t.theater_availability AS 상영관서용여부,
			t.row_seat AS 가로좌석수,
			t.column_seat AS 세로좌석수,
			t.total_seat AS 전체좌석수
		FROM theater t;

        SELECT 
			t.ticket_id AS 티켓번호,
			t.ticket_availibility AS 발권여부,
			t.standard_price AS 표준가격,
			t.sale_price AS 판매가격,
			t.seat_id AS 좌석번호,
			t.reservation_id AS 예매번호,
			t.schedule_id AS 상영일정번호,
			t.theater_id AS 상영관번호
		FROM ticket t;
    ELSE
    	SELECT '관리자만 해당 쿼리를 실행할 수 있습니다.' AS message;
    END IF;
END//
DELIMITER ;









-- 2. 회원 기능 구현을 위한 procedure
DROP PROCEDURE IF EXISTS CheckUserRole;
DELIMITER //
CREATE PROCEDURE CheckUserRole(
    IN input_member_id INT,
    OUT is_user_role BOOLEAN
)
BEGIN
    DECLARE user_role VARCHAR(255);
    SELECT role INTO user_role
    FROM member
    WHERE member_id = input_member_id;
    IF user_role = 'USER' THEN
        SET is_user_role = TRUE;
    ELSE
        SET is_user_role = FALSE;
    END IF;
END //
DELIMITER ;

DROP PROCEDURE IF EXISTS deleteUserReservation;
DELIMITER //
CREATE PROCEDURE deleteUserReservation(
    IN input_member_id INT,
    IN input_reservation_id INT 
)
BEGIN
    DECLARE seat_id INT;
	SELECT t.seat_id INTO seat_id
    FROM ticket t
    WHERE t.reservation_id = input_reservation_id;
    UPDATE seat
    SET seat_availability = 0
    WHERE seat_id = seat_id;
    DELETE FROM ticket WHERE reservation_id = input_reservation_id;
    DELETE FROM reservation WHERE member_id = input_member_id AND reservation_id = input_reservation_id;
END //	
DELIMITER ;

DROP PROCEDURE IF EXISTS makeUserReservation;
DELIMITER //
CREATE PROCEDURE makeUserReservation(
    IN input_member_id INT,  
    IN input_schedule_id INT, 
    IN input_seat_id INT, 
    OUT output_reservation_id INT,
    OUT output_ticket_id INT
)
BEGIN
    DECLARE schedule_theater_id VARCHAR(50);
    DECLARE seat_availability TINYINT;
    DECLARE seat_theater_id VARCHAR(50);
    SELECT seat_availability INTO seat_availability
    FROM seat
    WHERE seat_id = input_seat_id;
    IF seat_availability = 1 THEN
        SELECT '선택한 좌석은 이미 예매되었습니다.' AS Message;
    END IF;
    SELECT theater_id INTO schedule_theater_id
    FROM schedule s
    WHERE s.schedule_id = input_schedule_id LIMIT 1;
    SELECT theater_id INTO seat_theater_id
    FROM seat s
    WHERE s.seat_id = input_seat_id LIMIT 1;
    IF schedule_theater_id != seat_theater_id THEN
        SELECT '선택한 영화는 해당 상영관에서 상영되지 않습니다.' AS Message;
    END IF;
    INSERT INTO reservation (payment_method, payment_amount, payment_status, payment_date, member_id)
    VALUES ('카드 결제', 0, 'PENDING', NOW(), input_member_id);
    SET output_reservation_id = LAST_INSERT_ID();
    INSERT INTO ticket (ticket_availibility, standard_price, sale_price, seat_id, reservation_id, schedule_id, theater_id)
    VALUES (1, 10000, 10000, input_seat_id, output_reservation_id, input_schedule_id, seat_theater_id);
    
    SET output_ticket_id = LAST_INSERT_ID();
    update seat s
    set s.seat_availability = 1
    where s.seat_id = input_seat_id;
END //
DELIMITER ;

DROP PROCEDURE IF EXISTS ViewReservations;
DELIMITER //
CREATE PROCEDURE ViewReservations(
    IN input_member_id INT
)
BEGIN
    DECLARE is_user_role BOOLEAN;
    CALL CheckUserRole(input_member_id, is_user_role);
    IF is_user_role THEN
        SELECT m.movie_name AS 영화명,
               s.created_at AS 상영일,
               s.theater_id AS 상영관번호,
               t.seat_id AS 좌석번호,
               t.sale_price AS 판매가격,
               t.ticket_id AS 티켓아이디
        FROM reservation r, ticket t, schedule s, movie m
        WHERE (r.member_id = input_member_id and r.reservation_id = t.reservation_id and 
        t.schedule_id = s.schedule_id and s.movie_id = m.movie_id);
    ELSE
        SELECT 'USER 회원 권한인 사용자만 예매 내역을 조회할 수 있습니다.' AS Message;
    END IF;
END //
DELIMITER ;

DROP PROCEDURE IF EXISTS ExecuteUserReadAllScheduleAndTicket;
DELIMITER //
CREATE PROCEDURE ExecuteUserReadAllScheduleAndTicket (IN user_id INT)
BEGIN
    DECLARE user_role VARCHAR(255);
    SELECT role INTO user_role FROM member WHERE member_id = user_id LIMIT 1;
    IF user_role = 'USER' THEN
        SELECT 
			s.schedule_id ,
			s.created_at ,
			s.screening_day,
			s.screening_count ,
			s.start_time ,
			s.end_time ,
			m.movie_name ,
			s.theater_id 
		FROM schedule s, movie m
        WHERE s.movie_id = m.movie_id;
    ELSE
        SELECT '관리자의 경우 관리자 계정으로 로그인해주세요.' AS message;
    END IF;
END//
DELIMITER ;

DROP PROCEDURE IF EXISTS ViewOneReservationDetails;
DELIMITER //
CREATE PROCEDURE ViewOneReservationDetails(
	IN input_member_id INT,
    IN input_ticket_id INT
)
BEGIN
    DECLARE is_user_role BOOLEAN;
    CALL CheckUserRole(input_member_id, is_user_role);
    IF is_user_role THEN
        SELECT ti.ticket_availibility AS 티켓발권여부, 
			   ti.standard_price AS 표준가격,
               ti.sale_price AS 판매가격,
               s.created_at AS 상영시작일,
               s.screening_day AS 상영요일,
               s.screening_count AS 상영회차,
               s.start_time AS 상영시작시간,
               s.end_time AS 상영종료시간, 
               th.theater_availability AS 상영관사용여부,
               th.row_seat AS 상영관가로좌석수,
               th.column_seat AS 상영관세로좌석수,
               th.total_seat AS 상영관전체좌석수,
               r.reservation_id AS 예매아이디
        FROM ticket ti, schedule s, theater th, reservation r
        WHERE (ti.ticket_id = input_ticket_id and ti.schedule_id = s.schedule_id 
        and s.theater_id = th.theater_id and ti.theater_id = th.theater_id and r.reservation_id = ti.reservation_id);
    ELSE
        SELECT 'USER 회원 권한인 사용자만 예매 내역을 조회할 수 있습니다.' AS Message;
    END IF;
END //
DELIMITER ;

DROP PROCEDURE IF EXISTS SearchMovies;
DELIMITER //
CREATE PROCEDURE SearchMovies( 
    IN input_member_id INT,
    IN movie_title VARCHAR(255),
    IN director_name VARCHAR(255),
    IN actor_name VARCHAR(255),
    IN genre_name VARCHAR(255)
)
BEGIN
    DECLARE is_user_role BOOLEAN;
    CALL CheckUserRole(input_member_id, is_user_role);
    IF is_user_role THEN
        SELECT m.movie_name AS 영화명, m.director AS 감독명, m.actor AS 배우명, m.genre AS 장르
        FROM movie m
        WHERE (movie_title IS NULL OR m.movie_name LIKE CONCAT('%', movie_title, '%'))
          AND (director_name IS NULL OR m.director LIKE CONCAT('%', director_name, '%'))
          AND (actor_name IS NULL OR m.actor LIKE CONCAT('%', actor_name, '%'))
          AND (genre_name IS NULL OR m.genre LIKE CONCAT('%', genre_name, '%'));
    ELSE
        SELECT 'USER 회원 권한인 사용자만 해당 쿼리를 실행할 수 있습니다.' AS Message;
    END IF;
END //
DELIMITER ;

DROP PROCEDURE IF EXISTS updateUserReservation;
DELIMITER //
CREATE PROCEDURE updateUserReservation(
    IN input_member_id INT,
    IN input_reservation_id INT, 
    IN input_schedule_id INT, 
    IN input_seat_id INT 
)
BEGIN
    DECLARE schedule_theater_id VARCHAR(50);
    DECLARE seat_theater_id VARCHAR(50);
    IF NOT EXISTS (SELECT * FROM reservation WHERE reservation_id = input_reservation_id AND member_id = input_member_id) THEN
        SELECT '예약을 찾을 수 없습니다.' AS Message;
    END IF;
    SELECT theater_id INTO schedule_theater_id
    FROM schedule
    WHERE schedule_id = input_schedule_id;
    SELECT theater_id INTO seat_theater_id
    FROM seat
    WHERE seat_id = input_seat_id;
    IF schedule_theater_id != seat_theater_id THEN
        SELECT '선택한 영화는 해당 상영관에서 상영되지 않습니다.' AS Message;
    END IF;
    UPDATE ticket
    SET schedule_id = input_schedule_id,
		theater_id = (select theater_id from schedule s where s.schedule_id = input_schedule_id),
        seat_id = input_seat_id
    WHERE reservation_id = input_reservation_id;
    UPDATE seat
    SET seat_availability = 1
    WHERE seat_id = input_seat_id;
    SELECT '예약이 성공적으로 업데이트되었습니다.' AS Message;
END //
DELIMITER ;

