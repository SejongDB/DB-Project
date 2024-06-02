use db2;

-- 기존에 같은 이름의 프로시저가 있을 경우 삭제
DROP PROCEDURE IF EXISTS UpdateTicketStatus;

-- 프로시저를 생성하여 역할 기반 접근 제어 기능 구현
DELIMITER //
CREATE PROCEDURE UpdateTicketStatus(IN member_id INT, IN reservation_id INT)
BEGIN
    DECLARE admin_role VARCHAR(255);

    -- 현재 사용자가 ADMIN 역할인지 확인
    SELECT role INTO admin_role 
    FROM member 
    WHERE member_id = member_id
    LIMIT 1;

    -- 사용자 역할이 ADMIN일 경우에만 업데이트 쿼리 실행
    IF admin_role = 'ADMIN' THEN
        UPDATE schedule s
        JOIN movie m 
        SET s.theater_id = 'G'
        WHERE m.movie_id = s.movie_id and m.movie_id = 1;
    ELSE
        SIGNAL SQLSTATE '45000' 
        SET MESSAGE_TEXT = '권한이 없습니다. ADMIN 역할만 해당 작업을 수행할 수 있습니다.';
    END IF;
END//
DELIMITER ;

-- 프로시저 호출 예시 (관리자의 member_id == 0)
CALL UpdateTicketStatus(0, 1);


select * from schedule;
