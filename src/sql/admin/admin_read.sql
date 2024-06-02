DELIMITER //

CREATE PROCEDURE ExecuteAdminAllTablesReadQuery (IN user_id INT)
BEGIN
    DECLARE user_role VARCHAR(255);

    -- 멤버의 role을 확인
    SELECT role INTO user_role FROM member WHERE member_id = user_id LIMIT 1;

    -- 역할이 ADMIN인 경우에만 쿼리를 실행
    IF user_role = 'ADMIN' THEN
        SELECT * FROM member;
        SELECT * FROM movie;
        SELECT * FROM reservation;
        SELECT * FROM schedule;
        SELECT * FROM seat;
        SELECT * FROM theater;
        SELECT * FROM ticket;
    ELSE
    	SELECT '관리자만 해당 쿼리를 실행할 수 있습니다.' AS message;
    END IF;
END//

DELIMITER ;

-- 관리자는 member 테이블에서 id가 0
CALL ExecuteAdminAllTablesReadQuery(0);