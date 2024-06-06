USE db2;

DROP PROCEDURE IF EXISTS UpdateReservation;

DELIMITER //
CREATE PROCEDURE UpdateReservation(
    IN input_admin_name VARCHAR(255), 
    IN input_reservation_id INT,
    IN input_payment_method VARCHAR(255),
    IN input_payment_amount INT,
    IN input_payment_status VARCHAR(255),
    IN input_member_id INT
)
BEGIN
    IF input_admin_name = 'root' THEN 
        IF (SELECT payment_status FROM reservation WHERE reservation_id = input_reservation_id) = 'PENDING' THEN -- 조건식 : 예매 상태가 PENDING(대기 상태)인 경우에만 경우 수정 가능
            UPDATE `reservation`
            SET 
                payment_method = COALESCE(input_payment_method, payment_method), -- 파라미터가 NULL일 경우 기존 값 유지
                payment_amount = COALESCE(input_payment_amount, payment_amount),
                payment_status = COALESCE(input_payment_status, payment_status),
                payment_date = now(), -- 현재 시각으로 자동 갱신
                member_id = COALESCE(input_member_id, member_id)
            WHERE reservation_id = input_reservation_id;
        ELSE
            SELECT '지불 대기 상태인 예약 내역만 갱신할 수 있습니다.' AS Message;
        END IF;
    ELSE
        SELECT '관리자만 예매를 수정할 수 있습니다.' AS Message;
    END IF;
END //
DELIMITER ;

-- 프로시저 테스트
CALL UpdateReservation('root', 4, '현금 결제', 15000, 'CONFIRMED', NULL);
SELECT * FROM reservation where reservation_id = 4;
