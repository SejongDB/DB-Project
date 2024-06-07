import java.io.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import java.awt.*;
import java.awt.event.*;

public class db2 {
    private static int loggedInMemberId = -1;
    
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

                if (isAdmin && authenticateAdmin(username, password)) {
                    loginFrame.dispose();
                    showAdminBoard();
                } else if (!isAdmin && authenticateUser(username, password)) {
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
    //[통신]사용자로그인함
    private static boolean authenticateUser(String username, String password) {
        try {
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/db2?serverTimezone=UTC", "root", "1234");
            CallableStatement stmt = conn.prepareCall("{CALL LoginUser(?, ?, ?)}");
            stmt.setString(1, username);
            stmt.setString(2, password);
            stmt.registerOutParameter(3, Types.INTEGER);

            stmt.execute();
            int memberId = stmt.getInt(3);

            if (memberId != 0) {
                loggedInMemberId = memberId;
                return checkUserRole(conn, memberId);
            } else {
                return false;
            }
        } catch (SQLException ex) {
            return false;
        }
    }
    //[통신]사용자 더블체크
    private static boolean checkUserRole(Connection conn, int memberId) {
        try {
            CallableStatement stmt = conn.prepareCall("{CALL CheckUserRole(?, ?)}");
            stmt.setInt(1, memberId);
            stmt.registerOutParameter(2, Types.BOOLEAN);

            stmt.execute();
            return stmt.getBoolean(2);
        } catch (SQLException ex) {
            return false;
        }
    }
    //[통신] 관리자 로그인 
    private static boolean authenticateAdmin(String username, String password) {
        try {
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/db2?serverTimezone=UTC", "root", "1234");
            CallableStatement stmt = conn.prepareCall("{CALL LoginAdmin(?, ?, ?)}");
            stmt.setString(1, username);
            stmt.setString(2, password);
            stmt.registerOutParameter(3, Types.INTEGER);

            stmt.execute();
            return stmt.getInt(3) == 1;
        } catch (SQLException ex) {
            return false;
        }
    }
    
    
    //관리자 페이지 
    private static void showAdminBoard() {
        JFrame adminFrame = new JFrame("Admin Board");
        adminFrame.setSize(1800, 600);
        adminFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JPanel adminPanel = new JPanel();
        adminPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        String[] buttonLabels = {"전체조회", "입력(추가)", "삭제", "변경", "초기화"};

        for (int i = 0; i < buttonLabels.length; i++) {
            JButton button = new JButton(buttonLabels[i]);
            button.setPreferredSize(new Dimension(100, 35));
            gbc.gridx = i;
            gbc.gridy = 0;
            adminPanel.add(button, gbc);

            if ("전체조회".equals(buttonLabels[i])) {
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
            else if ("초기".equals(buttonLabels[i])) {
                button.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        //showDeleteEntityDialog();
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
            System.out.println("SQLException: " + ex.getMessage());
            return false;
        }
    }
    //[통신]Theater table 삭제
    private static boolean deleteTheater(String theaterId) {
        try {
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/db2?serverTimezone=UTC", "root", "1234");
            CallableStatement stmt = conn.prepareCall("{CALL DeleteTheater('root', ?)}");
            stmt.setString(1, theaterId);

            stmt.execute();
            System.out.println("상영관 삭제: " + theaterId);
            return true;
        } catch (SQLException ex) {
            System.out.println("SQLException: " + ex.getMessage());
            return false;
        }
    }
    //[통신]Ticket table 삭제
    private static boolean deleteTicket(int ticketId) {
        // 구현 예정
        return true;
    }
    //[통신]Reservation table 삭제
    private static boolean deleteReservation(int reservationId) {
        try {
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/db2?serverTimezone=UTC", "root", "1234");
            CallableStatement stmt = conn.prepareCall("{CALL DeleteReservation('root', ?)}");
            stmt.setInt(1, reservationId);

            stmt.execute();
            System.out.println("예약 삭제: " + reservationId);
            return true;
        } catch (SQLException ex) {
            System.out.println("SQLException: " + ex.getMessage());
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
    //[통신]Schedule table 삭제
    private static boolean deleteSchedule(int scheduleId) {
        // 구현 예정
        return true;
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
    //[통신]member table 입력(추가) 
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
    //[통신]member table 입력(추가) 
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
    //[통신]member table 입력(추가) 
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
    //[통신]member table 입력(추가) 
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
    //[통신]member table 입력(추가) 
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
    //[통신]member table 입력(추가) 
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
   //[통신]모든 테이블 불러오기 
    private static void showAllTables(JFrame adminFrame) {
        if (adminFrame == null) {
            adminFrame = new JFrame("Admin Board");
            adminFrame.setSize(1800, 600);
            adminFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        }

        JPanel tablePanel = new JPanel();
        tablePanel.setLayout(new BoxLayout(tablePanel, BoxLayout.X_AXIS));
        adminFrame.getContentPane().add(new JScrollPane(tablePanel), BorderLayout.CENTER);

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

    // [통신]schedule table 업데이트 함수 (빈 함수 형태)
    private static boolean updateSchedule() {
        // 구현 예정
        return true;
    }

    // [통신]ticket table 업데이트 함수 (빈 함수 형태)
    private static boolean updateTicket() {
        // 구현 예정
        return true;
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

    
    
      
    //[사용자]
    //사용자 첫 화면 
    private static void showUserBoard() {
        JFrame userFrame = new JFrame("User Board");
        userFrame.setSize(800, 600);
        userFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JPanel userPanel = new JPanel();
        userPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        String[] buttonLabels = {"영화조회", "예매하기", "예매조회"};

        for (int i = 0; i < buttonLabels.length; i++) {
            JButton button = new JButton(buttonLabels[i]);
            button.setPreferredSize(new Dimension(150, 50));
            gbc.gridx = i;
            gbc.gridy = 0;
            userPanel.add(button, gbc);

            if ("예매조회".equals(buttonLabels[i])) {
                button.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        showReservations(userFrame, loggedInMemberId);
                    }
                });
            }
        }

        userFrame.getContentPane().add(userPanel, BorderLayout.NORTH);
        userFrame.setVisible(true);
    }
    //[통신] 사용자예매목록 불러오기 
    private static void showReservations(JFrame userFrame, int memberId) {
        JPanel tablePanel = new JPanel();
        tablePanel.setLayout(new BoxLayout(tablePanel, BoxLayout.X_AXIS));
        userFrame.getContentPane().add(new JScrollPane(tablePanel), BorderLayout.CENTER);

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
            userFrame.getContentPane().add(new JScrollPane(textArea), BorderLayout.CENTER);
        }

        userFrame.revalidate();
        userFrame.repaint();
    }
}
