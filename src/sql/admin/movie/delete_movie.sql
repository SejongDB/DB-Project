USE db2;

DROP PROCEDURE IF EXISTS DeleteMovie;

DELIMITER //
CREATE PROCEDURE DeleteMovie(IN member_id INT, IN selected_movie_id INT)
BEGIN
    DECLARE admin_role VARCHAR(255);

    SELECT role INTO admin_role 
    FROM member 
    WHERE member_id = member_id
    LIMIT 1;

    IF admin_role = 'ADMIN' THEN
        -- 조건식: 해당 영화가 스케줄에 있는지 확인
        IF EXISTS (SELECT * FROM schedule WHERE movie_id = selected_movie_id) THEN
            SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = '해당 영화에 스케줄이 있어 삭제할 수 없습니다.';
        ELSE
            -- 스케줄이 없을 경우 영화 삭제
            DELETE FROM movie WHERE movie_id = selected_movie_id;
        END IF;
    ELSE
        SIGNAL SQLSTATE '45000' 
        SET MESSAGE_TEXT = '권한이 없습니다. ADMIN 역할만 해당 작업을 수행할 수 있습니다.';
    END IF;
END//
DELIMITER ;

-- 관리자의 member_id == 1
CALL DeleteMovie(1, 13);
