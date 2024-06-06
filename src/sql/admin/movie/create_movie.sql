USE db2;

DROP PROCEDURE IF EXISTS CreateMovie;

DELIMITER //
CREATE PROCEDURE CreateMovie(
    IN input_admin_name VARCHAR(255), 
    IN input_movie_name VARCHAR(255), 
    IN input_showtime INT,
    IN input_rating VARCHAR(255),
	IN input_director VARCHAR(255) ,
	IN input_actor VARCHAR(255) ,
	IN input_genre VARCHAR(255) ,
	IN input_instruction VARCHAR(512) ,
	IN input_movie_created_at DATETIME ,
	IN input_grade FLOAT,
    OUT output_created_movie_id INT
)
BEGIN

    IF input_admin_name = 'root' THEN -- 관리자만 해당 쿼리를 실행할 수 있도록
        INSERT INTO `movie` (`movie_name`, `showtime`, `rating`, `director`, `actor`, `genre`, `instruction`, `movie_created_at`, `grade`)
        VALUES
        (input_movie_name, input_showtime, input_rating, input_director, input_actor, input_genre, input_instruction, input_movie_created_at, input_grade);
        SET output_created_movie_id = LAST_INSERT_ID();
        
    ELSE
        SET output_created_movie_id = NULL;
        SELECT '관리자만 새로운 사용자 영화를 추가할 수 있습니다.' AS Message;
    END IF;
END //
DELIMITER ;

SET @output_created_movie_id = NULL;

CALL CreateMovie('root', '지옥', 120, 'C', '이박명', '송가민', '스릴러', '지옥을 체험하고 싶다면...', '2023-12-25', 7.5, @output_created_movie_id);
SELECT @output_created_movie_id, m.* FROM movie m WHERE m.movie_id = @output_created_movie_id;

