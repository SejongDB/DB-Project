USE db2;

DROP PROCEDURE IF EXISTS DeleteTheater;

DELIMITER //
CREATE PROCEDURE DeleteTheater(
    IN input_admin_name VARCHAR(255), 
    IN input_theater_id VARCHAR(50)
)
BEGIN
    IF input_admin_name = 'root' THEN
        -- delete 조건식 : 상영관에 관련된 스케줄, 티켓, 좌석이 있는지 확인
        IF EXISTS (SELECT 1 FROM schedule WHERE theater_id = input_theater_id) THEN
            SELECT '상영관에 관련된 스케줄이 있어 삭제할 수 없습니다.' AS Message;
        ELSEIF EXISTS (SELECT 1 FROM ticket WHERE theater_id = input_theater_id) THEN
            SELECT '상영관에 관련된 티켓이 있어 삭제할 수 없습니다.' AS Message;
        ELSEIF EXISTS (SELECT 1 FROM seat WHERE theater_id = input_theater_id) THEN
            SELECT '상영관에 관련된 좌석이 있어 삭제할 수 없습니다.' AS Message;
        ELSE
            -- 관련된 항목이 없는 경우 상영관 삭제
            DELETE FROM theater WHERE theater_id = input_theater_id;
            SELECT '상영관이 성공적으로 삭제되었습니다.' AS Message;
        END IF;
    ELSE
        SELECT '관리자만 상영관을 삭제할 수 있습니다.' AS Message;
    END IF;
END //
DELIMITER ;

CALL DeleteTheater('root', 'I');
SELECT * FROM theater;
