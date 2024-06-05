USE db2;

DROP PROCEDURE IF EXISTS CreateMember;

DELIMITER //
CREATE PROCEDURE CreateMember(
    IN input_admin_id INT, 
    IN input_user_name VARCHAR(255), 
    IN input_user_phonenumber VARCHAR(255),
    IN input_user_email VARCHAR(255),
    OUT output_created_user_id INT
)
BEGIN

    IF input_admin_id = 0 THEN -- 관리자만 해당 쿼리를 실행할 수 있도록
        INSERT INTO `member` (`name`, `phonenumber`, `email`, `role`)
        VALUES (input_user_name, input_user_phonenumber, input_user_email, 'USER');
        SET output_created_user_id = LAST_INSERT_ID();
        
    ELSE
        SET output_created_user_id = NULL;
        SELECT '관리자만 새로운 사용자 정보를 추가할 수 있습니다.' AS Message;
    END IF;
END //
DELIMITER ;

SET @output_user_id = NULL;

CALL CreateMember(0, '김수빈', '01011112222', 'kimsubin@example.com', @output_user_id);
SELECT @output_user_id, m.name FROM member m WHERE m.member_id = @output_user_id;

