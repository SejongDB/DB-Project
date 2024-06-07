USE db2;

DROP PROCEDURE IF EXISTS updateTicket;

DELIMITER //
CREATE PROCEDURE updateTicket(
    IN input_admin_name VARCHAR(255), 
    IN input_ticket_id INT,
    OUT output_updated_ticket_id INT
)
BEGIN

    DECLARE payment_amount INT;

    IF input_admin_name = 'root' THEN -- 관리자만 해당 쿼리를 실행할 수 있도록
        IF ((SELECT ticket_availibility FROM ticket WHERE ticket_id = input_ticket_id) = 1) THEN -- 예매 확정 시
            UPDATE `ticket`
            SET `ticket_availibility` = 0 -- 예매 취소
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

SET @output_updated_ticket_id = NULL;

CALL updateTicket('root', 1, @output_updated_ticket_id);
SELECT @output_updated_ticket_id, t.* FROM ticket t WHERE t.ticket_id = @output_updated_ticket_id;

SELECT * FROM ticket WHERE ticket_id = 1;
