USE db2;

DROP PROCEDURE IF EXISTS deleteUserReservation;

DELIMITER //

CREATE PROCEDURE deleteUserReservation(
    IN input_member_id INT,
    IN input_reservation_id INT -- 유저가 본인 예매 내역 조회 후 삭제하고 싶은 예매 내역 id를 념기면 됨
)
BEGIN

	-- 티켓이 삭제된 좌석의 seat_availability를 0으로 변경
    UPDATE seat s
    JOIN (
        SELECT seat_id
        FROM ticket
        WHERE reservation_id IN (SELECT reservation_id FROM reservation)
    ) AS deleted_seats ON s.seat_id = deleted_seats.seat_id
    SET s.seat_availability = 0;
    
    -- 해당 회원이 선택한 예약 및 연결된 티켓 삭제
    DELETE FROM reservation WHERE member_id = input_member_id and reservation_id = input_reservation_id;
    DELETE t FROM ticket t
    JOIN reservation r ON t.reservation_id = r.reservation_id
    WHERE r.member_id = input_member_id;

    
END //

DELIMITER ;

CALL deleteUserReservation(2,25);
select * from reservation where reservation_id = 25;
select * from ticket where ticket_id = 25;
select * from seat where seat_id = 19;