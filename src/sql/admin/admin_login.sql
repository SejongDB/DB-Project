USE db2;

DELIMITER //
DROP PROCEDURE IF EXISTS GrantAdminPrivileges;
CREATE PROCEDURE GrantAdminPrivileges()
BEGIN
    GRANT ALL PRIVILEGES ON *.* TO 'root'@'localhost' WITH GRANT OPTION;
    FLUSH PRIVILEGES;
END //
DELIMITER ;


DROP PROCEDURE IF EXISTS LoginAdmin;

-- root/1234 로그인 시 자동 관리자 계정 member_id 1 할당되도록
DELIMITER //
CREATE PROCEDURE LoginAdmin(IN input_username VARCHAR(255), IN input_password VARCHAR(255), OUT output_admin_id INT)
BEGIN
	DECLARE member_id INT;
    
    IF input_username = 'root' AND input_password = '1234' THEN
        SET member_id = 1;
        CALL GrantAdminPrivileges();
    ELSE
        SET member_id = NULL;
    END IF;
    
    SET output_admin_id = member_id;
    SELECT output_admin_id;
END //
DELIMITER ;



SET @output_admin_id = NULL;
CALL LoginAdmin('root', '1234', @output_admin_id);

