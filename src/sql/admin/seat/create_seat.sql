USE db2;

DROP PROCEDURE IF EXISTS CreateSeat;

DELIMITER //
CREATE PROCEDURE CreateSeat(
    IN input_admin_name VARCHAR(255), 
	IN input_theater_id VARCHAR(255),
	IN input_row INT,
	IN input_column INT,
	IN input_seat_availability	tinyint(1),
    OUT output_created_seat_id INT
)
BEGIN

    IF input_admin_name = 'root' THEN -- 관리자만 해당 쿼리를 실행할 수 있도록
        INSERT INTO `seat` (`theater_id`, `row`, `column`, `seat_availability`)
		VALUES
		(input_theater_id, input_row, input_column, input_seat_availability);
        SET output_created_seat_id = LAST_INSERT_ID();
        
    ELSE
        SET output_created_seat_id = NULL;
        SELECT '관리자만 새로운 사용자 정보를 추가할 수 있습니다.' AS Message;
    END IF;
END //
DELIMITER ;

SET @output_created_seat_id = NULL;

CALL CreateSeat('root', 'Y', 1, 1, 1, @output_created_seat_id);
SELECT @output_created_seat_id, s.* FROM seat s WHERE s.seat_id = @output_created_seat_id;

