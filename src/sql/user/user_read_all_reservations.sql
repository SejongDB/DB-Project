USE db2;

DROP PROCEDURE IF EXISTS ViewReservations;
DELIMITER //
CREATE PROCEDURE ViewReservations(
    IN input_member_id INT
)
BEGIN
    DECLARE is_user_role BOOLEAN;
    
    CALL CheckUserRole(input_member_id, is_user_role);
    
    IF is_user_role THEN
        SELECT m.movie_name AS 영화명,
               s.created_at AS 상영일,
               s.theater_id AS 상영관번호,
               t.seat_id AS 좌석번호,
               t.sale_price AS 판매가격,
               t.ticket_id AS 티켓아이디
        FROM reservation r, ticket t, schedule s, movie m
        WHERE (r.member_id = input_member_id and r.reservation_id = t.reservation_id and 
        t.schedule_id = s.schedule_id and s.movie_id = m.movie_id);
			
    ELSE
        SELECT 'USER 회원 권한인 사용자만 예매 내역을 조회할 수 있습니다.' AS Message;
    END IF;
END //
DELIMITER ;

-- user1/user1로 로그인한 유저의 member_id == 2
CALL ViewReservations(2);
