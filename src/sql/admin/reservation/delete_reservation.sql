USE db2;

DROP PROCEDURE IF EXISTS DeleteReservation;

DELIMITER //
CREATE PROCEDURE DeleteReservation(
    IN input_admin_name VARCHAR(255), 
    IN input_reservation_id INT
)
BEGIN
    IF input_admin_name = 'root' THEN
        IF EXISTS (SELECT 1 FROM reservation WHERE reservation_id = input_reservation_id) THEN
            -- 외래키 제약 조건으로 ticket 삭제
            DELETE FROM ticket WHERE reservation_id = input_reservation_id;
            -- 이제 예약 내역 삭제
            DELETE FROM reservation WHERE reservation_id = input_reservation_id;
        ELSE
            SELECT '삭제하려는 예약이 존재하지 않습니다.' AS Message;
        END IF;
    ELSE
        SELECT '관리자만 예약을 삭제할 수 있습니다.' AS Message;
    END IF;
END //
DELIMITER ;

CALL DeleteReservation('root', 4);
SELECT * FROM reservation WHERE reservation_id = 4;
SELECT * FROM ticket WHERE reservation_id = 4;
