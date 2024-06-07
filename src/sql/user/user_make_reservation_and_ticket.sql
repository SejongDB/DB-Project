USE db2;

DROP PROCEDURE IF EXISTS makeUserReservation;

DELIMITER //

CREATE PROCEDURE makeUserReservation(
    IN input_member_id INT, -- 로그인한 member_id = 2를 고정 
    IN input_schedule_id INT, -- 유저가 선택하고 싶은 상영 일정 id 넘기고
    IN input_seat_id INT, -- 해당 영화가 상영되는 상영관 아이디를 참고해서 해당 상영관 좌석 id를 넘기면 됨
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
    
    -- 선택한 좌석이 이미 예매된 경우 예외 처리
    IF seat_availability = 1 THEN
        SELECT '선택한 좌석은 이미 예매되었습니다.' AS Message;
    END IF;
    
    SELECT theater_id INTO schedule_theater_id
    FROM schedule s
    WHERE s.schedule_id = input_schedule_id LIMIT 1;
    
    SELECT theater_id INTO seat_theater_id
    FROM seat s
    WHERE s.seat_id = input_seat_id LIMIT 1;
    
    -- 상영 일정과 좌석이 다른 경우 예외 처리
    IF schedule_theater_id != seat_theater_id THEN
        SELECT '선택한 영화는 해당 상영관에서 상영되지 않습니다.' AS Message;
    END IF;
    
    -- reservation 테이블에 새로운 예약 추가
    INSERT INTO reservation (payment_method, payment_amount, payment_status, payment_date, member_id)
    VALUES ('카드 결제', 0, 'PENDING', NOW(), input_member_id);
    
    SET output_reservation_id = LAST_INSERT_ID();
    
    -- ticket 테이블에 새로운 티켓 추가
    INSERT INTO ticket (ticket_availibility, standard_price, sale_price, seat_id, reservation_id, schedule_id, theater_id)
    VALUES (1, 10000, 10000, input_seat_id, output_reservation_id, input_schedule_id, seat_theater_id);
    
    SET output_ticket_id = LAST_INSERT_ID();
    
    -- 티켓 발권 되었으므로 좌석 사용여부 1로 변경
    update seat s
    set s.seat_availability = 1
    where s.seat_id = input_seat_id;
END //

DELIMITER ;

CALL makeUserReservation(2, 24, 19, @output_reservation_id, @output_ticket_id);
SELECT @output_reservation_id, @output_ticket_id;

SELECT * FROM reservation WHERE member_id = 1;
SELECT * FROM ticket WHERE reservation_id = @output_reservation_id;
SELECT * FROM seat WHERE seat_id = 19;
