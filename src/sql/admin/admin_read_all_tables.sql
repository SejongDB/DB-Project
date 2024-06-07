DELIMITER //

drop procedure if exists ExecuteAdminAllTablesReadQuery;

CREATE PROCEDURE ExecuteAdminAllTablesReadQuery (IN user_id INT)
BEGIN
    DECLARE user_role VARCHAR(255);

    -- 멤버의 role을 확인
    SELECT role INTO user_role FROM member WHERE member_id = user_id LIMIT 1;

    -- 역할이 ADMIN인 경우에만 쿼리를 실행
    IF user_role = 'ADMIN' THEN
        SELECT 
			m.movie_id as 영화번호,
            m.movie_name as 영화명,
			m.showtime as 상영시간,
			m.rating as 상영등급,
			m.director as 감독명,
			m.actor as 배우명,
			m.genre as 장르,
			m.instruction as 영화소개,
			m.movie_created_at as 개봉일자,
			m.grade as 평점정보
        FROM movie m;
        
        SELECT 
			m.member_id AS 회원아이디,
			m.name AS 고객명,
			m.phonenumber AS 휴대폰번호,
			m.email AS 전자메일주소,
			m.role AS 역할
		FROM member m;

        SELECT 
			r.reservation_id AS 예매번호,
			r.payment_method AS 결제방법,
			r.payment_amount AS 결제금액,
			r.payment_status AS 결제상태,
			r.payment_date AS 결제일자,
			r.member_id AS 회원아이디
		FROM reservation r;
        
        SELECT 
			s.schedule_id AS 상영일정번호,
			s.created_at AS 상영시작일,
			s.screening_day AS 상영요일,
			s.screening_count AS 상영회차,
			s.start_time AS 상영시작시간,
			s.end_time AS 상영종료시간,
			s.movie_id AS 영화번호,
			s.theater_id AS 상영관번호
		FROM schedule s;

        SELECT 
			s.seat_id AS 좌석번호,
			s.theater_id AS 상영관번호,
			s.row AS 행위치,
			s.column AS 열위치,
			s.seat_availability AS 좌석사용여부
		FROM seat s;
        
        SELECT 
			t.theater_id AS 상영관번호,
			t.theater_availability AS 상영관서용여부,
			t.row_seat AS 가로좌석수,
			t.column_seat AS 세로좌석수,
			t.total_seat AS 전체좌석수
		FROM theater t;

        SELECT 
			t.ticket_id AS 티켓번호,
			t.ticket_availibility AS 발권여부,
			t.standard_price AS 표준가격,
			t.sale_price AS 판매가격,
			t.seat_id AS 좌석번호,
			t.reservation_id AS 예매번호,
			t.schedule_id AS 상영일정번호,
			t.theater_id AS 상영관번호
		FROM ticket t;
    ELSE
    	SELECT '관리자만 해당 쿼리를 실행할 수 있습니다.' AS message;
    END IF;
END//

DELIMITER ;

-- 관리자는 member 테이블에서 id가 1
CALL ExecuteAdminAllTablesReadQuery(1);