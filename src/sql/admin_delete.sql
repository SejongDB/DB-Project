use db2;

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
        DELETE FROM movie m
        WHERE m.movie_id = selected_movie_id;
    ELSE
        SIGNAL SQLSTATE '45000' 
        SET MESSAGE_TEXT = '권한이 없습니다. ADMIN 역할만 해당 작업을 수행할 수 있습니다.';
    END IF;
END//
DELIMITER ;

-- 관리자의 member_id == 0
CALL DeleteMovie(0, 2);

