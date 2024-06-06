USE db2;

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

CALL DeleteSeat('root', 4);
