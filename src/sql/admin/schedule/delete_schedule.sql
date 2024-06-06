USE db2;

DROP PROCEDURE IF EXISTS deleteSchedule;

DELIMITER //
CREATE PROCEDURE deleteSchedule(
    IN input_admin_name VARCHAR(255),
    IN input_schedule_id INT
)
BEGIN
    IF input_admin_name = 'root' THEN
        -- 예약 삭제
        DELETE FROM reservation
        WHERE reservation_id IN (
            SELECT t.reservation_id
            FROM ticket t
            WHERE t.schedule_id = input_schedule_id
        );
        
        -- 좌석 삭제
        DELETE FROM seat
        WHERE seat_id IN (
            SELECT t.seat_id
            FROM ticket t
            WHERE t.schedule_id = input_schedule_id
        );
        
        -- 티켓 삭제
        DELETE FROM ticket
        WHERE schedule_id = input_schedule_id;
        
        -- 스케줄 삭제
        DELETE FROM schedule
        WHERE schedule_id = input_schedule_id;
        
        SELECT '상영일정 및 관련된 티켓, 좌석, 예매 정보가 성공적으로 삭제되었습니다.' AS Message;
    ELSE
        SELECT '관리자만 상영일정을 삭제할 수 있습니다.' AS Message;
    END IF;
END //
DELIMITER ;

-- 프로시저 실행 예시
CALL deleteSchedule('root', 1);
