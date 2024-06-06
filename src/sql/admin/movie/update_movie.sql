USE db2;

DROP PROCEDURE IF EXISTS UpdateMovie;

DELIMITER //
CREATE PROCEDURE UpdateMovie(
    IN input_admin_name VARCHAR(255), 
    IN input_movie_id INT,
    IN input_movie_name VARCHAR(255), 
    IN input_showtime INT,
    IN input_rating VARCHAR(255),
	IN input_director VARCHAR(255),
	IN input_actor VARCHAR(255),
	IN input_genre VARCHAR(255),
	IN input_instruction VARCHAR(512),
	IN input_movie_created_at DATETIME,
	IN input_grade FLOAT
)
BEGIN
    IF input_admin_name = 'root' THEN -- 관리자만 해당 쿼리를 실행할 수 있도록
        IF (SELECT rating FROM movie WHERE movie_id = input_movie_id) = 'A' THEN -- update 조건식 : 등급이 A인 영화 수정 가능
            UPDATE `movie`
            SET 
                movie_name = COALESCE(input_movie_name, movie_name), -- 파라미터 null일 경우
                showtime = COALESCE(input_showtime, showtime),
                -- rating은 'A'이므로 업데이트하지 않하고 그대로 유지
                director = COALESCE(input_director, director),
                actor = COALESCE(input_actor, actor),
                genre = COALESCE(input_genre, genre),
                instruction = COALESCE(input_instruction, instruction),
                movie_created_at = COALESCE(input_movie_created_at, movie_created_at),
                grade = COALESCE(input_grade, grade)
            WHERE movie_id = input_movie_id;
        ELSEIF (SELECT grade FROM movie WHERE movie_id = input_movie_id) <= 7 THEN -- 평점이 7이하인 경우 수정 가능
            UPDATE `movie`
            SET 
                movie_name = COALESCE(input_movie_name, movie_name),
                showtime = COALESCE(input_showtime, showtime),
                rating = COALESCE(input_rating, rating),
                director = COALESCE(input_director, director),
                actor = COALESCE(input_actor, actor),
                genre = COALESCE(input_genre, genre),
                instruction = COALESCE(input_instruction, instruction),
                movie_created_at = COALESCE(input_movie_created_at, movie_created_at),
                grade = COALESCE(input_grade, grade)
            WHERE movie_id = input_movie_id;
        ELSE
            SELECT '영화 정보는 상영등급이 A일 때와 평점이 7 이하일 경우에만 수정 가능합니다.' AS Message;
        END IF;
    ELSE
        SELECT '관리자만 영화를 수정할 수 있습니다.' AS Message;
    END IF;
END //
DELIMITER ;

CALL UpdateMovie('root', 3, '그것이 궁금하다', 140, NULL, NULL, NULL, '다큐멘터리', '불편한 진실', '2023-12-30', 8.5);
SELECT * FROM movie where movie_id = 3;
