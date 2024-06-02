USE db2;

DROP PROCEDURE IF EXISTS SearchMovies;
DELIMITER //
CREATE PROCEDURE SearchMovies(
    IN input_member_id INT,
    IN movie_title VARCHAR(255),
    IN director_name VARCHAR(255),
    IN actor_name VARCHAR(255),
    IN genre_name VARCHAR(255)
)
BEGIN
    DECLARE is_user_role BOOLEAN;
    
    CALL CheckUserRole(input_member_id, is_user_role);
    
    IF is_user_role THEN
        SELECT m.movie_name AS 영화명, m.director AS 감독명, m.actor AS 배우명, m.genre AS 장르
        FROM movie m
        WHERE (movie_title IS NULL OR m.movie_name LIKE CONCAT('%', movie_title, '%'))
          AND (director_name IS NULL OR m.director LIKE CONCAT('%', director_name, '%'))
          AND (actor_name IS NULL OR m.actor LIKE CONCAT('%', actor_name, '%'))
          AND (genre_name IS NULL OR m.genre LIKE CONCAT('%', genre_name, '%'));
    ELSE
        SELECT 'USER 회원 권한인 사용자만 해당 쿼리를 실행할 수 있습니다.' AS Message;
    END IF;
END //
DELIMITER ;

CALL SearchMovies(1, '무야호', NULL, NULL, NULL);
