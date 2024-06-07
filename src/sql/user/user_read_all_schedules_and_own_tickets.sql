use db2;

DELIMITER //
DROP PROCEDURE IF EXISTS ExecuteUserReadAllScheduleAndTicket;
CREATE PROCEDURE ExecuteUserReadAllScheduleAndTicket (IN user_id INT)
BEGIN
    DECLARE user_role VARCHAR(255);

    -- 멤버의 role을 확인
    SELECT role INTO user_role FROM member WHERE member_id = user_id LIMIT 1;

    IF user_role = 'USER' THEN
        SELECT * FROM schedule;
    ELSE
        SELECT '관리자의 경우 관리자 계정으로 로그인해주세요.' AS message;
    END IF;
END//

DELIMITER ;

-- 로그인한 유저가 모든 상영정보를 알 수 있음
CALL ExecuteUserReadAllScheduleAndTicket(2);
