USE db2;

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

    IF input_admin_name = 'root' THEN -- 관리자만 해당 쿼리를 실행할 수 있도록
        INSERT INTO `reservation` (`member_id`, `payment_method`, `payment_amount`, `payment_date`, `payment_status`)
		VALUES
		(input_member_id, input_payment_method, input_payment_amount, input_payment_date, input_payment_status);
        SET output_created_reservation_id = LAST_INSERT_ID();
        
    ELSE
        SET output_created_reservation_id = NULL;
        SELECT '관리자만 새로운 사용자 정보를 추가할 수 있습니다.' AS Message;
    END IF;
END //
DELIMITER ;

SET @output_created_reservation_id = NULL;

CALL CreateReservation('root', '카드 결제', 11000, 'CONFIRMED', '2024-01-19 10:20:00', 3, @output_created_reservation_id);
SELECT @output_created_reservation_id, r.* FROM reservation r WHERE r.reservation_id = @output_created_reservation_id;
