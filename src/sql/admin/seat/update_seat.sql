USE db2;

DROP PROCEDURE IF EXISTS UpdateSeat;

DELIMITER //
CREATE PROCEDURE UpdateSeat(
    IN input_admin_name VARCHAR(255),
    IN input_seat_id INT,
    IN input_row INT,
    IN input_column INT,
    IN input_seat_availability TINYINT(1)
)
BEGIN
    DECLARE existing_theater_id VARCHAR(50);

    IF input_admin_name = 'root' THEN 
        -- 이미 존재하는 theater_id 조회
        SELECT theater_id INTO existing_theater_id FROM seat WHERE seat_id = input_seat_id;
        
        IF (SELECT seat_availability FROM seat WHERE seat_id = input_seat_id) = 1 THEN
            SELECT '회원에 의해 이미 발권된 티켓의 좌석 정보는 수정할 수 없습니다.' AS Message;
        ELSEIF EXISTS (
            SELECT 1 
            FROM seat 
            WHERE theater_id = existing_theater_id 
            AND `row` = input_row 
            AND `column` = input_column 
            AND seat_id != input_seat_id
        ) THEN
            SELECT '이미 다른 회원이 지정한 좌석입니다.' AS Message;
        ELSE
            UPDATE `seat`
            SET 
                `row` = COALESCE(input_row, `row`),
                `column` = COALESCE(input_column, `column`),
                seat_availability = COALESCE(input_seat_availability, seat_availability)
            WHERE seat_id = input_seat_id;
            SELECT '회원의 좌석 정보가 성공적으로 업데이트되었습니다.' AS Message;
        END IF;
    ELSE
        SELECT '관리자만 좌석 정보를 수정할 수 있습니다.' AS Message;
    END IF;
END //
DELIMITER ;

-- 프로시저 테스트
CALL UpdateSeat('root', 5, 2, 3, 0); -- 이미 다른 사용자가 예약한 좌석이라는 예외 문구가 뜨는 코드
CALL UpdateSeat('root', 5, 1, 5, 0); -- 다른 사용자가 예매하지 않은 좌석의 좌표를 넣어줘야 함

