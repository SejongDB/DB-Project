import java.io.*;
import java.sql.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class db2 {
    private static final String USER_USERNAME = "user1";
    private static final String USER_PASSWORD = "user1";
    
    public static void main(String[] args) {
        System.setProperty("apple.awt.application.appearance", "system");

        // GUI 부분
        SwingUtilities.invokeLater(new Runnable(){
            public void run() {
                createAndShowGUI();
            }
        });
    }

    //**처음 시작 보드(로그인,관리자 로그인 버튼)**
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

    //**로그인 다이얼로그**
    private static void showLoginDialog(String role, boolean isAdmin) {
        // 로그인 프레임 생성
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
                    loginFrame.dispose(); // 로그인 프레임 닫기
                    showAdminBoard(); // 관리자 보드로 이동
                } else if (!isAdmin && username.equals(USER_USERNAME) && password.equals(USER_PASSWORD)) {
                    connectToDatabase(username, password);
                    loginFrame.dispose(); // 로그인 프레임 닫기
                    showUserBoard(); // 유저 보드로 이동
                } else {
                    JOptionPane.showMessageDialog(loginFrame, "아이디 또는 비밀번호가 잘못되었습니다.", "로그인 오류", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // 로그인 프레임에 컴포넌트 추가
        loginFrame.add(userLabel);
        loginFrame.add(userText);
        loginFrame.add(passwordLabel);
        loginFrame.add(passwordText);
        loginFrame.add(new JLabel()); // 빈 셀 추가
        loginFrame.add(loginButton);

        // 프레임 표시
        loginFrame.setVisible(true);
    }

    //[sql]admin_login.sql(관리자 로그인 함수 호)
    private static boolean authenticateAdmin(String username, String password) {
        // 데이터베이스 연결 부분
        try {
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/db2?serverTimezone=UTC",
                    "root", "1234");
            CallableStatement stmt = conn.prepareCall("{CALL LoginAdmin(?, ?, ?)}");
            stmt.setString(1, username);
            stmt.setString(2, password);
            stmt.registerOutParameter(3, Types.INTEGER);

            stmt.execute();
            int adminId = stmt.getInt(3);

            if (adminId == 0) {
                System.out.println("admin_login 함수 호출 성공!");
                return true;
            } else {
            	System.out.println("admin_login 함수 호출 실패!");
                return false;
            }
        } catch (SQLException ex) {
            System.out.println("SQLException: " + ex);
            return false;
        }
    }

//**관리자 페이지(관리자 로그인 성공 시 나오는 화면)**
    private static void showAdminBoard() {
	    JFrame adminFrame = new JFrame("Admin Board");
	    adminFrame.setSize(800, 600);
	    adminFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    JPanel adminPanel = new JPanel();
	    adminPanel.setLayout(new GridBagLayout());
	    GridBagConstraints gbc = new GridBagConstraints();
	    gbc.insets = new Insets(10, 10, 10, 10);
	    gbc.fill = GridBagConstraints.HORIZONTAL;
	
	    // 버튼 이름 배열
	    String[] buttonLabels = {"전체조회", "입력(추가)", "삭제/변경", "초기화"};
	
	    // 버튼 추가
	    for (int i = 0; i < buttonLabels.length; i++) {
	        JButton button = new JButton(buttonLabels[i]);
	        button.setPreferredSize(new Dimension(150, 50));
	        gbc.gridx = i; // 각 버튼의 위치를 가로로 설정
	        gbc.gridy = 0; // 모든 버튼을 첫 번째 행에 배치
	        adminPanel.add(button, gbc);
	    }
	
	    adminFrame.getContentPane().add(adminPanel, BorderLayout.NORTH); // 패널을 프레임 상단에 추가
	
	    // 데이터베이스에서 관리자 전체 조회 함수 호출 및 결과 출력
	    JTextArea textArea = new JTextArea(20, 70);
	    JScrollPane scrollPane = new JScrollPane(textArea);
	    adminFrame.getContentPane().add(scrollPane, BorderLayout.CENTER);
	
	    try {
	        Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/db2?serverTimezone=UTC", "root", "1234");
	
	        // 기존 프로시저가 존재하면 삭제
	        String dropProcedure = "DROP PROCEDURE IF EXISTS ExecuteAdminAllTablesReadQuery";
	        Statement stmt = conn.createStatement();
	        stmt.execute(dropProcedure);
	
	        // 저장 프로시저 생성
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
	
	        // 프로시저 호출
	        CallableStatement cstmt = conn.prepareCall("{CALL ExecuteAdminAllTablesReadQuery(?)}");
	        cstmt.setInt(1, 0); // 관리자 ID는 0
	
	        boolean hasResults = cstmt.execute();
	        while (hasResults) {
	            ResultSet rs = cstmt.getResultSet();
	            ResultSetMetaData rsmd = rs.getMetaData();
	            int columnsNumber = rsmd.getColumnCount();
	
	            while (rs.next()) {
	                for (int i = 1; i <= columnsNumber; i++) {
	                    if (i > 1) textArea.append(",  ");
	                    String columnValue = rs.getString(i);
	                    textArea.append(rsmd.getColumnName(i) + ": " + columnValue);
	                }
	                textArea.append("\n");
	            }
	            hasResults = cstmt.getMoreResults();
	        }
	    } catch (SQLException ex) {
	        textArea.setText("SQLException: " + ex.getMessage());
	    }
	
	    adminFrame.setVisible(true);
	}


    //**유저 페이지(유저 로그인 성공 시 나오는 화면)**
    private static void showUserBoard() {
        JFrame userFrame = new JFrame("User Board");
        userFrame.setSize(800, 600);
        userFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JPanel userPanel = new JPanel();
        userPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // 버튼 이름 배열
        String[] buttonLabels = {"전체조회", "입력(추가)", "삭제/변경", "초기화"};

        // 버튼 추가
        for (int i = 0; i < buttonLabels.length; i++) {
            JButton button = new JButton(buttonLabels[i]);
            button.setPreferredSize(new Dimension(150, 50));
            gbc.gridx = i; // 각 버튼의 위치를 가로로 설정
            gbc.gridy = 0; // 모든 버튼을 첫 번째 행에 배치
            userPanel.add(button, gbc);
        }

        userFrame.getContentPane().add(userPanel, BorderLayout.NORTH); // 패널을 프레임 상단에 추가
        userFrame.setVisible(true);
    }

    private static void connectToDatabase(String username, String password) {
        // 데이터베이스 연결 부분
        try {
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/db2?serverTimezone=UTC",
                    username, password);
            System.out.println("데이터베이스 연결 성공");
            Statement stmt = conn.createStatement();

        } catch (SQLException ex) {
            System.out.println("SQLException: " + ex);
        }
    }
}
