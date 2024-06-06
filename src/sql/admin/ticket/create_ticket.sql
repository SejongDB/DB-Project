USE db2;

DROP PROCEDURE IF EXISTS CreateTicket;

DELIMITER //
CREATE PROCEDURE CreateTicket(
    IN input_admin_name VARCHAR(255), 
	IN input_standard_price	int4,
	IN input_seat_id INT, -- seat 테이블에서 seat_availability가 0(자리 비어있음)인 seat_id를 넘겨줘야 함
	IN input_reservation_id INT,
	IN input_schedule_id INT,
    OUT output_created_ticket_id INT
)
BEGIN

    IF input_admin_name = 'root' THEN -- 관리자만 해당 쿼리를 실행할 수 있도록
        INSERT INTO `ticket` (`reservation_id`, `ticket_availibility`, `standard_price`, `sale_price`, `schedule_id`, `seat_id`, `theater_id`)
		VALUES
		(input_reservation_id, 
        1, -- 티켓을 항상 이 함수에서 관리자가 발권하는 것이므로 1
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

SET @output_created_ticket_id = NULL;

CALL CreateTicket('root', 12000,  5, 26, 4, @output_created_ticket_id);
SELECT @output_created_ticket_id, t.* FROM ticket t WHERE t.ticket_id = @output_created_ticket_id;

-- select * from schedule;
select * from reservation;
-- select * from seat;