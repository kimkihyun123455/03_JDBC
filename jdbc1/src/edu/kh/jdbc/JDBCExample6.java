package edu.kh.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.Scanner;

public class JDBCExample6 {

	public static void main(String[] args) {

		// 아이디, 비밀번호, 이름을 입력받아
		// 아이디 ,비밀번호가 일치하는 사용자의
		// 이름을 수정(UPDATE)
		// 수정 성공 시 "수정성공" 실패 시 "아이디 또는 비번 불일치"
		
		Connection conn = null;
		
		PreparedStatement pstmt = null;
		
		Scanner sc = null;
		
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
			
			String type = "jdbc:oracle:thin:@";
			
			String host = "localhost";
			
			String port = ":1521";
			
			String dbName = ":XE";
			
			String userName = "kh";
			
			String password = "kh1234";
			
			conn = DriverManager.getConnection(type+host+port+dbName, userName, password);
			
			sc = new Scanner(System.in);
			
			System.out.print("아이디 입력 : ");
			String id = sc.nextLine();
			
			System.out.print("비밀번호 입력 : ");
			String pw = sc.nextLine();
			
			System.out.print("이름 입력 : ");
			String name = sc.nextLine();
			
			String sql = """
					UPDATE TB_USER
					SET USER_NAME = ?
					WHERE USER_ID = ?
					AND USER_PW = ?
					""";
			
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setString(1,name);
			pstmt.setString(2,id);
			pstmt.setString(3,pw);
			
			conn.setAutoCommit(false);
			
			int result = pstmt.executeUpdate();
			
			if(result>0) {
				System.out.println("수정성공");
				conn.commit();
			}else {
				System.out.println("실패");
				conn.rollback();
			}
			
		}catch(Exception e) {
			e.printStackTrace();
		}finally {
			try {
				if(sc!=null)sc.close();
				if(pstmt!=null)pstmt.close();
				if(conn!=null)conn.close();
			} catch (Exception e2) {
				e2.printStackTrace();
			}
			
			
		}
		
		
		
	}

}
