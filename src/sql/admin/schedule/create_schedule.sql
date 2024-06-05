USE db2;

DROP PROCEDURE IF EXISTS CreateSchedule;

DELIMITER //
CREATE PROCEDURE CreateSchedule(
    IN input_admin_name VARCHAR(255), 
	IN input_created_at DATETIME,
	IN input_screening_day VARCHAR(255),
	IN input_screening_count INT,
	IN input_start_time DATETIME,
	IN input_movie_id INT,
	IN input_theater_id VARCHAR(50),
    OUT output_created_schedule_id INT
)
BEGIN

    IF input_admin_name = 'root' THEN -- 관리자만 해당 쿼리를 실행할 수 있도록
        INSERT INTO `schedule` (`created_at`, `screening_day`, `screening_count`, `start_time`, `end_time`, `movie_id`, `theater_id`)
        VALUES
        (input_created_at, input_screening_day, input_screening_count, input_start_time, DATE_ADD(input_start_time, INTERVAL (SELECT `showtime` FROM `movie` WHERE `movie_id` = input_movie_id) MINUTE), input_movie_id, input_theater_id);
        
        SET output_created_schedule_id = LAST_INSERT_ID();
        
    ELSE
        SET output_created_schedule_id = NULL;
        SELECT '관리자만 새로운 사용자 정보를 추가할 수 있습니다.' AS Message;
    END IF;
END //
DELIMITER ;

SET @output_created_schedule_id = NULL;

CALL CreateSchedule('root','2024-01-29', '월요일', 4, '2024-02-01 10:00:00', 15, 'Z', @output_created_schedule_id);
SELECT @output_created_seat_id, s.* FROM schedule s WHERE s.schedule_id = @output_created_schedule_id;
