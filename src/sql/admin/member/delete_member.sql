USE db2;

DROP PROCEDURE IF EXISTS DeleteMember;

DELIMITER //

CREATE PROCEDURE DeleteMember(
    IN target_member_id INT
)
BEGIN
    -- 외래키 제약 조건으로 인해 해당 회원과 관련된 튜플 먼저 삭제
    DELETE FROM ticket WHERE reservation_id IN (SELECT reservation_id FROM reservation WHERE member_id = target_member_id);
    DELETE FROM reservation WHERE member_id = target_member_id;
    
    -- 해당 회원 삭제
    DELETE FROM member WHERE member_id = target_member_id;

END //

DELIMITER ;

CALL DeleteMember(2); -- 탈퇴 시키고 싶은 회원의 아이디(>=2) 파라미터로 넘겨야 함
