-- user 로그인 시 가장 먼저 실행해주어야 하는 코드 (procedure 정의)
USE db2;

DROP PROCEDURE IF EXISTS CheckUserRole;
DELIMITER //
CREATE PROCEDURE CheckUserRole(
    IN input_member_id INT,
    OUT is_user_role BOOLEAN
)
BEGIN
    DECLARE user_role VARCHAR(255);
    
    SELECT role INTO user_role
    FROM member
    WHERE member_id = input_member_id;
    
    IF user_role = 'USER' THEN
        SET is_user_role = TRUE;
    ELSE
        SET is_user_role = FALSE;
    END IF;
END //
DELIMITER ;