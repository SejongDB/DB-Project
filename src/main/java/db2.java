import java.io.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import java.awt.*;
import java.awt.event.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class db2 {
	// 로그인 성공 시 저장할 멤버 아이디
	private static int loggedInMemberId = -1;
	
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
    //**로그인 화면 
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
            } else if (!isAdmin && authenticateUser(username, password)) {
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
    //**회원 로그인 통신 함수
    private static boolean authenticateUser(String username, String password) {
        // 데이터베이스 연결 부분
        try {
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/db2?serverTimezone=UTC",
                    "root", "1234");
            CallableStatement stmt = conn.prepareCall("{CALL LoginUser(?, ?, ?)}");
            stmt.setString(1, username);
            stmt.setString(2, password);
            stmt.registerOutParameter(3, Types.INTEGER);

            stmt.execute();
            int memberId = stmt.getInt(3);

            if (memberId != 0) {
                System.out.println("유저 로그인 통신 성공! member_id: " + memberId);
                loggedInMemberId = memberId; // 로그인한 사용자의 멤버 아이디 저장
                // member_id를 사용하여 CheckUserRole 프로시저 호출
                if (checkUserRole(conn, memberId)) {
                    return true;
                } else {
                    System.out.println("유저 역할 확인 실패!");
                    return false;
                }
            } else {
                System.out.println("user_login 함수 호출 실패!");
                return false;
            }
        } catch (SQLException ex) {
            System.out.println("SQLException: " + ex.getMessage());
            return false;
        }
    }


    //회원 로그인 시, 로그인 성공 후 더블체크 함수
    private static boolean checkUserRole(Connection conn, int memberId) {
        try {
            CallableStatement stmt = conn.prepareCall("{CALL CheckUserRole(?, ?)}");
            stmt.setInt(1, memberId);
            stmt.registerOutParameter(2, Types.BOOLEAN);

            stmt.execute();
            boolean isUserRole = stmt.getBoolean(2);

            if (isUserRole) {
                System.out.println("유저 역할 확인 통신 성공!");
                return true;
            } else {
                System.out.println("유저 역할 확인 통신 실패!");
                return false;
            }
        } catch (SQLException ex) {
            System.out.println("SQLException: " + ex.getMessage());
            return false;
        }
    }

	
	//[sql]admin_login.sql(관리자 로그인 함수 호출)
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
	
	        if (adminId == 1) {
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
        adminFrame.setSize(1800, 600);
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
            
            if ("전체조회".equals(buttonLabels[i])) {
                button.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        showAllTables(adminFrame);
                    }
                });
            }
        }

        adminFrame.getContentPane().add(adminPanel, BorderLayout.NORTH); // 패널을 프레임 상단에 추가
        adminFrame.setVisible(true);
    
   }

    //**전체 테이블 조회 및 렌더링**
    private static void showAllTables(JFrame adminFrame) {
        JPanel tablePanel = new JPanel();
        tablePanel.setLayout(new BoxLayout(tablePanel, BoxLayout.X_AXIS));
        adminFrame.getContentPane().add(new JScrollPane(tablePanel), BorderLayout.CENTER);
        
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
            cstmt.setInt(1, 1); // 관리자 ID는 1

            boolean hasResults = cstmt.execute();
            while (hasResults) {
                ResultSet rs = cstmt.getResultSet();
                ResultSetMetaData rsmd = rs.getMetaData();
                int columnsNumber = rsmd.getColumnCount();
                String tableName = rsmd.getTableName(1);

                // 테이블 모델 생성
                DefaultTableModel tableModel = new DefaultTableModel();
                JTable table = new JTable(tableModel);

                // 테이블 이름 추가
                tableModel.addColumn(tableName);

                // 테이블 헤더 추가
                for (int i = 1; i <= columnsNumber; i++) {
                    tableModel.addColumn(rsmd.getColumnName(i));
                }

                // 데이터 추가
                while (rs.next()) {
                    Object[] rowData = new Object[columnsNumber + 1];
                    rowData[0] = rs.getRow(); // 인덱스 추가
                    for (int i = 1; i <= columnsNumber; i++) {
                        rowData[i] = rs.getObject(i);
                    }
                    tableModel.addRow(rowData);
                }

                // 테이블을 패널에 추가
                tablePanel.add(new JScrollPane(table));

                hasResults = cstmt.getMoreResults();
            }
        } catch (SQLException ex) {
            JTextArea textArea = new JTextArea("SQLException: " + ex.getMessage(), 20, 70);
            adminFrame.getContentPane().add(new JScrollPane(textArea), BorderLayout.CENTER);
        }

        adminFrame.revalidate();
        adminFrame.repaint();
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
        String[] buttonLabels = {"영화조회", "예매하기", "예매조회"};

        // 버튼 추가
        for (int i = 0; i < buttonLabels.length; i++) {
            JButton button = new JButton(buttonLabels[i]);
            button.setPreferredSize(new Dimension(150, 50));
            gbc.gridx = i; // 각 버튼의 위치를 가로로 설정
            gbc.gridy = 0; // 모든 버튼을 첫 번째 행에 배치
            userPanel.add(button, gbc);

            if ("예매조회".equals(buttonLabels[i])) {
                button.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        showReservations(userFrame, loggedInMemberId); // 여기에 유저의 member_id를 전달
                    }
                });
            }
        }

        userFrame.getContentPane().add(userPanel, BorderLayout.NORTH); // 패널을 프레임 상단에 추가
        userFrame.setVisible(true);
    }

//**예약 조회 및 렌더링**
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

            // 테이블 모델 생성
            DefaultTableModel tableModel = new DefaultTableModel();
            JTable table = new JTable(tableModel);

            // 테이블 헤더 추가
            for (int i = 1; i <= columnsNumber; i++) {
                tableModel.addColumn(rsmd.getColumnName(i));
            }

            // 데이터 추가
            while (rs.next()) {
                Object[] rowData = new Object[columnsNumber];
                for (int i = 0; i < columnsNumber; i++) {
                    rowData[i] = rs.getObject(i + 1);
                }
                tableModel.addRow(rowData);
            }

            // 테이블을 패널에 추가
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

    //지금은 무용지물이 되어 버린 DB 접속코
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
