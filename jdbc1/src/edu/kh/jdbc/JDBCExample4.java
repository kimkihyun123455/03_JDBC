package edu.kh.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Scanner;

public class JDBCExample4 {

	public static void main(String[] args) {

		// 부서명을 입력 받아
		// 해당 부서에 근무하는 사원의
		// 사번, 이름, 부서명, 직급명을
		// 직급코드 오름차순으로 조회
		// 일치하는 부서가 없습니다
		
		Connection conn = null;
		
		Statement stmt = null;
		
		ResultSet rs = null;
		
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
			
			stmt = conn.createStatement();
			
			sc = new Scanner(System.in);
			
			System.out.print("부서명 입력 : ");
			String input = sc.next();
			
			String sql = "SELECT EMP_ID, EMP_NAME, DEPT_TITLE, JOB_NAME"+
						" FROM EMPLOYEE JOIN JOB USING(JOB_CODE)"+
						" JOIN DEPARTMENT ON(DEPT_CODE = DEPT_ID)"+
						" WHERE DEPT_TITLE = '"+input+"'";
			
			rs = stmt.executeQuery(sql);
			
			if(!rs.next())System.out.println("일치하는 부서가 없습니다.");
			
			do {
				
				String empId = rs.getString("EMP_ID");
								
				String empName = rs.getString("EMP_NAME");
				
				String dept = rs.getString("DEPT_TITLE");
				
				String job = rs.getString("JOB_NAME");
				
				System.out.printf("%s / %s / %s / %s\n", empId, empName, dept, job);
				
			}while(rs.next());
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			try {
				if(sc!=null) sc.close();
				if(rs!=null) rs.close();
				if(stmt!=null) stmt.close();
				if(conn!=null) conn.close();
				
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
		
		
		
	}

}
