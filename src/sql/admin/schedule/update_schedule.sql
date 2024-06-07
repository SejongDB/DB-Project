USE db2;

DROP PROCEDURE IF EXISTS UpdateSchedule;

DELIMITER //
CREATE PROCEDURE UpdateSchedule(
    IN input_admin_name VARCHAR(255),
    IN input_schedule_id INT,
    IN input_screening_day VARCHAR(255),
    IN input_screening_count INT,
    IN input_start_time DATETIME
)
BEGIN
    DECLARE temporary_time DATETIME DEFAULT '2022-12-31 00:00:00';
    DECLARE movie_showtime INT;

    IF input_admin_name = 'root' THEN
        IF (SELECT start_time FROM schedule WHERE schedule_id = input_schedule_id) < temporary_time THEN
            SELECT '특정 시점(2022-12-31) 이후에 상영을 시작하는 스케쥴만 수정할 수 있습니다.' AS Message;
        ELSE
            SET movie_showtime = (SELECT `showtime` FROM `movie` WHERE `movie_id` = (SELECT movie_id FROM schedule WHERE schedule_id = input_schedule_id));
            
            UPDATE `schedule`
            SET `screening_day` = input_screening_day,
                `screening_count` = input_screening_count,
                `start_time` = input_start_time,
                `end_time` = DATE_ADD(input_start_time, INTERVAL movie_showtime MINUTE)
            WHERE `schedule_id` = input_schedule_id;
            
            SELECT '상영일정이 성공적으로 업데이트되었습니다.' AS Message;
        END IF;
    ELSE
        SELECT '관리자만 상영일정을 수정할 수 있습니다.' AS Message;
    END IF;
END //
DELIMITER ;

CALL UpdateSchedule('root', 1, '목요일', 3, '2024-06-06 10:00:00');

