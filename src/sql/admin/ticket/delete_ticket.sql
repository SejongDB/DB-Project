USE db2;

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

    IF input_admin_name = 'root' THEN -- 관리자만 해당 쿼리를 실행할 수 있도록

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

SET @output_deleted_ticket_id = NULL;

CALL deleteTicket('root', 1, @output_deleted_ticket_id);
SELECT @output_deleted_ticket_id;