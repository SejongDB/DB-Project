USE db2;

DROP PROCEDURE IF EXISTS CreateTheater;

DELIMITER //
CREATE PROCEDURE CreateTheater(
    IN input_admin_name VARCHAR(255), 
    IN input_theater_id VARCHAR(50),
	IN input_theater_availability TINYINT(1),
	IN input_row_seat INT,
	IN input_column_seat INT,
	IN input_total_seat INT
)
BEGIN

    IF input_admin_name = 'root' THEN -- 관리자만 해당 쿼리를 실행할 수 있도록
        INSERT INTO `theater` (`theater_id`, `theater_availability`, `row_seat`, `column_seat`, `total_seat`)
		VALUES
		(input_theater_id, input_theater_availability, input_row_seat, input_column_seat, input_total_seat);
        
    ELSE
        SELECT '관리자만 새로운 사용자 정보를 추가할 수 있습니다.' AS Message;
    END IF;
END //
DELIMITER ;

SET @output_created_theater_id = NULL;

CALL CreateTheater('root', 'Y', 1, 11, 10, 110);

