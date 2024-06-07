import java.io.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import java.awt.*;
import java.awt.event.*;

public class db2 {
	private static final String VALID_USERNAME_ADMIN = "root";
	private static final String VALID_PASSWORD_ADMIN = "1234";
	private static final String VALID_USERNAME_USER = "user1";
	private static final String VALID_PASSWORD_USER = "user1";
	
    private static int loggedInMemberId = 2;
    
    public static void main(String[] args) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver"); // MySQL 드라이버 로드
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/db2", "root", "1234"); // JDBC 연결
            System.out.println("DB 연결 완료");

            // GUI 부분
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    createAndShowGUI();
                }
            });

        } catch (ClassNotFoundException e) {
            System.out.println("JDBC 드라이버 로드 오류");
        } catch (SQLException e) {
            System.out.println("DB 연결 오류");
        }
    }
    //첫 화면 생성  
    private static void createAndShowGUI() {
        // 메인 프레임 생성
        JFrame frame = new JFrame("Database Project");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 400);

        // 메인 패널 생성
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new FlowLayout());

        // 관리자 로그인 버튼 생성
        JButton adminButton = new JButton("관리자 로그인");
        adminButton.setPreferredSize(new Dimension(150, 50));
        adminButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.dispose(); // 현재 프레임 닫기
                showLoginDialog("관리자", true);
            }
        });

        // 회원 로그인 버튼 생성
        JButton userButton = new JButton("회원 로그인");
        userButton.setPreferredSize(new Dimension(150, 50));
        userButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.dispose(); // 현재 프레임 닫기
                showLoginDialog("회원", false);
            }
        });

        // 버튼들을 패널에 추가
        mainPanel.add(adminButton);
        mainPanel.add(userButton);

        // 패널을 프레임에 추가
        frame.getContentPane().add(mainPanel, BorderLayout.CENTER);

        // 프레임 표시
        frame.setVisible(true);
    }

    //<로그인>
    //로그인 입력 화면
    private static void showLoginDialog(String role, boolean isAdmin) {
    JFrame loginFrame = new JFrame(role + " 로그인");
    loginFrame.setSize(300, 150);
    loginFrame.setLayout(new GridLayout(3, 2));
    loginFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

    JLabel userLabel = new JLabel("아이디:");
    JTextField userText = new JTextField(20);
    JLabel passwordLabel = new JLabel("비밀번호:");
    JPasswordField passwordText = new JPasswordField(20);

    JButton loginButton = new JButton("로그인");
    loginButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            String username = userText.getText();
            String password = new String(passwordText.getPassword());

            if (username.equals(VALID_USERNAME_ADMIN) && password.equals(VALID_PASSWORD_ADMIN)) {
                loginFrame.dispose();
                showAdminBoard();
            } else if (username.equals(VALID_USERNAME_USER) && password.equals(VALID_PASSWORD_USER)) {
                loginFrame.dispose();
                showUserBoard();
            } else {
                JOptionPane.showMessageDialog(loginFrame, "아이디 또는 비밀번호가 잘못되었습니다.", "로그인 오류", JOptionPane.ERROR_MESSAGE);
            }
        }
    });

    loginFrame.add(userLabel);
    loginFrame.add(userText);
    loginFrame.add(passwordLabel);
    loginFrame.add(passwordText);
    loginFrame.add(new JLabel());
    loginFrame.add(loginButton);

    loginFrame.setVisible(true);
}
    
   
    // [통신] 초기화 함수
    public static void initsamplecode() {
        try {
        	 Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/db2", "root", "1234");
             Statement stmt = conn.createStatement();

             stmt.execute("DROP TABLE IF EXISTS ticket;");
             stmt.execute("DROP TABLE IF EXISTS reservation;");
             stmt.execute("DROP TABLE IF EXISTS schedule;");
             stmt.execute("DROP TABLE IF EXISTS seat;");
             stmt.execute("DROP TABLE IF EXISTS theater;");
             stmt.execute("DROP TABLE IF EXISTS movie;");
             stmt.execute("DROP TABLE IF EXISTS member;");

             stmt.execute("CREATE TABLE movie (" +
                          "movie_id INT NOT NULL AUTO_INCREMENT," +
                          "movie_name VARCHAR(255) NULL," +
                          "showtime INT NULL," +
                          "rating VARCHAR(255) NULL," +
                          "director VARCHAR(255) NULL," +
                          "actor VARCHAR(255) NULL," +
                          "genre VARCHAR(255) NULL," +
                          "instruction VARCHAR(512) NULL," +
                          "movie_created_at DATETIME NULL," +
                          "grade FLOAT NULL," +
                          "PRIMARY KEY (movie_id)" +
                          ");");

             stmt.execute("INSERT INTO movie (movie_name, showtime, rating, director, actor, genre, instruction, movie_created_at, grade) VALUES" +
                          "('바람막이', 120, 'C', '이효성', '송철수', '액션', '송철수의 연기가 돋보이는 액션 영화', '2023-01-01', 8.5)," +
                          "('모 아니면 도', 110, 'B', '김현수', '김현수', '코미디', '배꼽이 빠지게 웃을 수 있는 코미디 영화', '2023-02-01', 9.8)," +
                          "('서프라이즈', 180, 'A', '홍영희', '전지민', '드라마', '전지민의 감정 이입을 통해 공감할 수 있는 인상적인 영화', '2023-03-01', 6.5)," +
                          "('수첩', 95, 'C', '장지선', '박태리', '로맨틱코미디', 'MZ 세대 기준 로맨틱코미디 추천 영화 1위', '2023-04-01', 8.9)," +
                          "('환경과 유전', 130, 'A', '이수민', '김유정', '다큐멘터리', '사색에 잠기고 싶다면 봐야하는 영화', '2023-05-01', 5.5)," +
                          "('움이의 세계', 105, 'C', '김혁', '김움', '애니메이션', '일상에 지친 당신에게 추천하는 감성 애니메이션', '2023-06-01', 7.3)," +
                          "('베이징의 연인', 140, 'B', '김수정', '신보윤', '로맨스', '꿈과 사랑이 가득한 따뜻한 로맨스 영화', '2023-07-01', 8.2)," +
                          "('어둠', 175, 'B', '김길동', '이수헌', '호러', '여름철 섬뜩해지고 싶다면 강추', '2023-08-01', 9.1)," +
                          "('판타지', 100, 'C', '이수현', '이동욱', '판타지', '상상력을 자극하는 환상적인 판타지 영화', '2023-09-01', 7.7)," +
                          "('노래', 125, 'B', '이철', '전혜진', '가족', '가족과 함께 보기 좋은 따뜻한 가족 영화', '2023-10-01', 8.6)," +
                          "('신비', 135, 'A', '임빈', '박동빈', '미스터리', '흥미진진한 미스터리 영화', '2023-11-01', 6.9)," +
                          "('서스펜스', 115, 'A', '조은미', '전도연', '스릴러', '긴장감과 반전이 있는 스릴러 영화', '2023-12-01', 5.8)," +
                          "('무야호', 125, 'A', '강건모', '김민성', '코미디', '스트레스를 풀고 싶을 때 추천하는 영화', '2023-11-01', 6.8);");

             stmt.execute("CREATE TABLE theater (" +
                          "theater_id VARCHAR(50) NOT NULL," +
                          "theater_availability TINYINT(1) NULL," +
                          "row_seat INT NULL," +
                          "column_seat INT NULL," +
                          "total_seat INT NULL," +
                          "PRIMARY KEY (theater_id)" +
                          ");");

             stmt.execute("INSERT INTO theater (theater_id, theater_availability, row_seat, column_seat, total_seat) VALUES" +
                          "('A', 1, 10, 10, 100)," +
                          "('B', 1, 10, 12, 120)," +
                          "('C', 1, 8, 8, 64)," +
                          "('D', 1, 8, 10, 80)," +
                          "('E', 1, 6, 8, 48)," +
                          "('F', 1, 7, 9, 63)," +
                          "('G', 1, 9, 11, 99)," +
                          "('H', 1, 10, 10, 100)," +
                          "('I', 1, 8, 12, 96)," +
                          "('J', 1, 6, 10, 60)," +
                          "('K', 1, 7, 11, 77)," +
                          "('L', 1, 9, 9, 81)," +
                          "('M', 1, 10, 8, 80)," +
                          "('N', 1, 8, 10, 80)," +
                          "('O', 1, 7, 12, 84);");

             stmt.execute("CREATE TABLE schedule (" +
                          "schedule_id INT NOT NULL AUTO_INCREMENT," +
                          "created_at DATETIME NULL," +
                          "screening_day VARCHAR(255) NULL," +
                          "screening_count INT NULL," +
                          "start_time DATETIME NULL," +
                          "end_time DATETIME NULL," +
                          "movie_id INT NOT NULL," +
                          "theater_id VARCHAR(50) NOT NULL," +
                          "PRIMARY KEY (schedule_id)," +
                          "FOREIGN KEY (movie_id) REFERENCES movie(movie_id) ON DELETE CASCADE," +
                          "FOREIGN KEY (theater_id) REFERENCES theater(theater_id)" +
                          ");");

             stmt.execute("INSERT INTO schedule (created_at, screening_day, screening_count, start_time, end_time, movie_id, theater_id) VALUES" +
                          "('2024-01-01', '월요일', 2, '2024-02-01 10:00:00', DATE_ADD('2024-02-01 10:00:00', INTERVAL 120 MINUTE), 1, 'A')," +
                          "('2024-01-02', '화요일', 3, '2024-02-02 13:00:00', DATE_ADD('2024-02-02 13:00:00', INTERVAL 110 MINUTE), 2, 'B')," +
                          "('2024-01-03', '수요일', 4, '2024-03-01 09:00:00', DATE_ADD('2024-03-01 09:00:00', INTERVAL 180 MINUTE), 3, 'C')," +
                          "('2024-01-04', '목요일', 5, '2024-04-01 11:00:00', DATE_ADD('2024-04-01 11:00:00', INTERVAL 95 MINUTE), 4, 'A')," +
                          "('2024-01-05', '금요일', 2, '2024-05-01 15:00:00', DATE_ADD('2024-05-01 15:00:00', INTERVAL 130 MINUTE), 5, 'B')," +
                          "('2024-01-01', '월요일', 2, '2024-02-01 15:00:00', DATE_ADD('2024-02-01 15:00:00', INTERVAL 105 MINUTE), 6, 'D')," +
                          "('2024-01-02', '화요일', 3, '2024-02-02 09:00:00', DATE_ADD('2024-02-02 09:00:00', INTERVAL 140 MINUTE), 7, 'F')," +
                          "('2024-01-03', '수요일', 4, '2024-03-01 17:00:00', DATE_ADD('2024-03-01 17:00:00', INTERVAL 175 MINUTE), 8, 'C')," +
                          "('2024-01-04', '목요일', 5, '2024-04-01 18:00:00', DATE_ADD('2024-04-01 18:00:00', INTERVAL 100 MINUTE), 9, 'E')," +
                          "('2024-01-05', '금요일', 2, '2024-05-01 10:00:00', DATE_ADD('2024-05-01 10:00:00', INTERVAL 125 MINUTE), 10, 'B')," +
                          "('2024-01-01', '월요일', 2, '2024-02-01 20:00:00', DATE_ADD('2024-02-01 20:00:00', INTERVAL 135 MINUTE), 11, 'A')," +
                          "('2024-01-02', '화요일', 3, '2024-02-02 18:00:00', DATE_ADD('2024-02-02 18:00:00', INTERVAL 115 MINUTE), 12, 'B')," +
                          "('2024-01-08', '월요일', 3, '2024-02-01 10:00:00', DATE_ADD('2024-02-01 10:00:00', INTERVAL 120 MINUTE), 1, 'F')," +
                          "('2024-01-09', '화요일', 4, '2024-02-02 13:00:00', DATE_ADD('2024-02-02 13:00:00', INTERVAL 110 MINUTE), 2, 'D')," +
                          "('2024-01-10', '수요일', 5, '2024-03-01 09:00:00', DATE_ADD('2024-03-01 09:00:00', INTERVAL 180 MINUTE), 3, 'C')," +
                          "('2024-01-11', '목요일', 6, '2024-04-01 11:00:00', DATE_ADD('2024-04-01 11:00:00', INTERVAL 95 MINUTE), 4, 'A')," +
                          "('2024-01-12', '금요일', 3, '2024-05-01 15:00:00', DATE_ADD('2024-05-01 15:00:00', INTERVAL 130 MINUTE), 5, 'D')," +
                          "('2024-01-08', '월요일', 3, '2024-02-01 15:00:00', DATE_ADD('2024-02-01 15:00:00', INTERVAL 105 MINUTE), 6, 'A')," +
                          "('2024-01-09', '화요일', 4, '2024-02-01 19:00:00', DATE_ADD('2024-02-01 19:00:00', INTERVAL 140 MINUTE), 7, 'B')," +
                          "('2024-01-10', '수요일', 5, '2024-03-01 14:00:00', DATE_ADD('2024-03-01 14:00:00', INTERVAL 175 MINUTE), 8, 'F')," +
                          "('2024-01-11', '목요일', 6, '2024-04-01 15:00:00', DATE_ADD('2024-04-01 15:00:00', INTERVAL 100 MINUTE), 9, 'A')," +
                          "('2024-01-12', '금요일', 3, '2024-05-01 07:00:00', DATE_ADD('2024-05-01 07:00:00', INTERVAL 125 MINUTE), 10, 'B')," +
                          "('2024-01-08', '월요일', 3, '2024-02-01 21:00:00', DATE_ADD('2024-02-01 21:00:00', INTERVAL 135 MINUTE), 11, 'C')," +
                          "('2024-01-09', '화요일', 4, '2024-02-03 08:00:00', DATE_ADD('2024-02-03 08:00:00', INTERVAL 115 MINUTE), 12, 'B');");

             stmt.execute("CREATE TABLE seat (" +
                          "seat_id INT NOT NULL AUTO_INCREMENT," +
                          "theater_id VARCHAR(50) NOT NULL," +
                          "`row` INT NULL," +
                          "`column` INT NULL," +
                          "seat_availability TINYINT(1) NOT NULL," +
                          "PRIMARY KEY (seat_id)," +
                          "FOREIGN KEY (theater_id) REFERENCES theater(theater_id)" +
                          ");");

             stmt.execute("INSERT INTO seat (theater_id, `row`, `column`, seat_availability) VALUES" +
                          "('A', 1, 1, 1), ('A', 1, 2, 1), ('A', 1, 3, 1), ('A', 1, 4, 1), ('A', 1, 5, 0)," +
                          "('A', 2, 1, 0), ('A', 2, 2, 0), ('A', 2, 3, 1), ('A', 2, 4, 0), ('A', 2, 5, 0)," +
                          "('B', 1, 1, 0), ('B', 1, 2, 1), ('B', 1, 3, 1), ('B', 1, 4, 1), ('B', 1, 5, 1)," +
                          "('B', 2, 1, 1), ('B', 2, 2, 1), ('B', 2, 3, 1), ('B', 2, 4, 0), ('B', 2, 5, 0)," +
                          "('C', 1, 1, 1), ('C', 1, 2, 1), ('C', 1, 3, 1), ('C', 1, 4, 1), ('C', 1, 5, 0)," +
                          "('C', 2, 1, 0), ('C', 2, 2, 0), ('C', 2, 3, 0), ('C', 2, 4, 1), ('C', 2, 5, 0)," +
                          "('D', 1, 1, 0), ('D', 1, 2, 1), ('D', 1, 3, 1), ('D', 1, 4, 0), ('D', 1, 5, 0)," +
                          "('D', 2, 1, 0), ('D', 2, 2, 0), ('D', 2, 3, 1), ('D', 2, 4, 0), ('D', 2, 5, 0)," +
                          "('E', 1, 1, 0), ('E', 1, 2, 0), ('E', 1, 3, 0), ('E', 1, 4, 0), ('E', 1, 5, 0)," +
                          "('E', 2, 1, 0), ('E', 2, 2, 0), ('E', 2, 3, 1), ('E', 2, 4, 0), ('E', 2, 5, 0)," +
                          "('F', 1, 1, 0), ('F', 1, 2, 0), ('F', 1, 3, 0), ('F', 1, 4, 0), ('F', 1, 5, 1)," +
                          "('F', 2, 1, 1), ('F', 2, 2, 1), ('F', 2, 3, 0), ('F', 2, 4, 0), ('F', 2, 5, 0)," +
                          "('G', 1, 1, 0), ('G', 1, 2, 0), ('G', 1, 3, 0), ('G', 1, 4, 0), ('G', 1, 5, 0)," +
                          "('G', 2, 1, 0), ('G', 2, 2, 0), ('G', 2, 3, 0), ('G', 2, 4, 0), ('G', 2, 5, 0);");

             stmt.execute("CREATE TABLE member (" +
                          "member_id INT NOT NULL AUTO_INCREMENT," +
                          "name VARCHAR(255) NOT NULL," +
                          "phonenumber VARCHAR(255) NOT NULL," +
                          "email VARCHAR(255) NOT NULL," +
                          "role VARCHAR(255) NOT NULL," +
                          "PRIMARY KEY (member_id)" +
                          ");");

             stmt.execute("INSERT INTO member (name, phonenumber, email, role) VALUES" +
                          "('root', '01073947182', 'admin@example.com', 'ADMIN')," +
                          "('김철수', '01012345671', 'chulsoo@example.com', 'USER')," +
                          "('이영희', '01012345672', 'younghee@example.com', 'USER')," +
                          "('박민수', '01012345673', 'minsoo@example.com', 'USER')," +
                          "('최지은', '01012345674', 'jieun@example.com', 'USER')," +
                          "('정현우', '01012345675', 'hyunwoo@example.com', 'USER')," +
                          "('문수진', '01012345676', 'sujin@example.com', 'USER')," +
                          "('장준혁', '01012345677', 'junhyuk@example.com', 'USER')," +
                          "('서하나', '01012345678', 'hana@example.com', 'USER')," +
                          "('윤세준', '01012345679', 'sejun@example.com', 'USER')," +
                          "('한지민', '01012345670', 'jimin@example.com', 'USER')," +
                          "('백은우', '01012345681', 'eunwoo@example.com', 'USER')," +
                          "('황지영', '01012345682', 'jieyong@example.com', 'USER');");

             stmt.execute("CREATE TABLE reservation (" +
                          "reservation_id INT NOT NULL AUTO_INCREMENT," +
                          "payment_method VARCHAR(255) NOT NULL," +
                          "payment_amount INT NOT NULL," +
                          "payment_status VARCHAR(255) NOT NULL," +
                          "payment_date DATETIME NOT NULL," +
                          "member_id INT NOT NULL," +
                          "PRIMARY KEY (reservation_id)," +
                          "FOREIGN KEY (member_id) REFERENCES member(member_id)" +
                          ");");

             stmt.execute("INSERT INTO reservation (member_id, payment_method, payment_amount, payment_date, payment_status) VALUES" +
                          "(2, '카드 결제', 12000, '2024-01-18 10:00:00', 'CONFIRMED')," +
                          "(3, '휴대폰 결제', 15000, '2024-02-03 11:00:00', 'PENDING')," +
                          "(4, '현금', 8000, '2024-03-04 12:00:00', 'CONFIRMED')," +
                          "(5, '적립금 결제', 8500, '2024-04-05 13:00:00', 'PENDING')," +
                          "(6, '카드 결제', 12500, '2024-01-06 14:00:00', 'CONFIRMED')," +
                          "(7, '현금', 15000, '2024-01-07 15:00:00', 'CONFIRMED')," +
                          "(8, '휴대폰 결제', 9000, '2024-02-08 16:00:00', 'PENDING')," +
                          "(9, '현금', 8400, '2024-03-09 17:00:00', 'CONFIRMED')," +
                          "(10, '적립금 결제', 14500, '2024-04-10 18:00:00', 'CONFIRMED')," +
                          "(11, '카드 결제', 9900, '2024-01-11 19:00:00', 'PENDING')," +
                          "(12, '현금', 13500, '2024-01-12 20:00:00', 'CONFIRMED')," +
                          "(13, '현금', 10000, '2024-01-15 09:00:00', 'CONFIRMED')," +
                          "(2, '카드 결제', 12000, '2024-01-17 10:00:00', 'CONFIRMED')," +
                          "(3, '적립금 결제', 14000, '2024-03-28 11:00:00', 'CONFIRMED')," +
                          "(4, '현금', 14000, '2024-03-04 12:00:00', 'CONFIRMED')," +
                          "(5, '적립금 결제', 17000, '2024-02-05 13:00:00', 'PENDING')," +
                          "(6, '휴대폰 결제', 16000, '2024-01-28 14:00:00', 'CONFIRMED')," +
                          "(7, '카드 결제', 12000, '2024-01-10 15:00:00', 'CONFIRMED')," +
                          "(8, '휴대폰 결제', 13200, '2024-04-08 16:00:00', 'PENDING')," +
                          "(9, '카드 결제', 13000, '2024-03-09 17:00:00', 'CONFIRMED')," +
                          "(10, '휴대폰 결제', 14000, '2024-02-10 18:00:00', 'PENDING')," +
                          "(11, '휴대폰 결제', 13800, '2024-01-11 19:00:00', 'CONFIRMED')," +
                          "(12, '현금', 15600, '2024-01-12 20:00:00', 'PENDING')," +
                          "(13, '휴대폰 결제', 15000, '2024-01-14 09:00:00', 'CONFIRMED');");

             stmt.execute("CREATE TABLE ticket (" +
                          "ticket_id INT NOT NULL AUTO_INCREMENT," +
                          "ticket_availibility TINYINT(1) NOT NULL," +
                          "standard_price INT NOT NULL," +
                          "sale_price INT NOT NULL," +
                          "seat_id INT NOT NULL," +
                          "reservation_id INT NOT NULL," +
                          "schedule_id INT NOT NULL," +
                          "theater_id VARCHAR(50) NOT NULL," +
                          "PRIMARY KEY (ticket_id)," +
                          "FOREIGN KEY (reservation_id) REFERENCES reservation(reservation_id) ON DELETE CASCADE," +
                          "FOREIGN KEY (schedule_id) REFERENCES schedule(schedule_id) ON DELETE CASCADE," +
                          "FOREIGN KEY (seat_id) REFERENCES seat(seat_id)," +
                          "FOREIGN KEY (theater_id) REFERENCES theater(theater_id)" +
                          ");");

             stmt.execute("INSERT INTO ticket (reservation_id, ticket_availibility, standard_price, sale_price, schedule_id, seat_id, theater_id) VALUES" +
                          "(1, 1, 15000, 10000, 1, 1, 'A')," +
                          "(2, 1, 15000, 12000, 2, 12, 'B')," +
                          "(3, 0, 15000, 15000, 3, 23, 'C')," +
                          "(4, 1, 15000, 8000, 4, 8, 'A')," +
                          "(5, 0, 15000, 8500, 5, 18, 'B')," +
                          "(6, 1, 15000, 12500, 6, 32, 'D')," +
                          "(7, 1, 15000, 15000, 7, 55, 'F')," +
                          "(8, 0, 15000, 9000, 8, 24, 'C')," +
                          "(9, 1, 15000, 8400, 9, 48, 'E')," +
                          "(10, 1, 15000, 14500, 10, 17, 'B')," +
                          "(11, 0, 15000, 9000, 11, 29, 'C')," +
                          "(12, 1, 15000, 13500, 12, 13, 'B')," +
                          "(13, 1, 15000, 15000, 24, 15, 'B')," +
                          "(14, 1, 15000, 12000, 23, 22, 'C')," +
                          "(15, 1, 15000, 14000, 22, 14, 'B')," +
                          "(16, 1, 15000, 14000, 21, 4, 'A')," +
                          "(17, 0, 17000, 17000, 20, 57, 'F')," +
                          "(18, 1, 16000, 16000, 19, 16, 'B')," +
                          "(19, 1, 15000, 12000, 18, 2, 'A')," +
                          "(20, 0, 15000, 13200, 17, 33, 'D')," +
                          "(21, 1, 15000, 13000, 16, 3, 'A')," +
                          "(22, 0, 15000, 14000, 15, 21, 'C')," +
                          "(23, 1, 15000, 13000, 14, 38, 'D')," +
                          "(24, 0, 15000, 15600, 13, 56, 'F');");

            JOptionPane.showMessageDialog(null, "데이터베이스 초기화 성공.");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "데이터베이스 초기화 실패: " + e.getMessage());
        }
    }
  
    
    ////////////////[관리]
    private static void showAdminBoard() {
        JFrame adminFrame = new JFrame("Admin Board");
        adminFrame.setSize(1800, 600);
        adminFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JPanel adminPanel = new JPanel();
        adminPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        String[] buttonLabels = {"전체조회(새로고침)", "입력(추가)", "삭제", "변경", "초기화"};

        for (int i = 0; i < buttonLabels.length; i++) {
            JButton button = new JButton(buttonLabels[i]);
            button.setPreferredSize(new Dimension(120, 40));
            gbc.gridx = i;
            gbc.gridy = 0;
            adminPanel.add(button, gbc);

            if ("전체조회(새로고침)".equals(buttonLabels[i])) {
                button.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        showAllTables(adminFrame);
                    }
                });
            } else if ("입력(추가)".equals(buttonLabels[i])) {
                button.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        showCreateEntityDialog();
                    }
                });
            }else if ("삭제".equals(buttonLabels[i])) {
                button.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        showDeleteEntityDialog();
                    }
                });
            }
            else if ("변경".equals(buttonLabels[i])) {
                button.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                    	showUpdateEntityDialog();
                    }
                });
            }
         // 초기화 버튼 추가
            else if ("초기화".equals(buttonLabels[i])) {
                button.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        initsamplecode();
                    }
                });
            }
        }

        adminFrame.getContentPane().add(adminPanel, BorderLayout.NORTH);
        adminFrame.setVisible(true);
    }
    //[통신] 테이블 삭제 화면  
    private static void showDeleteEntityDialog() {
        JFrame deleteEntityFrame = new JFrame("데이터 삭제");
        deleteEntityFrame.setSize(700, 700);
        deleteEntityFrame.setLayout(new BorderLayout());

        JPanel radioPanel = new JPanel();
        radioPanel.setLayout(new FlowLayout());

        String[] entities = {"member", "movie", "reservation", "schedule", "seat", "theater", "ticket"};
        ButtonGroup group = new ButtonGroup();
        for (String entity : entities) {
            JRadioButton radioButton = new JRadioButton(entity);
            group.add(radioButton);
            radioPanel.add(radioButton);

            radioButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    showEntityInputFieldsDelete(deleteEntityFrame, entity);
                }
            });
        }

        deleteEntityFrame.add(radioPanel, BorderLayout.NORTH);
        deleteEntityFrame.setVisible(true);
    }
    //[통신] 삭제 테이블 칼럼 데이터 넣는 화
    private static void showEntityInputFieldsDelete(JFrame frame, String entity) {
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridLayout(0, 2));

        switch (entity) {
            case "member":
                inputPanel.add(new JLabel("회원 ID:"));
                JTextField memberIdText = new JTextField(20);
                inputPanel.add(memberIdText);

                JButton deleteMemberButton = new JButton("삭제");
                deleteMemberButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        if (deleteMember(Integer.parseInt(memberIdText.getText()))) {
                            JOptionPane.showMessageDialog(frame, "delete_member 성공", "성공", JOptionPane.INFORMATION_MESSAGE);
                            frame.dispose();
                            showAllTables(null);
                        } else {
                            JOptionPane.showMessageDialog(frame, "회원 정보 삭제 실패", "오류", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                });
                inputPanel.add(new JLabel());
                inputPanel.add(deleteMemberButton);
                break;

            case "movie":
                inputPanel.add(new JLabel("회원 ID:"));
                JTextField memberIdForMovieText = new JTextField(20);
                inputPanel.add(memberIdForMovieText);

                inputPanel.add(new JLabel("영화 ID:"));
                JTextField movieIdText = new JTextField(20);
                inputPanel.add(movieIdText);

                JButton deleteMovieButton = new JButton("삭제");
                deleteMovieButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        if (deleteMovie(Integer.parseInt(memberIdForMovieText.getText()), Integer.parseInt(movieIdText.getText()))) {
                            JOptionPane.showMessageDialog(frame, "delete_movie 성공", "성공", JOptionPane.INFORMATION_MESSAGE);
                            frame.dispose();
                            showAllTables(null);
                        } else {
                            JOptionPane.showMessageDialog(frame, "영화 정보 삭제 실패", "오류", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                });
                inputPanel.add(new JLabel());
                inputPanel.add(deleteMovieButton);
                break;

            case "theater":
                inputPanel.add(new JLabel("상영관 ID:"));
                JTextField theaterIdText = new JTextField(20);
                inputPanel.add(theaterIdText);

                JButton deleteTheaterButton = new JButton("삭제");
                deleteTheaterButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        if (deleteTheater(theaterIdText.getText())) {
                            JOptionPane.showMessageDialog(frame, "delete_theater 성공", "성공", JOptionPane.INFORMATION_MESSAGE);
                            frame.dispose();
                            showAllTables(null);
                        } else {
                            JOptionPane.showMessageDialog(frame, "상영관 정보 삭제 실패", "오류", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                });
                inputPanel.add(new JLabel());
                inputPanel.add(deleteTheaterButton);
                break;

            case "ticket":

                inputPanel.add(new JLabel("티켓 ID:"));
                JTextField ticketIdText = new JTextField(20);
                inputPanel.add(ticketIdText);

                JButton deleteTicketButton = new JButton("삭제");
                deleteTicketButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        if (deleteTicket(Integer.parseInt(ticketIdText.getText()))) {
                            JOptionPane.showMessageDialog(frame, "delete_ticket 성공", "성공", JOptionPane.INFORMATION_MESSAGE);
                            frame.dispose();
                            showAllTables(null);
                        } else {
                            JOptionPane.showMessageDialog(frame, "티켓 정보 삭제 실패", "오류", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                });
                inputPanel.add(new JLabel());
                inputPanel.add(deleteTicketButton);
                break;

            case "reservation":
                inputPanel.add(new JLabel("예약 ID:"));
                JTextField reservationIdText = new JTextField(20);
                inputPanel.add(reservationIdText);

                JButton deleteReservationButton = new JButton("삭제");
                deleteReservationButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        if (deleteReservation(Integer.parseInt(reservationIdText.getText()))) {
                            JOptionPane.showMessageDialog(frame, "delete_reservation 성공", "성공", JOptionPane.INFORMATION_MESSAGE);
                            frame.dispose();
                            showAllTables(null);
                        } else {
                            JOptionPane.showMessageDialog(frame, "예약 정보 삭제 실패", "오류", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                });
                inputPanel.add(new JLabel());
                inputPanel.add(deleteReservationButton);
                break;

            case "seat":
                inputPanel.add(new JLabel("좌석 ID:"));
                JTextField seatIdText = new JTextField(20);
                inputPanel.add(seatIdText);

                JButton deleteSeatButton = new JButton("삭제");
                deleteSeatButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        if (deleteSeat(Integer.parseInt(seatIdText.getText()))) {
                            JOptionPane.showMessageDialog(frame, "delete_seat 성공", "성공", JOptionPane.INFORMATION_MESSAGE);
                            frame.dispose();
                            showAllTables(null);
                        } else {
                            JOptionPane.showMessageDialog(frame, "좌석 정보 삭제 실패", "오류", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                });
                inputPanel.add(new JLabel());
                inputPanel.add(deleteSeatButton);
                break;

            case "schedule":

                inputPanel.add(new JLabel("스케줄 ID:"));
                JTextField scheduleIdText = new JTextField(20);
                inputPanel.add(scheduleIdText);

                JButton deleteScheduleButton = new JButton("삭제");
                deleteScheduleButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        if (deleteSchedule(Integer.parseInt(scheduleIdText.getText()))) {
                            JOptionPane.showMessageDialog(frame, "delete_schedule 성공", "성공", JOptionPane.INFORMATION_MESSAGE);
                            frame.dispose();
                            showAllTables(null);
                        } else {
                            JOptionPane.showMessageDialog(frame, "스케줄 정보 삭제 실패", "오류", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                });
                inputPanel.add(new JLabel());
                inputPanel.add(deleteScheduleButton);
                break;                
                
            default:
                break;
        }

        frame.getContentPane().removeAll();
        frame.add(inputPanel, BorderLayout.CENTER);
        frame.revalidate();
        frame.repaint();
    }
    //[통신]member table 삭제 
    private static boolean deleteMember(int memberId) {
        try {
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/db2?serverTimezone=UTC", "root", "1234");
            CallableStatement stmt = conn.prepareCall("{CALL DeleteMember(?)}");
            stmt.setInt(1, memberId);

            stmt.execute();
            System.out.println("회원 삭제: " + memberId);
            return true;
        } catch (SQLException ex) {
            System.out.println("SQLException: " + ex.getMessage());
            return false;
        }
    }
    //[통신]Movie table 삭제 
    private static boolean deleteMovie(int memberId, int movieId) {
        try {
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/db2?serverTimezone=UTC", "root", "1234");
            CallableStatement stmt = conn.prepareCall("{CALL DeleteMovie(?, ?)}");
            stmt.setInt(1, memberId);
            stmt.setInt(2, movieId);

            stmt.execute();
            System.out.println("영화 삭제: " + movieId);
            return true;
        } catch (SQLException ex) {
            String message = ex.getMessage();
            System.out.println("SQLException: " + message);

            // Display the error message in a Swing dialog
            SwingUtilities.invokeLater(() -> {
                JOptionPane.showMessageDialog(null, message, "Error", JOptionPane.ERROR_MESSAGE);
            });

            return false;
        }
    }   //[통신]Theater table 삭제
    //[통신]Theater table 삭제
    private static boolean deleteTheater(String theaterId) {
        // Check if the theaterId contains only alphabetic characters
        if (!theaterId.matches("[a-zA-Z]+")) {
            // Display a warning dialog
            SwingUtilities.invokeLater(() -> {
                JOptionPane.showMessageDialog(null, "알파벳을 입력해주세요", "Invalid Input", JOptionPane.WARNING_MESSAGE);
            });
            return false;
        }

        try {
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/db2?serverTimezone=UTC", "root", "1234");
            CallableStatement stmt = conn.prepareCall("{CALL DeleteTheater('root', ?)}");
            stmt.setString(1, theaterId);

            boolean hasResultSet = stmt.execute();
            if (hasResultSet) {
                ResultSet rs = stmt.getResultSet();
                if (rs.next()) {
                    String message = rs.getString("Message");
                    System.out.println(message);

                    SwingUtilities.invokeLater(() -> {
                        JOptionPane.showMessageDialog(null, message, "Message", JOptionPane.INFORMATION_MESSAGE);
                    });
                }
            }
            return true;
        } catch (SQLException ex) {
            String errorMessage = ex.getMessage();
            System.out.println("SQLException: " + errorMessage);

            SwingUtilities.invokeLater(() -> {
                JOptionPane.showMessageDialog(null, errorMessage, "Error", JOptionPane.ERROR_MESSAGE);
            });

            return false;
        }
    }

    //[통신]Reservation table 삭제
    private static boolean deleteReservation(int reservationId) {
        try {
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/db2?serverTimezone=UTC", "root", "1234");
            CallableStatement stmt = conn.prepareCall("{CALL DeleteReservation('root', ?)}");
            stmt.setInt(1, reservationId);

            boolean hasResultSet = stmt.execute();
            if (hasResultSet) {
                ResultSet rs = stmt.getResultSet();
                if (rs.next()) {
                    String message = rs.getString("Message");
                    System.out.println(message);

                    SwingUtilities.invokeLater(() -> {
                        JOptionPane.showMessageDialog(null, message, "Message", JOptionPane.INFORMATION_MESSAGE);
                    });
                }
            }
            return true;
        } catch (SQLException ex) {
            String errorMessage = ex.getMessage();
            System.out.println("SQLException: " + errorMessage);

            SwingUtilities.invokeLater(() -> {
                JOptionPane.showMessageDialog(null, errorMessage, "Error", JOptionPane.ERROR_MESSAGE);
            });

            return false;
        }
    }
    //[통신]Seat table 삭제
    private static boolean deleteSeat(int seatId) {
        try {
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/db2?serverTimezone=UTC", "root", "1234");
            CallableStatement stmt = conn.prepareCall("{CALL DeleteSeat('root', ?)}");
            stmt.setInt(1, seatId);

            stmt.execute();
            System.out.println("좌석 삭제: " + seatId);
            return true;
        } catch (SQLException ex) {
            System.out.println("SQLException: " + ex.getMessage());
            return false;
        }
    }

	// [통신]Schedule table 삭제
	private static boolean deleteSchedule(int scheduleId) {
	    try {
	        Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/db2?serverTimezone=UTC", "root", "1234");
	        CallableStatement stmt = conn.prepareCall("{CALL deleteSchedule('root', ?)}");
	        stmt.setInt(1, scheduleId);
	
	        stmt.execute();
	        System.out.println("스케줄 삭제: " + scheduleId);
	        return true;
	    } catch (SQLException ex) {
	        System.out.println("SQLException: " + ex.getMessage());
	        return false;
	    }
	}
	
	// [통신]Ticket table 삭제
	private static boolean deleteTicket(int ticketId) {
	    try {
	        Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/db2?serverTimezone=UTC", "root", "1234");
	        CallableStatement stmt = conn.prepareCall("{CALL deleteTicket('root', ?, ?)}");
	        stmt.setInt(1, ticketId);
	        stmt.registerOutParameter(2, Types.INTEGER);
	
	        stmt.execute();
	        int deletedTicketId = stmt.getInt(2);
	
	        if (deletedTicketId != 0) {
	            System.out.println("티켓 삭제: " + ticketId);
	            return true;
	        } else {
	            return false;
	        }
	    } catch (SQLException ex) {
	        System.out.println("SQLException: " + ex.getMessage());
	        return false;
	    }
	}

    
    //[통신]입력 테이블 선택 화면 
    private static void showCreateEntityDialog() {
        JFrame createEntityFrame = new JFrame("데이터 추가");
        createEntityFrame.setSize(700, 700);
        createEntityFrame.setLayout(new BorderLayout());

        JPanel radioPanel = new JPanel();
        radioPanel.setLayout(new FlowLayout());

        String[] entities = {"member", "movie", "reservation", "schedule", "seat", "theater", "ticket"};
        ButtonGroup group = new ButtonGroup();
        for (String entity : entities) {
            JRadioButton radioButton = new JRadioButton(entity);
            group.add(radioButton);
            radioPanel.add(radioButton);

            radioButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    showEntityInputFields(createEntityFrame, entity);
                }
            });
        }

        createEntityFrame.add(radioPanel, BorderLayout.NORTH);
        createEntityFrame.setVisible(true);
    }
    //[통신] 입력 테이블 칼럼 데이터 넣는 화면 
    private static void showEntityInputFields(JFrame frame, String entity) {
    JPanel inputPanel = new JPanel();
    inputPanel.setLayout(new GridLayout(0, 2));

    switch (entity) {
        case "member":
            inputPanel.add(new JLabel("이름:"));
            JTextField nameText = new JTextField(20);
            inputPanel.add(nameText);

            inputPanel.add(new JLabel("전화번호:"));
            JTextField phoneText = new JTextField(20);
            inputPanel.add(phoneText);

            inputPanel.add(new JLabel("이메일:"));
            JTextField emailText = new JTextField(20);
            inputPanel.add(emailText);

            JButton memberSubmitButton = new JButton("입력");
            memberSubmitButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    if (createMember(nameText.getText(), phoneText.getText(), emailText.getText())) {
                        JOptionPane.showMessageDialog(frame, "create_member 성공", "성공", JOptionPane.INFORMATION_MESSAGE);
                        frame.dispose();
                        showAllTables(null);
                    } else {
                        JOptionPane.showMessageDialog(frame, "회원 정보 추가 실패", "오류", JOptionPane.ERROR_MESSAGE);
                    }
                }
            });
            inputPanel.add(new JLabel());
            inputPanel.add(memberSubmitButton);
            break;

        case "movie":
            inputPanel.add(new JLabel("영화 이름:"));
            JTextField movieNameText = new JTextField(20);
            inputPanel.add(movieNameText);

            inputPanel.add(new JLabel("상영 시간:"));
            JTextField showtimeText = new JTextField(20);
            inputPanel.add(showtimeText);

            inputPanel.add(new JLabel("등급:"));
            JTextField ratingText = new JTextField(20);
            inputPanel.add(ratingText);

            inputPanel.add(new JLabel("감독:"));
            JTextField directorText = new JTextField(20);
            inputPanel.add(directorText);

            inputPanel.add(new JLabel("배우:"));
            JTextField actorText = new JTextField(20);
            inputPanel.add(actorText);

            inputPanel.add(new JLabel("장르:"));
            JTextField genreText = new JTextField(20);
            inputPanel.add(genreText);

            inputPanel.add(new JLabel("설명:"));
            JTextField instructionText = new JTextField(20);
            inputPanel.add(instructionText);

            inputPanel.add(new JLabel("제작일:"));
            JTextField movieCreatedAtText = new JTextField(20);
            inputPanel.add(movieCreatedAtText);

            inputPanel.add(new JLabel("평점:"));
            JTextField gradeText = new JTextField(20);
            inputPanel.add(gradeText);

            JButton movieSubmitButton = new JButton("입력");
            movieSubmitButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    if (createMovie(movieNameText.getText(), Integer.parseInt(showtimeText.getText()), ratingText.getText(), 
                                    directorText.getText(), actorText.getText(), genreText.getText(), 
                                    instructionText.getText(), movieCreatedAtText.getText(), Float.parseFloat(gradeText.getText()))) {
                        JOptionPane.showMessageDialog(frame, "create_movie 성공", "성공", JOptionPane.INFORMATION_MESSAGE);
                        frame.dispose();
                        showAllTables(null);
                    } else {
                        JOptionPane.showMessageDialog(frame, "영화 정보 추가 실패", "오류", JOptionPane.ERROR_MESSAGE);
                    }
                }
            });
            inputPanel.add(new JLabel());
            inputPanel.add(movieSubmitButton);
            break;

        case "theater":
            inputPanel.add(new JLabel("상영관 ID:"));
            JTextField theaterIdText = new JTextField(20);
            inputPanel.add(theaterIdText);

            inputPanel.add(new JLabel("상영관 가용성:"));
            JTextField availabilityText = new JTextField(20);
            inputPanel.add(availabilityText);

            inputPanel.add(new JLabel("행 좌석 수:"));
            JTextField rowSeatText = new JTextField(20);
            inputPanel.add(rowSeatText);

            inputPanel.add(new JLabel("열 좌석 수:"));
            JTextField columnSeatText = new JTextField(20);
            inputPanel.add(columnSeatText);

            inputPanel.add(new JLabel("총 좌석 수:"));
            JTextField totalSeatText = new JTextField(20);
            inputPanel.add(totalSeatText);

            JButton theaterSubmitButton = new JButton("입력");
            theaterSubmitButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    try {
                        String theaterId = theaterIdText.getText();
                        boolean availability = Boolean.parseBoolean(availabilityText.getText());
                        int rowSeat = Integer.parseInt(rowSeatText.getText());
                        int columnSeat = Integer.parseInt(columnSeatText.getText());
                        int totalSeat = Integer.parseInt(totalSeatText.getText());

                        if (createTheater(theaterId, availability, rowSeat, columnSeat, totalSeat)) {
                            JOptionPane.showMessageDialog(frame, "create_theater 성공", "성공", JOptionPane.INFORMATION_MESSAGE);
                            frame.dispose();
                            showAllTables(null);
                        } else {
                            JOptionPane.showMessageDialog(frame, "상영관 정보 추가 실패", "오류", JOptionPane.ERROR_MESSAGE);
                        }
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(frame, "유효한 숫자를 입력하세요", "입력 오류", JOptionPane.ERROR_MESSAGE);
                    }
                }
            });


            inputPanel.add(new JLabel());
            inputPanel.add(theaterSubmitButton);
            break;

        case "ticket":
            inputPanel.add(new JLabel("기본 가격:"));
            JTextField standardPriceText = new JTextField(20);
            inputPanel.add(standardPriceText);

            inputPanel.add(new JLabel("좌석 ID:"));
            JTextField seatIdText = new JTextField(20);
            inputPanel.add(seatIdText);

            inputPanel.add(new JLabel("예약 ID:"));
            JTextField reservationIdText = new JTextField(20);
            inputPanel.add(reservationIdText);

            inputPanel.add(new JLabel("스케줄 ID:"));
            JTextField scheduleIdText = new JTextField(20);
            inputPanel.add(scheduleIdText);

            JButton ticketSubmitButton = new JButton("입력");
            ticketSubmitButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    if (createTicket(Integer.parseInt(standardPriceText.getText()), Integer.parseInt(seatIdText.getText()), 
                                      Integer.parseInt(reservationIdText.getText()), Integer.parseInt(scheduleIdText.getText()))) {
                        JOptionPane.showMessageDialog(frame, "create_ticket 성공", "성공", JOptionPane.INFORMATION_MESSAGE);
                        frame.dispose();
                        showAllTables(null);
                    } else {
                        JOptionPane.showMessageDialog(frame, "티켓 정보 추가 실패", "오류", JOptionPane.ERROR_MESSAGE);
                    }
                }
            });
            inputPanel.add(new JLabel());
            inputPanel.add(ticketSubmitButton);
            break;

        case "reservation":
            inputPanel.add(new JLabel("결제 방법:"));
            JTextField paymentMethodText = new JTextField(20);
            inputPanel.add(paymentMethodText);

            inputPanel.add(new JLabel("결제 금액:"));
            JTextField paymentAmountText = new JTextField(20);
            inputPanel.add(paymentAmountText);

            inputPanel.add(new JLabel("결제 상태:"));
            JTextField paymentStatusText = new JTextField(20);
            inputPanel.add(paymentStatusText);

            inputPanel.add(new JLabel("결제 날짜:"));
            JTextField paymentDateText = new JTextField(20);
            inputPanel.add(paymentDateText);

            inputPanel.add(new JLabel("회원 ID:"));
            JTextField memberIdText = new JTextField(20);
            inputPanel.add(memberIdText);

            JButton reservationSubmitButton = new JButton("입력");
            reservationSubmitButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    if (createReservation(paymentMethodText.getText(), Integer.parseInt(paymentAmountText.getText()), 
                                          paymentStatusText.getText(), paymentDateText.getText(), Integer.parseInt(memberIdText.getText()))) {
                        JOptionPane.showMessageDialog(frame, "create_reservation 성공", "성공", JOptionPane.INFORMATION_MESSAGE);
                        frame.dispose();
                        showAllTables(null);
                    } else {
                        JOptionPane.showMessageDialog(frame, "예약 정보 추가 실패", "오류", JOptionPane.ERROR_MESSAGE);
                    }
                }
            });
            inputPanel.add(new JLabel());
            inputPanel.add(reservationSubmitButton);
            break;

        case "seat":
            inputPanel.add(new JLabel("상영관 ID:"));
            JTextField seatTheaterIdText = new JTextField(20);
            inputPanel.add(seatTheaterIdText);

            inputPanel.add(new JLabel("행:"));
            JTextField rowText = new JTextField(20);
            inputPanel.add(rowText);

            inputPanel.add(new JLabel("열:"));
            JTextField columnText = new JTextField(20);
            inputPanel.add(columnText);

            inputPanel.add(new JLabel("좌석 가용성:"));
            JTextField seatAvailabilityText = new JTextField(20);
            inputPanel.add(seatAvailabilityText);

            JButton seatSubmitButton = new JButton("입력");
         seatSubmitButton.addActionListener(new ActionListener() {
    public void actionPerformed(ActionEvent e) {
        try {
            String theaterId = seatTheaterIdText.getText();
            int row = Integer.parseInt(rowText.getText());
            int column = Integer.parseInt(columnText.getText());
            // seatAvailability를 boolean으로 변환
            boolean seatAvailability = Boolean.parseBoolean(seatAvailabilityText.getText());

            if (createSeat(theaterId, row, column, seatAvailability)) {
                JOptionPane.showMessageDialog(frame, "create_seat 성공", "성공", JOptionPane.INFORMATION_MESSAGE);
                frame.dispose();
                showAllTables(null);
            } else {
                JOptionPane.showMessageDialog(frame, "좌석 정보 추가 실패", "오류", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(frame, "유효한 숫자를 입력하세요", "입력 오류", JOptionPane.ERROR_MESSAGE);
        }
    }
});

            inputPanel.add(new JLabel());
            inputPanel.add(seatSubmitButton);
            break;

        case "schedule":
            inputPanel.add(new JLabel("생성 날짜:"));
            JTextField createdAtText = new JTextField(20);
            inputPanel.add(createdAtText);

            inputPanel.add(new JLabel("상영일:"));
            JTextField screeningDayText = new JTextField(20);
            inputPanel.add(screeningDayText);

            inputPanel.add(new JLabel("상영 횟수:"));
            JTextField screeningCountText = new JTextField(20);
            inputPanel.add(screeningCountText);

            inputPanel.add(new JLabel("시작 시간:"));
            JTextField startTimeText = new JTextField(20);
            inputPanel.add(startTimeText);

            inputPanel.add(new JLabel("영화 ID:"));
            JTextField movieIdText = new JTextField(20);
            inputPanel.add(movieIdText);

            inputPanel.add(new JLabel("상영관 ID:"));
            JTextField scheduleTheaterIdText = new JTextField(20);
            inputPanel.add(scheduleTheaterIdText);

            JButton scheduleSubmitButton = new JButton("입력");
            scheduleSubmitButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    if (createSchedule(createdAtText.getText(), screeningDayText.getText(), Integer.parseInt(screeningCountText.getText()), 
                                       startTimeText.getText(), Integer.parseInt(movieIdText.getText()), scheduleTheaterIdText.getText())) {
                        JOptionPane.showMessageDialog(frame, "create_schedule 성공", "성공", JOptionPane.INFORMATION_MESSAGE);
                        frame.dispose();
                        showAllTables(null);
                    } else {
                        JOptionPane.showMessageDialog(frame, "상영 일정 정보 추가 실패", "오류", JOptionPane.ERROR_MESSAGE);
                    }
                }
            });
            inputPanel.add(new JLabel());
            inputPanel.add(scheduleSubmitButton);
            break;

        default:
            break;
    }

    frame.getContentPane().removeAll();
    frame.add(inputPanel, BorderLayout.CENTER);
    frame.revalidate();
    frame.repaint();
}
    //[통신]member table 입력(추가) 
    private static boolean createMember(String name, String phone, String email) {
        try {
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/db2?serverTimezone=UTC", "root", "1234");
            CallableStatement stmt = conn.prepareCall("{CALL CreateMember('root', ?, ?, ?, ?)}");
            stmt.setString(1, name);
            stmt.setString(2, phone);
            stmt.setString(3, email);
            stmt.registerOutParameter(4, Types.INTEGER);

            stmt.execute();
            int createdUserId = stmt.getInt(4);

            if (createdUserId != 0) {
                System.out.println("새로운 회원 생성: " + createdUserId);
                return true;
            } else {
                return false;
            }
        } catch (SQLException ex) {
            System.out.println("SQLException: " + ex.getMessage());
            return false;
        }
    }
    //[통신]movie table 입력(추가) 
    private static boolean createMovie(String name, int showtime, String rating, String director, String actor, String genre, String instruction, String createdAt, float grade) {
        try {
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/db2?serverTimezone=UTC", "root", "1234");
            CallableStatement stmt = conn.prepareCall("{CALL CreateMovie('root', ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}");
            stmt.setString(1, name);
            stmt.setInt(2, showtime);
            stmt.setString(3, rating);
            stmt.setString(4, director);
            stmt.setString(5, actor);
            stmt.setString(6, genre);
            stmt.setString(7, instruction);
            stmt.setString(8, createdAt);
            stmt.setFloat(9, grade);
            stmt.registerOutParameter(10, Types.INTEGER);

            stmt.execute();
            int createdMovieId = stmt.getInt(10);

            if (createdMovieId != 0) {
                System.out.println("새로운 영화 생성: " + createdMovieId);
                return true;
            } else {
                return false;
            }
        } catch (SQLException ex) {
            System.out.println("SQLException: " + ex.getMessage());
            return false;
        }
    }
    //[통신]Theater table 입력(추가) 
    private static boolean createTheater(String theaterId, boolean availability, int rowSeat, int columnSeat, int totalSeat) {
        try {
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/db2?serverTimezone=UTC", "root", "1234");
            CallableStatement stmt = conn.prepareCall("{CALL CreateTheater('root', ?, ?, ?, ?, ?)}");
            stmt.setString(1, theaterId);
            stmt.setInt(2, availability ? 1 : 0); // boolean 값을 tinyint로 변환
            stmt.setInt(3, rowSeat);
            stmt.setInt(4, columnSeat);
            stmt.setInt(5, totalSeat);

            stmt.execute();
            System.out.println("새로운 상영관 생성: " + theaterId);
            return true;
        } catch (SQLException ex) {
            System.out.println("SQLException: " + ex.getMessage());
            return false;
        }
    }
    //[통신]Ticket table 입력(추가) 
    private static boolean createTicket(int standardPrice, int seatId, int reservationId, int scheduleId) {
        try {
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/db2?serverTimezone=UTC", "root", "1234");
            CallableStatement stmt = conn.prepareCall("{CALL CreateTicket('root', ?, ?, ?, ?, ?)}");
            stmt.setInt(1, standardPrice);
            stmt.setInt(2, seatId);
            stmt.setInt(3, reservationId);
            stmt.setInt(4, scheduleId);
            stmt.registerOutParameter(5, Types.INTEGER);

            stmt.execute();
            int createdTicketId = stmt.getInt(5);

            if (createdTicketId != 0) {
                System.out.println("새로운 티켓 생성: " + createdTicketId);
                return true;
            } else {
                return false;
            }
        } catch (SQLException ex) {
            System.out.println("SQLException: " + ex.getMessage());
            return false;
        }
    }
    //[통신]reservation table 입력(추가) 
    private static boolean createReservation(String paymentMethod, int paymentAmount, String paymentStatus, String paymentDate, int memberId) {
    try {
        Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/db2?serverTimezone=UTC", "root", "1234");
        CallableStatement stmt = conn.prepareCall("{CALL CreateReservation('root', ?, ?, ?, ?, ?, ?)}");
        stmt.setString(1, paymentMethod);
        stmt.setInt(2, paymentAmount);
        stmt.setString(3, paymentStatus);
        stmt.setString(4, paymentDate);
        stmt.setInt(5, memberId);
        stmt.registerOutParameter(6, Types.INTEGER);

        stmt.execute();
        int createdReservationId = stmt.getInt(6);

        if (createdReservationId != 0) {
            System.out.println("새로운 예약 생성: " + createdReservationId);
            return true;
        } else {
            return false;
        }
    } catch (SQLException ex) {
        System.out.println("SQLException: " + ex.getMessage());
        return false;
    }
}
    //[통신]Seat table 입력(추가) 
    private static boolean createSeat(String theaterId, int row, int column, boolean seatAvailability) {
    try {
        Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/db2?serverTimezone=UTC", "root", "1234");
        CallableStatement stmt = conn.prepareCall("{CALL CreateSeat('root', ?, ?, ?, ?, ?)}");
        stmt.setString(1, theaterId);
        stmt.setInt(2, row);
        stmt.setInt(3, column);
        stmt.setInt(4, seatAvailability ? 1 : 0); // boolean 값을 tinyint로 변환
        stmt.registerOutParameter(5, Types.INTEGER);

        stmt.execute();
        int createdSeatId = stmt.getInt(5);

        if (createdSeatId != 0) {
            System.out.println("새로운 좌석 생성: " + createdSeatId);
            return true;
        } else {
            return false;
        }
    } catch (SQLException ex) {
        System.out.println("SQLException: " + ex.getMessage());
        return false;
    }
}
    //[통신]Schedule table 입력(추가) 
    private static boolean createSchedule(String createdAt, String screeningDay, int screeningCount, String startTime, int movieId, String theaterId) {
    try {
        Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/db2?serverTimezone=UTC", "root", "1234");
        CallableStatement stmt = conn.prepareCall("{CALL CreateSchedule('root', ?, ?, ?, ?, ?, ?, ?)}");
        stmt.setString(1, createdAt);
        stmt.setString(2, screeningDay);
        stmt.setInt(3, screeningCount);
        stmt.setString(4, startTime);
        stmt.setInt(5, movieId);
        stmt.setString(6, theaterId);
        stmt.registerOutParameter(7, Types.INTEGER);

        stmt.execute();
        int createdScheduleId = stmt.getInt(7);

        if (createdScheduleId != 0) {
            System.out.println("새로운 상영 일정 생성: " + createdScheduleId);
            return true;
        } else {
            return false;
        }
    } catch (SQLException ex) {
        System.out.println("SQLException: " + ex.getMessage());
        return false;
    }
}
    // [통신]모든 테이블 불러오기 
    private static void showAllTables(JFrame adminFrame) {
    if (adminFrame == null) {
        adminFrame = new JFrame("Admin Board");
        adminFrame.setSize(1800, 600);
        adminFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    // 기존에 추가된 내용이 있으면 제거, 단 상단 버튼 패널 제외
    Component[] components = adminFrame.getContentPane().getComponents();
    for (Component component : components) {
        if (!(component instanceof JPanel && ((JPanel) component).getComponent(0) instanceof JButton)) {
            adminFrame.getContentPane().remove(component);
        }
    }

    JPanel tablePanel = new JPanel();
    tablePanel.setLayout(new BoxLayout(tablePanel, BoxLayout.X_AXIS));
    JScrollPane scrollPane = new JScrollPane(tablePanel);
    adminFrame.getContentPane().add(scrollPane, BorderLayout.CENTER);

    try {
        Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/db2?serverTimezone=UTC", "root", "1234");

        String dropProcedure = "DROP PROCEDURE IF EXISTS ExecuteAdminAllTablesReadQuery";
        Statement stmt = conn.createStatement();
        stmt.execute(dropProcedure);

        String createProcedure = "CREATE PROCEDURE ExecuteAdminAllTablesReadQuery (IN user_id INT) " +
                "BEGIN " +
                "DECLARE user_role VARCHAR(255); " +
                "SELECT role INTO user_role FROM member WHERE member_id = user_id LIMIT 1; " +
                "IF user_role = 'ADMIN' THEN " +
                "SELECT * FROM member; " +
                "SELECT * FROM movie; " +
                "SELECT * FROM reservation; " +
                "SELECT * FROM schedule; " +
                "SELECT * FROM seat; " +
                "SELECT * FROM theater; " +
                "SELECT * FROM ticket; " +
                "ELSE " +
                "SELECT '관리자만 해당 쿼리를 실행할 수 있습니다.' AS message; " +
                "END IF; " +
                "END";

        stmt.execute(createProcedure);

        CallableStatement cstmt = conn.prepareCall("{CALL ExecuteAdminAllTablesReadQuery(?)}");
        cstmt.setInt(1, 1);

        boolean hasResults = cstmt.execute();
        while (hasResults) {
            ResultSet rs = cstmt.getResultSet();
            ResultSetMetaData rsmd = rs.getMetaData();
            int columnsNumber = rsmd.getColumnCount();
            String tableName = rsmd.getTableName(1);

            DefaultTableModel tableModel = new DefaultTableModel();
            JTable table = new JTable(tableModel);

            tableModel.addColumn(tableName);

            for (int i = 1; i <= columnsNumber; i++) {
                tableModel.addColumn(rsmd.getColumnName(i));
            }

            while (rs.next()) {
                Object[] rowData = new Object[columnsNumber + 1];
                rowData[0] = rs.getRow();
                for (int i = 1; i <= columnsNumber; i++) {
                    rowData[i] = rs.getObject(i);
                }
                tableModel.addRow(rowData);
            }

            tablePanel.add(new JScrollPane(table));

            hasResults = cstmt.getMoreResults();
        }
    } catch (SQLException ex) {
        JTextArea textArea = new JTextArea("SQLException: " + ex.getMessage(), 20, 70);
        adminFrame.getContentPane().add(new JScrollPane(textArea), BorderLayout.CENTER);
    }

    adminFrame.revalidate();
    adminFrame.repaint();
    adminFrame.setVisible(true);
}

    
    // [통신] 테이블 업데이트 화면
    	private static void showUpdateEntityDialog() {
    	    JFrame updateEntityFrame = new JFrame("데이터 변경");
    	    updateEntityFrame.setSize(700, 700);
    	    updateEntityFrame.setLayout(new BorderLayout());

    	    JPanel radioPanel = new JPanel();
    	    radioPanel.setLayout(new FlowLayout());

    	    String[] entities = {"member", "movie", "reservation", "schedule", "seat", "theater", "ticket"};
    	    ButtonGroup group = new ButtonGroup();
    	    for (String entity : entities) {
    	        JRadioButton radioButton = new JRadioButton(entity);
    	        group.add(radioButton);
    	        radioPanel.add(radioButton);

    	        radioButton.addActionListener(new ActionListener() {
    	            public void actionPerformed(ActionEvent e) {
    	                showEntityInputFieldsUpdate(updateEntityFrame, entity);
    	            }
    	        });
    	    }

    	    updateEntityFrame.add(radioPanel, BorderLayout.NORTH);
    	    updateEntityFrame.setVisible(true);
    	}
	// [통신] 업데이트 테이블 칼럼 데이터 넣는 화면
	private static void showEntityInputFieldsUpdate(JFrame frame, String entity) {
	    JPanel inputPanel = new JPanel();
	    inputPanel.setLayout(new GridLayout(0, 2));
	
	    switch (entity) {
	        case "member":
	            inputPanel.add(new JLabel("회원 ID:"));
	            JTextField memberIdText = new JTextField(20);
	            inputPanel.add(memberIdText);
	
	            inputPanel.add(new JLabel("새 이름:"));
	            JTextField newNameText = new JTextField(20);
	            inputPanel.add(newNameText);
	
	            inputPanel.add(new JLabel("새 전화번호:"));
	            JTextField newPhoneText = new JTextField(20);
	            inputPanel.add(newPhoneText);
	
	            inputPanel.add(new JLabel("새 이메일:"));
	            JTextField newEmailText = new JTextField(20);
	            inputPanel.add(newEmailText);
	
	            JButton updateMemberButton = new JButton("변경");
	            updateMemberButton.addActionListener(new ActionListener() {
	                public void actionPerformed(ActionEvent e) {
	                    if (updateMember(memberIdText.getText(), newNameText.getText(), newPhoneText.getText(), newEmailText.getText())) {
	                        JOptionPane.showMessageDialog(frame, "update_member 성공", "성공", JOptionPane.INFORMATION_MESSAGE);
	                        frame.dispose();
	                        showAllTables(null);
	                    } else {
	                        JOptionPane.showMessageDialog(frame, "회원 정보 변경 실패", "오류", JOptionPane.ERROR_MESSAGE);
	                    }
	                }
	            });
	            inputPanel.add(new JLabel());
	            inputPanel.add(updateMemberButton);
	            break;
	
	        case "movie":
	            inputPanel.add(new JLabel("영화 ID:"));
	            JTextField movieIdText = new JTextField(20);
	            inputPanel.add(movieIdText);
	
	            inputPanel.add(new JLabel("새 영화 이름:"));
	            JTextField newMovieNameText = new JTextField(20);
	            inputPanel.add(newMovieNameText);
	
	            inputPanel.add(new JLabel("새 상영 시간:"));
	            JTextField newShowtimeText = new JTextField(20);
	            inputPanel.add(newShowtimeText);
	
	            inputPanel.add(new JLabel("새 등급:"));
	            JTextField newRatingText = new JTextField(20);
	            inputPanel.add(newRatingText);
	
	            inputPanel.add(new JLabel("새 감독:"));
	            JTextField newDirectorText = new JTextField(20);
	            inputPanel.add(newDirectorText);
	
	            inputPanel.add(new JLabel("새 배우:"));
	            JTextField newActorText = new JTextField(20);
	            inputPanel.add(newActorText);
	
	            inputPanel.add(new JLabel("새 장르:"));
	            JTextField newGenreText = new JTextField(20);
	            inputPanel.add(newGenreText);
	
	            inputPanel.add(new JLabel("새 설명:"));
	            JTextField newInstructionText = new JTextField(20);
	            inputPanel.add(newInstructionText);
	
	            inputPanel.add(new JLabel("새 제작일:"));
	            JTextField newCreatedAtText = new JTextField(20);
	            inputPanel.add(newCreatedAtText);
	
	            inputPanel.add(new JLabel("새 평점:"));
	            JTextField newGradeText = new JTextField(20);
	            inputPanel.add(newGradeText);
	
	            JButton updateMovieButton = new JButton("변경");
	            updateMovieButton.addActionListener(new ActionListener() {
	                public void actionPerformed(ActionEvent e) {
	                    if (updateMovie(movieIdText.getText(), newMovieNameText.getText(), newShowtimeText.getText(), newRatingText.getText(), 
	                                    newDirectorText.getText(), newActorText.getText(), newGenreText.getText(), 
	                                    newInstructionText.getText(), newCreatedAtText.getText(), newGradeText.getText())) {
	                        JOptionPane.showMessageDialog(frame, "update_movie 성공", "성공", JOptionPane.INFORMATION_MESSAGE);
	                        frame.dispose();
	                        showAllTables(null);
	                    } else {
	                        JOptionPane.showMessageDialog(frame, "영화 정보 변경 실패", "오류", JOptionPane.ERROR_MESSAGE);
	                    }
	                }
	            });
	            inputPanel.add(new JLabel());
	            inputPanel.add(updateMovieButton);
	            break;
	
	        case "reservation":
	            inputPanel.add(new JLabel("예약 ID:"));
	            JTextField reservationIdText = new JTextField(20);
	            inputPanel.add(reservationIdText);
	
	            inputPanel.add(new JLabel("새 결제 방법:"));
	            JTextField newPaymentMethodText = new JTextField(20);
	            inputPanel.add(newPaymentMethodText);
	
	            inputPanel.add(new JLabel("새 결제 금액:"));
	            JTextField newPaymentAmountText = new JTextField(20);
	            inputPanel.add(newPaymentAmountText);
	
	            inputPanel.add(new JLabel("새 결제 상태:"));
	            JTextField newPaymentStatusText = new JTextField(20);
	            inputPanel.add(newPaymentStatusText);
	
	            inputPanel.add(new JLabel("새 회원 ID:"));
	            JTextField newMemberIdText = new JTextField(20);
	            inputPanel.add(newMemberIdText);
	
	            JButton updateReservationButton = new JButton("변경");
	            updateReservationButton.addActionListener(new ActionListener() {
	                public void actionPerformed(ActionEvent e) {
	                    if (updateReservation(reservationIdText.getText(), newPaymentMethodText.getText(), newPaymentAmountText.getText(), 
	                                          newPaymentStatusText.getText(), newMemberIdText.getText())) {
	                        JOptionPane.showMessageDialog(frame, "update_reservation 성공", "성공", JOptionPane.INFORMATION_MESSAGE);
	                        frame.dispose();
	                        showAllTables(null);
	                    } else {
	                        JOptionPane.showMessageDialog(frame, "예약 정보 변경 실패", "오류", JOptionPane.ERROR_MESSAGE);
	                    }
	                }
	            });
	            inputPanel.add(new JLabel());
	            inputPanel.add(updateReservationButton);
	            break;
	
	        case "seat":
	            inputPanel.add(new JLabel("좌석 ID:"));
	            JTextField seatIdText = new JTextField(20);
	            inputPanel.add(seatIdText);
	
	            inputPanel.add(new JLabel("새 행:"));
	            JTextField newRowText = new JTextField(20);
	            inputPanel.add(newRowText);
	
	            inputPanel.add(new JLabel("새 열:"));
	            JTextField newColumnText = new JTextField(20);
	            inputPanel.add(newColumnText);
	
	            inputPanel.add(new JLabel("새 좌석 가용성:"));
	            JTextField newSeatAvailabilityText = new JTextField(20);
	            inputPanel.add(newSeatAvailabilityText);
	
	            JButton updateSeatButton = new JButton("변경");
	            updateSeatButton.addActionListener(new ActionListener() {
	                public void actionPerformed(ActionEvent e) {
	                    if (updateSeat(seatIdText.getText(), newRowText.getText(), newColumnText.getText(), newSeatAvailabilityText.getText())) {
	                        JOptionPane.showMessageDialog(frame, "update_seat 성공", "성공", JOptionPane.INFORMATION_MESSAGE);
	                        frame.dispose();
	                        showAllTables(null);
	                    } else {
	                        JOptionPane.showMessageDialog(frame, "좌석 정보 변경 실패", "오류", JOptionPane.ERROR_MESSAGE);
	                    }
	                }
	            });
	            inputPanel.add(new JLabel());
	            inputPanel.add(updateSeatButton);
	            break;
	
	        case "theater":
	            inputPanel.add(new JLabel("회원 ID:"));
	            JTextField memberIdTextForTheater = new JTextField(20);
	            inputPanel.add(memberIdTextForTheater);
	
	            inputPanel.add(new JLabel("영화 ID:"));
	            JTextField movieIdTextForTheater = new JTextField(20);
	            inputPanel.add(movieIdTextForTheater);
	
	            JButton updateTheaterButton = new JButton("변경");
	            updateTheaterButton.addActionListener(new ActionListener() {
	                public void actionPerformed(ActionEvent e) {
	                    if (updateTheater(memberIdTextForTheater.getText(), movieIdTextForTheater.getText())) {
	                        JOptionPane.showMessageDialog(frame, "update_theater 성공", "성공", JOptionPane.INFORMATION_MESSAGE);
	                        frame.dispose();
	                        showAllTables(null);
	                    } else {
	                        JOptionPane.showMessageDialog(frame, "상영관 정보 변경 실패", "오류", JOptionPane.ERROR_MESSAGE);
	                    }
	                }
	            });
	            inputPanel.add(new JLabel());
	            inputPanel.add(updateTheaterButton);
	            break;
	        case "schedule":

	            inputPanel.add(new JLabel("스케줄 ID:"));
	            JTextField scheduleIdText = new JTextField(20);
	            inputPanel.add(scheduleIdText);

	            inputPanel.add(new JLabel("새 상영일:"));
	            JTextField screeningDayText = new JTextField(20);
	            inputPanel.add(screeningDayText);

	            inputPanel.add(new JLabel("새 상영 횟수:"));
	            JTextField screeningCountText = new JTextField(20);
	            inputPanel.add(screeningCountText);

	            inputPanel.add(new JLabel("새 시작 시간:"));
	            JTextField startTimeText = new JTextField(20);
	            inputPanel.add(startTimeText);

	            JButton updateScheduleButton = new JButton("변경");
	            updateScheduleButton.addActionListener(new ActionListener() {
	                public void actionPerformed(ActionEvent e) {
	                    if (updateSchedule( Integer.parseInt(scheduleIdText.getText()), 
	                                       screeningDayText.getText(), Integer.parseInt(screeningCountText.getText()), 
	                                       startTimeText.getText())) {
	                        JOptionPane.showMessageDialog(frame, "update_schedule 성공", "성공", JOptionPane.INFORMATION_MESSAGE);
	                        frame.dispose();
	                        showAllTables(null);
	                    } else {
	                        JOptionPane.showMessageDialog(frame, "스케줄 정보 변경 실패", "오류", JOptionPane.ERROR_MESSAGE);
	                    }
	                }
	            });
	            inputPanel.add(new JLabel());
	            inputPanel.add(updateScheduleButton);
	            break;

	        case "ticket":
	            inputPanel.add(new JLabel("티켓 ID:"));
	            JTextField ticketIdText = new JTextField(20);
	            inputPanel.add(ticketIdText);

	            JButton updateTicketButton = new JButton("변경");
	            updateTicketButton.addActionListener(new ActionListener() {
	                public void actionPerformed(ActionEvent e) {
	                    if (updateTicket(Integer.parseInt(ticketIdText.getText()))) {
	                        JOptionPane.showMessageDialog(frame, "update_ticket 성공", "성공", JOptionPane.INFORMATION_MESSAGE);
	                        frame.dispose();
	                        showAllTables(null);
	                    } else {
	                        JOptionPane.showMessageDialog(frame, "티켓 정보 변경 실패", "오류", JOptionPane.ERROR_MESSAGE);
	                    }
	                }
	            });
	            inputPanel.add(new JLabel());
	            inputPanel.add(updateTicketButton);
	            break;

	        default:
	            break;
	    }
	
	    frame.getContentPane().removeAll();
	    frame.add(inputPanel, BorderLayout.CENTER);
	    frame.revalidate();
	    frame.repaint();
	}
	
	// [통신]member table 업데이트 함수
    private static boolean updateMember(String memberId, String newName, String newPhone, String newEmail) {
        try {
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/db2?serverTimezone=UTC", "root", "1234");
            CallableStatement stmt = conn.prepareCall("{CALL UpdateMember('root', ?, ?, ?, ?)}");
            stmt.setInt(1, Integer.parseInt(memberId));
            stmt.setString(2, newName);
            stmt.setString(3, newPhone);
            stmt.setString(4, newEmail);

            stmt.execute();
            System.out.println("회원 업데이트: " + memberId);
            return true;
        } catch (SQLException ex) {
            System.out.println("SQLException: " + ex.getMessage());
            return false;
        }
    }

    // [통신]movie table 업데이트 함수
    private static boolean updateMovie(String movieId, String newMovieName, String newShowtime, String newRating, String newDirector, String newActor, String newGenre, String newInstruction, String newCreatedAt, String newGrade) {
        try {
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/db2?serverTimezone=UTC", "root", "1234");
            CallableStatement stmt = conn.prepareCall("{CALL UpdateMovie('root', ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}");
            stmt.setInt(1, Integer.parseInt(movieId));
            stmt.setString(2, newMovieName);
            stmt.setInt(3, Integer.parseInt(newShowtime));
            stmt.setString(4, newRating);
            stmt.setString(5, newDirector);
            stmt.setString(6, newActor);
            stmt.setString(7, newGenre);
            stmt.setString(8, newInstruction);
            stmt.setString(9, newCreatedAt);
            stmt.setFloat(10, Float.parseFloat(newGrade));

            stmt.execute();
            System.out.println("영화 업데이트: " + movieId);
            return true;
        } catch (SQLException ex) {
            System.out.println("SQLException: " + ex.getMessage());
            return false;
        }
    }

    // [통신]schedule table 업데이트 함수
    private static boolean updateSchedule(int scheduleId, String screeningDay, int screeningCount, String startTime) {
    try {
        Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/db2?serverTimezone=UTC", "root", "1234");
        CallableStatement stmt = conn.prepareCall("{CALL UpdateSchedule('root', ?, ?, ?, ?)}");
        stmt.setInt(1, scheduleId);
        stmt.setString(2, screeningDay);
        stmt.setInt(3, screeningCount);
        stmt.setString(4, startTime);

        stmt.execute();
        System.out.println("스케줄 업데이트: " + scheduleId);
        return true;
    } catch (SQLException ex) {
        System.out.println("SQLException: " + ex.getMessage());
        return false;
    }
}
    

    // [통신]ticket table 업데이트 함수
    private static boolean updateTicket(int ticketId) {
    try {
        Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/db2?serverTimezone=UTC", "root", "1234");
        CallableStatement stmt = conn.prepareCall("{CALL updateTicket('root', ?, ?)}");
        stmt.setInt(1, ticketId);
        stmt.registerOutParameter(2, Types.INTEGER);

        stmt.execute();
        int updatedTicketId = stmt.getInt(2);

        if (updatedTicketId != 0) {
            System.out.println("티켓 업데이트: " + updatedTicketId);
            return true;
        } else {
            return false;
        }
    } catch (SQLException ex) {
        System.out.println("SQLException: " + ex.getMessage());
        return false;
    }
}
    
    // [통신]reservation table 업데이트 함수
    private static boolean updateReservation(String reservationId, String newPaymentMethod, String newPaymentAmount, String newPaymentStatus, String newMemberId) {
        try {
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/db2?serverTimezone=UTC", "root", "1234");
            CallableStatement stmt = conn.prepareCall("{CALL UpdateReservation('root', ?, ?, ?, ?, ?)}");
            stmt.setInt(1, Integer.parseInt(reservationId));
            stmt.setString(2, newPaymentMethod);
            stmt.setInt(3, Integer.parseInt(newPaymentAmount));
            stmt.setString(4, newPaymentStatus);
            stmt.setInt(5, Integer.parseInt(newMemberId));

            stmt.execute();
            System.out.println("예약 업데이트: " + reservationId);
            return true;
        } catch (SQLException ex) {
            System.out.println("SQLException: " + ex.getMessage());
            return false;
        }
    }

    // [통신]seat table 업데이트 함수
    private static boolean updateSeat(String seatId, String newRow, String newColumn, String newSeatAvailability) {
        try {
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/db2?serverTimezone=UTC", "root", "1234");
            CallableStatement stmt = conn.prepareCall("{CALL UpdateSeat('root', ?, ?, ?, ?)}");
            stmt.setInt(1, Integer.parseInt(seatId));
            stmt.setInt(2, Integer.parseInt(newRow));
            stmt.setInt(3, Integer.parseInt(newColumn));
            stmt.setInt(4, Integer.parseInt(newSeatAvailability));

            stmt.execute();
            System.out.println("좌석 업데이트: " + seatId);
            return true;
        } catch (SQLException ex) {
            System.out.println("SQLException: " + ex.getMessage());
            return false;
        }
    }

    // [통신]theater table 업데이트 함수
    private static boolean updateTheater(String memberId, String movieId) {
        try {
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/db2?serverTimezone=UTC", "root", "1234");
            CallableStatement stmt = conn.prepareCall("{CALL UpdateTheaterByMovie(?, ?)}");
            stmt.setInt(1, Integer.parseInt(memberId));
            stmt.setInt(2, Integer.parseInt(movieId));

            stmt.execute();
            System.out.println("상영관 업데이트: " + movieId);
            return true;
        } catch (SQLException ex) {
            System.out.println("SQLException: " + ex.getMessage());
            return false;
        }
    }

       


    
    
    ////////////////[사용자]
	// 사용자 첫 화면
    private static void showUserBoard() {
    JFrame userFrame = new JFrame("User Board");
    userFrame.setSize(1200, 900);
    userFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    JPanel userPanel = new JPanel();
    userPanel.setLayout(new GridBagLayout());
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.insets = new Insets(10, 10, 10, 10);
    gbc.fill = GridBagConstraints.HORIZONTAL;

    String[] buttonLabels = {"영화조회 및 검색", "예매하기", "예매조회"};

    for (int i = 0; i < buttonLabels.length; i++) {
        JButton button = new JButton(buttonLabels[i]);
        button.setPreferredSize(new Dimension(150, 50));
        gbc.gridx = i;
        gbc.gridy = 0;
        userPanel.add(button, gbc);

        if ("영화조회 및 검색".equals(buttonLabels[i])) {
            button.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    showMovies(userFrame);
                }
            });
        } else if ("예매조회".equals(buttonLabels[i])) {
            button.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    showReservations(userFrame, loggedInMemberId);
                }
            });
        } else if ("예매하기".equals(buttonLabels[i])) {
            button.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    showReservationForm(userFrame);
                }
            });
        }
    }

    userFrame.getContentPane().add(userPanel, BorderLayout.NORTH);
    userFrame.setVisible(true);
}

	// [통신] movie table 불러오기
    private static void showMovies(JFrame userFrame) {
    // 메인 패널 생성
    JPanel mainPanel = new JPanel();
    mainPanel.setLayout(new BorderLayout());
    mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));


    // 테이블 패널 생성
    JPanel tablePanel = new JPanel();
    tablePanel.setLayout(new BoxLayout(tablePanel, BoxLayout.Y_AXIS));
    tablePanel.setPreferredSize(new Dimension(500, 300));

    // "영화 검색" 버튼 추가
    JButton searchButton = new JButton("영화검색");
    searchButton.setPreferredSize(new Dimension(200, 70)); // 버튼 크기 설정

    searchButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            showSearchMovies(userFrame, tablePanel);
        }
    });

    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    buttonPanel.add(searchButton);
    mainPanel.add(buttonPanel, BorderLayout.SOUTH);

    // 테이블을 스크롤 가능한 영역에 추가
    mainPanel.add(new JScrollPane(tablePanel), BorderLayout.CENTER);

    try {
        Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/db2?serverTimezone=UTC", "root", "1234");

        CallableStatement stmt = conn.prepareCall("{CALL SearchMovies(?, ?, ?, ?, ?)}");
        stmt.setInt(1, loggedInMemberId); // input_member_id 고정값
        stmt.setNull(2, Types.VARCHAR); // movie_title null 값
        stmt.setNull(3, Types.VARCHAR); // director_name null 값
        stmt.setNull(4, Types.VARCHAR); // actor_name null 값
        stmt.setNull(5, Types.VARCHAR); // genre_name null 값

        boolean hasResults = stmt.execute();
        while (hasResults) {
            ResultSet rs = stmt.getResultSet();
            ResultSetMetaData rsmd = rs.getMetaData();
            int columnsNumber = rsmd.getColumnCount();

            DefaultTableModel tableModel = new DefaultTableModel();
            JTable table = new JTable(tableModel);
            table.setPreferredScrollableViewportSize(new Dimension(500, 300));

            for (int i = 1; i <= columnsNumber; i++) {
                tableModel.addColumn(rsmd.getColumnName(i));
            }

            while (rs.next()) {
                Object[] rowData = new Object[columnsNumber];
                for (int i = 0; i < columnsNumber; i++) {
                    rowData[i] = rs.getObject(i + 1);
                }
                tableModel.addRow(rowData);
            }

            tablePanel.add(new JScrollPane(table));

            hasResults = stmt.getMoreResults();
        }
    } catch (SQLException ex) {
        JTextArea textArea = new JTextArea("SQLException: " + ex.getMessage(), 20, 70);
        mainPanel.add(new JScrollPane(textArea), BorderLayout.CENTER);
    }

    userFrame.getContentPane().add(mainPanel, BorderLayout.CENTER);
    userFrame.revalidate();
    userFrame.repaint();
}
	
	// [통신] 특정 영화 검색하기
	private static void showSearchMovies(JFrame userFrame, JPanel mainPanel) {
	    // 기존 검색 패널 제거
	    for (Component component : mainPanel.getComponents()) {
	        if (component instanceof JPanel) {
	            mainPanel.remove(component);
	        }
	    }
	
	    JPanel searchPanel = new JPanel();
	    searchPanel.setLayout(new GridLayout(5, 2));
	    searchPanel.setPreferredSize(new Dimension(100,50));
	
	    JLabel titleLabel = new JLabel("영화 제목:");
	    JTextField titleText = new JTextField(20);
	    JLabel directorLabel = new JLabel("감독명:");
	    JTextField directorText = new JTextField(20);
	    JLabel actorLabel = new JLabel("배우명:");
	    JTextField actorText = new JTextField(20);
	    JLabel genreLabel = new JLabel("장르:");
	    JTextField genreText = new JTextField(20);
	
	    JButton searchButton = new JButton("검색");
	    searchButton.addActionListener(new ActionListener() {
	        public void actionPerformed(ActionEvent e) {
	            searchMovies(userFrame, titleText.getText(), directorText.getText(), actorText.getText(), genreText.getText(), mainPanel);
	        }
	    });
	
	    searchPanel.add(titleLabel);
	    searchPanel.add(titleText);
	    searchPanel.add(directorLabel);
	    searchPanel.add(directorText);
	    searchPanel.add(actorLabel);
	    searchPanel.add(actorText);
	    searchPanel.add(genreLabel);
	    searchPanel.add(genreText);
	    searchPanel.add(new JLabel());
	    searchPanel.add(searchButton);
	
	    mainPanel.add(searchPanel, BorderLayout.EAST);
	    userFrame.revalidate();
	    userFrame.repaint();
	}
	
	// [통신] 영화 검색 결과 보여주기
	private static void searchMovies(JFrame userFrame, String movieTitle, String directorName, String actorName, String genreName, JPanel mainPanel) {
    // 기존 결과 패널 제거
    for (Component component : mainPanel.getComponents()) {
        if (component instanceof JPanel && component != mainPanel.getComponent(0)) {
            mainPanel.remove(component);
        }
    }

    JPanel resultPanel = new JPanel();
    resultPanel.setLayout(new BoxLayout(resultPanel, BoxLayout.Y_AXIS));
    resultPanel.setPreferredSize(new Dimension(800, 300));

    try {
        Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/db2?serverTimezone=UTC", "root", "1234");

        CallableStatement stmt = conn.prepareCall("{CALL SearchMovies(?, ?, ?, ?, ?)}");
        stmt.setInt(1, loggedInMemberId); // input_member_id 고정값 2
        stmt.setString(2, movieTitle.isEmpty() ? null : movieTitle);
        stmt.setString(3, directorName.isEmpty() ? null : directorName);
        stmt.setString(4, actorName.isEmpty() ? null : actorName);
        stmt.setString(5, genreName.isEmpty() ? null : genreName);

        boolean hasResults = stmt.execute();
        while (hasResults) {
            ResultSet rs = stmt.getResultSet();
            ResultSetMetaData rsmd = rs.getMetaData();
            int columnsNumber = rsmd.getColumnCount();

            DefaultTableModel tableModel = new DefaultTableModel();
            JTable table = new JTable(tableModel);
            table.setPreferredScrollableViewportSize(new Dimension(500, 300));

            for (int i = 1; i <= columnsNumber; i++) {
                tableModel.addColumn(rsmd.getColumnName(i));
            }

            while (rs.next()) {
                Object[] rowData = new Object[columnsNumber];
                for (int i = 0; i < columnsNumber; i++) {
                    rowData[i] = rs.getObject(i + 1);
                }
                tableModel.addRow(rowData);
            }

            resultPanel.add(new JScrollPane(table));

            hasResults = stmt.getMoreResults();
        }
    } catch (SQLException ex) {
        JTextArea textArea = new JTextArea("SQLException: " + ex.getMessage(), 20, 70);
        resultPanel.add(new JScrollPane(textArea));
    }

    mainPanel.add(resultPanel, BorderLayout.SOUTH);
    userFrame.revalidate();
    userFrame.repaint();
}


	private static void showReservationForm(JFrame userFrame) {
    // 메인 패널 생성
    JPanel mainPanel = new JPanel();
    mainPanel.setLayout(new BorderLayout());
    mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

    // 테이블 패널 생성
    JPanel scheduleTablePanel = new JPanel();
    scheduleTablePanel.setLayout(new BoxLayout(scheduleTablePanel, BoxLayout.Y_AXIS));
    scheduleTablePanel.setPreferredSize(new Dimension(500, 300));

    JPanel reservationTablePanel = new JPanel();
    reservationTablePanel.setLayout(new BoxLayout(reservationTablePanel, BoxLayout.Y_AXIS));
    reservationTablePanel.setPreferredSize(new Dimension(500, 300));

    // 예매 입력 패널 생성
    JPanel reservationPanel = new JPanel(new GridBagLayout());
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.insets = new Insets(5, 5, 5, 5);
    gbc.fill = GridBagConstraints.HORIZONTAL;

    // 예매 입력 필드 및 버튼 (왼쪽)
    JPanel leftPanel = new JPanel(new GridBagLayout());
    GridBagConstraints leftGbc = new GridBagConstraints();
    leftGbc.insets = new Insets(5, 5, 5, 5);
    leftGbc.fill = GridBagConstraints.HORIZONTAL;

    JLabel scheduleIdLabel = new JLabel("Schedule_id");
    JTextField scheduleIdText = new JTextField(10);
    JLabel seatIdLabel = new JLabel("Seat_id:");
    JComboBox<Integer> seatIdComboBox = new JComboBox<>();
    for (int i = 1; i <= 20; i++) {
        seatIdComboBox.addItem(i);
    }

    leftGbc.gridx = 0;
    leftGbc.gridy = 0;
    leftPanel.add(scheduleIdLabel, leftGbc);
    leftGbc.gridx = 1;
    leftPanel.add(scheduleIdText, leftGbc);
    leftGbc.gridx = 0;
    leftGbc.gridy = 1;
    leftPanel.add(seatIdLabel, leftGbc);
    leftGbc.gridx = 1;
    leftPanel.add(seatIdComboBox, leftGbc);

    JButton reserveButton = new JButton("예매");
    reserveButton.setPreferredSize(new Dimension(200, 70)); // 버튼 크기 설정
    leftGbc.gridx = 0;
    leftGbc.gridy = 2;
    leftGbc.gridwidth = 2;
    leftGbc.fill = GridBagConstraints.CENTER;
    leftPanel.add(reserveButton, leftGbc);

    reserveButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            int scheduleId = Integer.parseInt(scheduleIdText.getText());
            int seatId = (int) seatIdComboBox.getSelectedItem();
            makeReservation(scheduleId, seatId);
        }
    });

    // 예매 수정 입력 필드 및 버튼 (오른쪽)
    JPanel rightPanel = new JPanel(new GridBagLayout());
    GridBagConstraints rightGbc = new GridBagConstraints();
    rightGbc.insets = new Insets(5, 5, 5, 5);
    rightGbc.fill = GridBagConstraints.HORIZONTAL;

    JLabel reservationIdLabel = new JLabel("Reservation_id");
    JTextField reservationIdText = new JTextField(10);
    JLabel newScheduleIdLabel = new JLabel("New Schedule_id");
    JTextField newScheduleIdText = new JTextField(10);
    JLabel newSeatIdLabel = new JLabel("New Seat_id");
    JComboBox<Integer> newSeatIdComboBox = new JComboBox<>();
    for (int i = 1; i <= 20; i++) {
        newSeatIdComboBox.addItem(i);
    }

    rightGbc.gridx = 0;
    rightGbc.gridy = 0;
    rightPanel.add(reservationIdLabel, rightGbc);
    rightGbc.gridx = 1;
    rightPanel.add(reservationIdText, rightGbc);
    rightGbc.gridx = 0;
    rightGbc.gridy = 1;
    rightPanel.add(newScheduleIdLabel, rightGbc);
    rightGbc.gridx = 1;
    rightPanel.add(newScheduleIdText, rightGbc);
    rightGbc.gridx = 0;
    rightGbc.gridy = 2;
    rightPanel.add(newSeatIdLabel, rightGbc);
    rightGbc.gridx = 1;
    rightPanel.add(newSeatIdComboBox, rightGbc);

    JButton updateButton = new JButton("예매수정");
    updateButton.setPreferredSize(new Dimension(200, 70)); // 버튼 크기 설정
    rightGbc.gridx = 0;
    rightGbc.gridy = 3;
    rightGbc.gridwidth = 2;
    rightGbc.fill = GridBagConstraints.CENTER;
    rightPanel.add(updateButton, rightGbc);

    updateButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            int reservationId = Integer.parseInt(reservationIdText.getText());
            int newScheduleId = Integer.parseInt(newScheduleIdText.getText());
            int newSeatId = (int) newSeatIdComboBox.getSelectedItem();
            updateUserReservation(reservationId, newScheduleId, newSeatId);
        }
    });

    // "뒤로가기" 버튼 추가
    JButton backButton = new JButton("뒤로가기");
    backButton.setPreferredSize(new Dimension(100, 50)); // 버튼 크기 설정
    backButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            showUserBoard();
        }
    });

    // 버튼 패널 생성 및 "뒤로가기" 버튼 추가
    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    buttonPanel.add(backButton);
    mainPanel.add(buttonPanel, BorderLayout.SOUTH);

    // 기존 메인 패널 제거
    userFrame.getContentPane().removeAll();

    // 테이블을 스크롤 가능한 영역에 추가
    mainPanel.add(new JScrollPane(scheduleTablePanel), BorderLayout.NORTH);
    mainPanel.add(leftPanel, BorderLayout.WEST);
    mainPanel.add(rightPanel, BorderLayout.EAST);
    mainPanel.add(new JScrollPane(reservationTablePanel), BorderLayout.CENTER);

    userFrame.getContentPane().add(mainPanel, BorderLayout.CENTER);
    userFrame.revalidate();
    userFrame.repaint();

    // ExecuteUserReadAllScheduleAndTicket 테이블 로드
    loadScheduleTable(scheduleTablePanel);

    // ViewReservations 테이블 로드
    refreshReservations(userFrame, reservationTablePanel, loggedInMemberId);
}


	private static void loadScheduleTable(JPanel tablePanel) {
    tablePanel.removeAll(); // 기존 데이터를 제거

    try {
        Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/db2?serverTimezone=UTC", "root", "1234");

        CallableStatement stmt = conn.prepareCall("{CALL ExecuteUserReadAllScheduleAndTicket(?)}");
        stmt.setInt(1, loggedInMemberId); // input_member_id 고정값

        boolean hasResults = stmt.execute();
        while (hasResults) {
            ResultSet rs = stmt.getResultSet();
            ResultSetMetaData rsmd = rs.getMetaData();
            int columnsNumber = rsmd.getColumnCount();

            DefaultTableModel tableModel = new DefaultTableModel();
            JTable table = new JTable(tableModel);
            table.setPreferredScrollableViewportSize(new Dimension(500, 300));

            for (int i = 1; i <= columnsNumber; i++) {
                tableModel.addColumn(rsmd.getColumnName(i));
            }

            while (rs.next()) {
                Object[] rowData = new Object[columnsNumber];
                for (int i = 0; i < columnsNumber; i++) {
                    rowData[i] = rs.getObject(i + 1);
                }
                tableModel.addRow(rowData);
            }

            tablePanel.add(new JScrollPane(table));

            hasResults = stmt.getMoreResults();
        }
    } catch (SQLException ex) {
        JTextArea textArea = new JTextArea("SQLException: " + ex.getMessage(), 20, 70);
        tablePanel.add(new JScrollPane(textArea));
    }

    tablePanel.revalidate();
    tablePanel.repaint();
}

	private static void updateUserReservation(int reservationId, int newScheduleId, int newSeatId) {
    try {
        Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/db2?serverTimezone=UTC", "root", "1234");
        CallableStatement stmt = conn.prepareCall("{CALL updateUserReservation(?, ?, ?, ?)}");
        stmt.setInt(1, 2); // input_member_id 고정값
        stmt.setInt(2, reservationId);
        stmt.setInt(3, newScheduleId);
        stmt.setInt(4, newSeatId);

        stmt.execute();

        ResultSet rs = stmt.getResultSet();
        if (rs.next()) {
            String message = rs.getString("Message");
            JOptionPane.showMessageDialog(null, message, "Info", JOptionPane.INFORMATION_MESSAGE);
        }

        showReservations(null, 2);
    } catch (SQLException ex) {
        JOptionPane.showMessageDialog(null, "SQLException: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
}


	private static void refreshReservations(JFrame userFrame, JPanel tablePanel, int memberId) {
    tablePanel.removeAll(); // 기존 데이터를 제거

    try {
        Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/db2?serverTimezone=UTC", "root", "1234");

        CallableStatement stmt = conn.prepareCall("{CALL ViewReservations(?)}");
        stmt.setInt(1, memberId);

        boolean hasResults = stmt.execute();
        while (hasResults) {
            ResultSet rs = stmt.getResultSet();
            ResultSetMetaData rsmd = rs.getMetaData();
            int columnsNumber = rsmd.getColumnCount();

            DefaultTableModel tableModel = new DefaultTableModel();
            JTable table = new JTable(tableModel);
            table.setPreferredScrollableViewportSize(new Dimension(500, 300));

            for (int i = 1; i <= columnsNumber; i++) {
                tableModel.addColumn(rsmd.getColumnName(i));
            }

            while (rs.next()) {
                Object[] rowData = new Object[columnsNumber];
                for (int i = 0; i < columnsNumber; i++) {
                    rowData[i] = rs.getObject(i + 1);
                }
                tableModel.addRow(rowData);
            }

            table.addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    int row = table.getSelectedRow();
                    if (row != -1) {
                        int ticketId = (int) table.getValueAt(row, 6); // ticket_id is the 7th column (index 6)
                        showReservationDetails(userFrame, ticketId); // 예약 세부 정보를 보여주는 메서드 호출
                        System.out.println("ticketId : " + ticketId);
                    }
                }
            });

            tablePanel.add(new JScrollPane(table));

            hasResults = stmt.getMoreResults();
        }
    } catch (SQLException ex) {
        JTextArea textArea = new JTextArea("SQLException: " + ex.getMessage(), 20, 70);
        tablePanel.add(new JScrollPane(textArea));
    }

    tablePanel.revalidate();
    tablePanel.repaint();
}


	// [통신] 사용자예매목록 불러오기
	private static void showReservations(JFrame userFrame, int memberId) {
    // 메인 패널 생성
    JPanel mainPanel = new JPanel();
    mainPanel.setLayout(new BorderLayout());
    mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10)); // 가장자리에서 10픽셀 간격 추가

    // 테이블 패널 생성
    JPanel tablePanel = new JPanel();
    tablePanel.setLayout(new BoxLayout(tablePanel, BoxLayout.Y_AXIS));
    tablePanel.setPreferredSize(new Dimension(500, 300));

    JScrollPane scrollPane = new JScrollPane(tablePanel);
    mainPanel.add(scrollPane, BorderLayout.CENTER);

    // 새로고침 버튼 생성
    JButton refreshButton = new JButton("새로고침");
    refreshButton.setPreferredSize(new Dimension(150, 50));
    refreshButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            refreshReservations(userFrame, tablePanel, memberId); // 프로시저만 다시 호출하여 데이터 최신화
        }
    });

    // 새로고침 버튼을 패널에 추가
    JPanel refreshPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
    refreshPanel.add(refreshButton);
    mainPanel.add(refreshPanel, BorderLayout.SOUTH);

    userFrame.getContentPane().add(mainPanel, BorderLayout.CENTER);
    userFrame.revalidate();
    userFrame.repaint();

    // 초기 데이터 로드
    refreshReservations(userFrame, tablePanel, memberId);
}


	private static void makeReservation(int scheduleId, int seatId) {
        try {
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/db2?serverTimezone=UTC", "root", "1234");
            CallableStatement stmt = conn.prepareCall("{CALL makeUserReservation(?, ?, ?, ?, ?)}");
            stmt.setInt(1, 2); // input_member_id 고정값
            stmt.setInt(2, scheduleId);
            stmt.setInt(3, seatId);
            stmt.registerOutParameter(4, Types.INTEGER); // output_reservation_id
            stmt.registerOutParameter(5, Types.INTEGER); // output_ticket_id

            stmt.execute();
            int reservationId = stmt.getInt(4);
            int ticketId = stmt.getInt(5);

            JOptionPane.showMessageDialog(null, "예매되었습니다. 예약 ID: " + reservationId + ", 티켓 ID: " + ticketId);
            showUserBoard();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "SQLException: " + ex.getMessage());
        }
    }
    

	private static void showReservationDetails(JFrame userFrame, int ticketId) {
    System.out.println("ticketId : " + ticketId);

    JFrame detailsFrame = new JFrame("Reservation Details");
    detailsFrame.setSize(600, 400);
    detailsFrame.setLayout(new BorderLayout());

    JPanel detailsPanel = new JPanel();
    detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));
    detailsPanel.setBorder(new EmptyBorder(10, 10, 10, 10)); // 가장자리에서 10픽셀 간격 추가
    detailsPanel.setPreferredSize(new Dimension(800, 300));

    try {
        Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/db2?serverTimezone=UTC", "root", "1234");

        CallableStatement stmt = conn.prepareCall("{CALL ViewOneReservationDetails(?, ?)}");
        stmt.setInt(1, 2); // member_id 고정값
        stmt.setInt(2, ticketId);
        
        boolean hasResults = stmt.execute();
        if (hasResults) {
            ResultSet rs = stmt.getResultSet();
            ResultSetMetaData rsmd = rs.getMetaData();
            int columnsNumber = rsmd.getColumnCount();

            DefaultTableModel tableModel = new DefaultTableModel();
            JTable table = new JTable(tableModel);
            table.setPreferredScrollableViewportSize(new Dimension(500, 300));

            for (int i = 1; i <= columnsNumber; i++) {
                tableModel.addColumn(rsmd.getColumnName(i));
            }

            while (rs.next()) {
                Object[] rowData = new Object[columnsNumber];
                for (int i = 0; i < columnsNumber; i++) {
                    rowData[i] = rs.getObject(i + 1);
                }
                tableModel.addRow(rowData);
            }

            detailsPanel.add(new JScrollPane(table));
        }
    } catch (SQLException ex) {
        JTextArea textArea = new JTextArea("SQLException: " + ex.getMessage(), 20, 70);
        detailsPanel.add(new JScrollPane(textArea));
    }

    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
    JButton deleteButton = new JButton("삭제하기");
    JButton updateButton = new JButton("변경하기");

    deleteButton.setPreferredSize(new Dimension(150, 50));
    updateButton.setPreferredSize(new Dimension(150, 50));

    buttonPanel.add(deleteButton);
    buttonPanel.add(updateButton);

    deleteButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            try {
                Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/db2?serverTimezone=UTC", "root", "1234");
                CallableStatement stmt = conn.prepareCall("{CALL deleteUserReservation(?, ?)}");
                stmt.setInt(1, 2); // input_member_id 고정값
                stmt.setInt(2, ticketId); // input_reservation_id

                stmt.execute();

                JOptionPane.showMessageDialog(detailsFrame, "해당 예매 내역이 삭제되었습니다.", "삭제 완료", JOptionPane.INFORMATION_MESSAGE);
                detailsFrame.dispose();
                showReservations(userFrame, 2);
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(detailsFrame, "SQLException: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    });

    detailsFrame.add(detailsPanel, BorderLayout.CENTER);
    detailsFrame.add(buttonPanel, BorderLayout.SOUTH);

    detailsFrame.setVisible(true);
}



}

