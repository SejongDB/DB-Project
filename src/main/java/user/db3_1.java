
import java.io.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import java.awt.*;
import java.awt.event.*;

public class db3 {
    private static int loggedInMemberId = 2;
    
    public static void main(String[] args) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver"); // MySQL 드라이버 로드
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/db2", "root", "1234"); // JDBC 연결
            System.out.println("DB 연결 완료!!");

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
      
 
    
    
    //[사용자]
    // 사용자 첫 화면
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
	
	    // 테이블 패널 생성
	    JPanel tablePanel = new JPanel();
	    tablePanel.setLayout(new BoxLayout(tablePanel, BoxLayout.Y_AXIS));
	    tablePanel.setPreferredSize(new Dimension(400, 250));
	
	    // "영화 검색" 버튼 추가
	    JButton searchButton = new JButton("영화 검색");
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
	            table.setPreferredScrollableViewportSize(new Dimension(400, 250));
	
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
	            table.setPreferredScrollableViewportSize(new Dimension(400, 150));
	
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
                	if (!isAdmin && authenticateUser(username, password)) {
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
            System.out.println("1");
            stmt.setString(1, username);
            stmt.setString(2, password);
            System.out.println("2");

            stmt.registerOutParameter(3, Types.INTEGER);
            System.out.println("3");

            stmt.execute();
            int memberId = stmt.getInt(3);
            System.out.println("memberId : " + memberId);

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
   

}

