USE db2;

DROP PROCEDURE IF EXISTS ViewOneReservationDetails;
DELIMITER //
CREATE PROCEDURE ViewOneReservationDetails(
	IN input_member_id INT,
    IN input_ticket_id INT
)
BEGIN
    DECLARE is_user_role BOOLEAN;
    
    CALL CheckUserRole(input_member_id, is_user_role);
    
    IF is_user_role THEN
        SELECT ti.ticket_availibility AS 티켓발권여부, -- ti.*, s.*, th.*
			   ti.standard_price AS 표준가격,
               ti.sale_price AS 판매가격,
               s.created_at AS 상영시작일,
               s.screening_day AS 상영요일,
               s.screening_count AS 상영회차,
               s.start_time AS 상영시작시간,
               s.end_time AS 상영종료시간, 
               th.theater_availability AS 상영관사용여부,
               th.row_seat AS 상영관가로좌석수,
               th.column_seat AS 상영관세로좌석수,
               th.total_seat AS 상영관전체좌석수
        FROM ticket ti, schedule s, theater th
        WHERE (ti.ticket_id = input_ticket_id and ti.schedule_id = s.schedule_id 
        and s.theater_id = th.theater_id and ti.theater_id = th.theater_id);
			
    ELSE
        SELECT 'USER 회원 권한인 사용자만 예매 내역을 조회할 수 있습니다.' AS Message;
    END IF;
END //
DELIMITER ;

-- user1/user1로 로그인한 유저 (member_id == 1)가 예매한 영화 중 하나
CALL ViewOneReservationDetails(1, 13);