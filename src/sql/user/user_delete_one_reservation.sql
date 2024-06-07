USE db2;

DROP PROCEDURE IF EXISTS deleteUserReservation;

DELIMITER //

CREATE PROCEDURE deleteUserReservation(
    IN input_member_id INT,
    IN input_reservation_id INT -- 유저가 본인 예매 내역 조회 후 삭제하고 싶은 예매 내역 id를 넘기면 됨
)
BEGIN
    DECLARE seat_id INT;

    -- 티켓이 삭제된 좌석의 seat_availability를 0으로 변경
    SELECT t.seat_id INTO seat_id
    FROM ticket t
    WHERE t.reservation_id = input_reservation_id;

    UPDATE seat
    SET seat_availability = 0
    WHERE seat_id = seat_id;

    -- 해당 회원이 선택한 예약 및 연결된 티켓 삭제
    DELETE FROM ticket WHERE reservation_id = input_reservation_id;
    DELETE FROM reservation WHERE member_id = input_member_id AND reservation_id = input_reservation_id;
END //
	
DELIMITER ;

— 프로시저 호출 예시
CALL deleteUserReservation(2, 101);
