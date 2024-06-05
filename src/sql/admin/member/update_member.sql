USE db2;

DROP PROCEDURE IF EXISTS UpdateMember;

DELIMITER //
CREATE PROCEDURE UpdateMember(
    IN input_admin_id INT, 
    IN target_member_id INT,
    IN input_new_user_name VARCHAR(255), 
    IN input_new_user_phonenumber VARCHAR(255),
    IN input_new_user_email VARCHAR(255)
)
BEGIN

    IF input_admin_id = 0 THEN -- 관리자만 해당 쿼리를 실행할 수 있도록
        UPDATE member
        SET name = input_new_user_name, 
            phonenumber = input_new_user_phonenumber, 
            email = input_new_user_email
        WHERE member_id = target_member_id;
        
    ELSE
        SELECT '관리자만 새로운 사용자 정보를 추가할 수 있습니다.' AS Message;
    END IF;
END //
DELIMITER ;

CALL UpdateMember(0, 2, '김수정', '01033334444', 'kimsoojung@example.com');
select * from member where member_id = 2;


