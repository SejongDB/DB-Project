USE db2;

DROP PROCEDURE IF EXISTS LoginUser;

-- user1/user1 로그인 시 자동 member_id 1 할당되도록
DELIMITER //
CREATE PROCEDURE LoginUser(IN input_username VARCHAR(255), IN input_password VARCHAR(255), OUT output_member_id INT)
BEGIN
	DECLARE member_id INT;
    
    IF input_username = 'user1' AND input_password = 'user1' THEN
        SET member_id = 1;
    ELSE
        SET member_id = NULL;
    END IF;
    
    SET output_member_id = member_id;
END //
DELIMITER ;

SET @output_member_id = NULL;
CALL LoginUser('user1', 'user1', @output_member_id);
SELECT @output_member_id, name  from member m where m.member_id = @output_member_id ;
