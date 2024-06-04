import java.io.*;
import java.sql.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class db2 {

    private static final String ADMIN_USERNAME = "root";
    private static final String ADMIN_PASSWORD = "1234";
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
        adminButton.setMinimumSize(new Dimension(150, 50));
        adminButton.setMaximumSize(new Dimension(150, 50));
        adminButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showLoginDialog(frame, "관리자", ADMIN_USERNAME, ADMIN_PASSWORD);
            }
        });

        // 회원 로그인 버튼 생성
        JButton userButton = new JButton("회원 로그인");
        userButton.setPreferredSize(new Dimension(150, 50)); 
        userButton.setMinimumSize(new Dimension(150, 50));
        userButton.setMaximumSize(new Dimension(150, 50));
        userButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showLoginDialog(frame, "회원", USER_USERNAME, USER_PASSWORD);
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

    private static void showLoginDialog(JFrame parentFrame, String role, String expectedUsername, String expectedPassword) {
        // 로그인 다이얼로그 생성
        JDialog loginDialog = new JDialog(parentFrame, role + " 로그인", true);
        loginDialog.setSize(300, 150);
        loginDialog.setLayout(new GridLayout(3, 2));

        JLabel userLabel = new JLabel("아이디:");
        JTextField userText = new JTextField(20);
        JLabel passwordLabel = new JLabel("비밀번호:");
        JPasswordField passwordText = new JPasswordField(20);

        JButton loginButton = new JButton("로그인");
        loginButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String username = userText.getText();
                String password = new String(passwordText.getPassword());

                if (username.equals(expectedUsername) && password.equals(expectedPassword)) {
                    connectToDatabase(username, password);
                    loginDialog.dispose();
                    showAdminBoard(); // 관리자 보드로 이동
                } else {
                    JOptionPane.showMessageDialog(loginDialog, "아이디 또는 비밀번호가 잘못되었습니다.", "로그인 오류", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // 로그인 다이얼로그에 컴포넌트 추가
        loginDialog.add(userLabel);
        loginDialog.add(userText);
        loginDialog.add(passwordLabel);
        loginDialog.add(passwordText);
        loginDialog.add(new JLabel()); // 빈 셀 추가
        loginDialog.add(loginButton);

        // 다이얼로그 표시
        loginDialog.setVisible(true);
    }

    private static void showAdminBoard() {
        JFrame adminFrame = new JFrame("Admin Board");
        adminFrame.setSize(400, 300);
        adminFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        JPanel adminPanel = new JPanel();
        adminPanel.setLayout(new GridLayout(2, 2, 10, 10)); // 2x2 그리드, 수평 및 수직 간격 10

        // 1, 2, 3, 4 번 버튼 추가
        for (int i = 1; i <= 4; i++) {
            JButton button = new JButton(Integer.toString(i));
            adminPanel.add(button);
        }

        adminFrame.getContentPane().add(adminPanel);
        adminFrame.setVisible(true);
    }

    private static void connectToDatabase(String username, String password) {
        // 데이터베이스 연결 부분
        try {
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/db2?serverTimezone=UTC",
                    username, password);
            System.out.println("데이터베이스 연결 성공");
            Statement stmt = conn.createStatement();

            // 현재 날짜 설정 (임의의 날짜)
            String currentDate = "2024-06-20";
            stmt.executeUpdate("SET @current_date = '" + currentDate + "'");

        } catch (SQLException ex) {
            System.out.println("SQLException: " + ex);
        }
    }
}
