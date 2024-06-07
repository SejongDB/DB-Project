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

    private static void showAdminBoard() {
        JFrame adminFrame = new JFrame("Admin Board");
        adminFrame.setSize(1800, 600);
        adminFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JPanel adminPanel = new JPanel();
        adminPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        String[] buttonLabels = {"전체조회", "입력(추가)", "삭제/변경", "초기화"};

        for (int i = 0; i < buttonLabels.length; i++) {
            JButton button = new JButton(buttonLabels[i]);
            button.setPreferredSize(new Dimension(150, 50));
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
                        showCreateMemberDialog();
                    }
                });
            }
        }

        adminFrame.getContentPane().add(adminPanel, BorderLayout.NORTH);
        adminFrame.setVisible(true);
    }

private static void showCreateMemberDialog() {
    JFrame createMemberFrame = new JFrame("회원 정보 입력");
    createMemberFrame.setSize(500, 500);
    createMemberFrame.setLayout(new GridLayout(4, 2));

    JLabel nameLabel = new JLabel("이름:");
    JTextField nameText = new JTextField(20);
    JLabel phoneLabel = new JLabel("전화번호:");
    JTextField phoneText = new JTextField(20);
    JLabel emailLabel = new JLabel("이메일:");
    JTextField emailText = new JTextField(20);

    JButton submitButton = new JButton("입력");
    submitButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            String name = nameText.getText();
            String phone = phoneText.getText();
            String email = emailText.getText();

            if (createMember(name, phone, email)) {
                JOptionPane.showMessageDialog(createMemberFrame, "create_member 성공", "성공", JOptionPane.INFORMATION_MESSAGE);
                createMemberFrame.dispose();
                showAllTables(null); // 추가된 데이터까지 렌더링
            } else {
                JOptionPane.showMessageDialog(createMemberFrame, "회원 정보 추가 실패", "오류", JOptionPane.ERROR_MESSAGE);
            }
        }
    });

    createMemberFrame.add(nameLabel);
    createMemberFrame.add(nameText);
    createMemberFrame.add(phoneLabel);
    createMemberFrame.add(phoneText);
    createMemberFrame.add(emailLabel);
    createMemberFrame.add(emailText);
    createMemberFrame.add(new JLabel());
    createMemberFrame.add(submitButton);

    createMemberFrame.setVisible(true);
}

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
    adminFrame.setVisible(true); // ensure frame is visible
}

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
