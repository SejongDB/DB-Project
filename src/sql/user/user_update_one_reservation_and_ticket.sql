USE db2;

DROP PROCEDURE IF EXISTS updateUserReservation;

DELIMITER //

CREATE PROCEDURE updateUserReservation(
    IN input_member_id INT,
    IN input_reservation_id INT, -- 업데이트할 예매 아이디를 넣어주면 됨
    IN input_schedule_id INT, -- 바꾸고 싶은 영화 스케쥴 일정을 고르면 됨
    IN input_seat_id INT -- 원하는 좌석의 아이디(아직 아무도 티켓 발권받지 않은 좌석: ticket_availability 0인 id)를 넘기면 됨
)
BEGIN
    DECLARE schedule_theater_id VARCHAR(50);
    DECLARE seat_theater_id VARCHAR(50);
    
    -- 예약이 존재하는지 확인
    IF NOT EXISTS (SELECT * FROM reservation WHERE reservation_id = input_reservation_id AND member_id = input_member_id) THEN
        SELECT '예약을 찾을 수 없습니다.' AS Message;
    END IF;

    -- 새로운 상영 일정의 영화관 ID 확인
    SELECT theater_id INTO schedule_theater_id
    FROM schedule
    WHERE schedule_id = input_schedule_id;

    -- 선택한 좌석의 영화관 ID 확인
    SELECT theater_id INTO seat_theater_id
    FROM seat
    WHERE seat_id = input_seat_id;

    -- 상영 일정과 좌석이 다른 경우 예외 처리
    IF schedule_theater_id != seat_theater_id THEN
        SELECT '선택한 영화는 해당 상영관에서 상영되지 않습니다.' AS Message;
    END IF;

    -- 변경된 schedule에 따라 티켓 정보 변경
    UPDATE ticket
    SET schedule_id = input_schedule_id,
		theater_id = (select theater_id from schedule s where s.schedule_id = input_schedule_id),
        seat_id = input_seat_id
    WHERE reservation_id = input_reservation_id;

    -- 좌석의 상태 변경 및 티켓 상영관 정보도 함께 업데이트 : 티켓 발권 되었으므로 1
    UPDATE seat
    SET seat_availability = 1
    WHERE seat_id = input_seat_id;

    SELECT '예약이 성공적으로 업데이트되었습니다.' AS Message;
END //

DELIMITER ;

CALL updateUserReservation(2, 1, 14, 34);
