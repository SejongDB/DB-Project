import java.io.*;
import java.sql.*;
//import java.sql.Connection;
//import java.sql.DriverManager;
//import java.sql.SQLException;

public class Db2 {
	public static void main (String[] args) {
		Connection conn;
		Statement stmt = null;
		try {
			Class.forName("com.mysql.cj.jdbc.Driver"); // MySQL 드라이버 로드
			conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/DBTEST", "root","프로젝트 요구사항에서 지정한 비번 입력"); // JDBC 연결

			System.out.println("DB 연결 완료");
			
			stmt = conn.createStatement(); // SQL문 처리용 Statement 객체 생성
			
			ResultSet srs = stmt.executeQuery("select * from 고객"); // 테이블의 모든 데이터 검색
			System.out.println("[Query 1]");
			printData(srs, "고객이름", "고객아이디", "직업");
			
			srs = stmt.executeQuery("select 고객이름, 고객아이디, 직업 from 고객 where 고객아이디 = 'banana'"); // 고객아이디 = 'banana'만 검색
			System.out.println("\n[Query 2]");
			printData(srs, "고객이름", "고객아이디", "직업");
			
		} catch (ClassNotFoundException e) {
			System.out.println("JDBC 드라이버 로드 오류");
		} catch (SQLException e) {
			System.out.println("SQL 실행오류");
		} 
	}
	// 레코드의 각 열의 값 화면에 출력
	private static void printData(ResultSet srs, String col1, String col2, String col3) throws SQLException {
		while (srs.next()) {
			if (!col1.equals(""))
				System.out.print(srs.getString("고객이름")); 
			if (!col2.equals(""))
				System.out.print("\t|\t" + srs.getString("고객아이디"));
			if (!col3.equals(""))
				System.out.println("\t|\t" + srs.getString("직업"));
			else 
				System.out.println();
		}
	}

}
