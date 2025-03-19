package edu.kh.jdbc.dao;

//import static : 지정된 경로에 존재하는 static 구문을 모두 얻어와
//클래스명.메서드명() 이 아닌 메서드명() 만 작성해도 호출 가능하게 함.
import static edu.kh.jdbc.common.JDBCTemplate.close;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import edu.kh.jdbc.dto.User;

//(Model 중 하나)DAO (Data Access Object)
//데이터가 저장된 곳에 접근하는 용도의 객체
//-> DB에 접근하여 Java에서 원하는 결과를 얻기위해
//SQL을 수행하고 결과를 반환 받는 역할

public class UserDAO {
	
	// 필드
		// - DB 접근 관련한 JDBC 객체 참조 변수 미리 선언
		private Statement stmt = null;
		private PreparedStatement pstmt = null;
		private ResultSet rs = null;

		// 메서드
		
		/** 전달받은 Connection을 이용해서 DB에 접근하여
		 * 전달받은 아이디(input)와 일치하는 User 정보를 DB 조회하기
		 * @param conn : Service에서 생성한 Connection 객체
		 * @param input : View에서 입력받은 아이디
		 * @return 아이디가 일치하는 회원의 User 또는 null
		 */
		public User selectId(Connection conn, String input) {
			
			// 1. 결과 저장용 변수 선언
			User user = null;
			
			try {
				
				// 2. SQL 작성
				String sql = "SELECT * FROM TB_USER WHERE USER_ID = ?";
				
				// 3. PreparedStatement 객체 생성
				pstmt = conn.prepareStatement(sql);
				
				// 4. ? (위치홀더) 에 알맞은 값 세팅
				pstmt.setString(1, input);
				
				// 5. SQL 수행 후 결과 반환 받기
				rs = pstmt.executeQuery();
				
				// 6. 조회 결과가 있을 경우
				// + 중복되는 아이디가 없다고 가정
				// -> 1행만 조회되기 때문에 while문 보다는 if를 사용하는게 효과적
				if(rs.next()) {
					// 첫 행에 데이터가 존재한다면
					
					// 각 컬럼의 값 얻어오기
					int userNo = rs.getInt("USER_NO");
					String userId = rs.getString("USER_ID");
					String userPw = rs.getString("USER_PW");
					String userName = rs.getString("USER_NAME");
					// java.sql.Date
					Date enrollDate = rs.getDate("ENROLL_DATE");
					
					// 조회된 컬럼값을 이용해서 User 객체 생성
					user = new User(userNo, 
							userId, 
							userPw, 
							userName, 
							enrollDate.toString() );
					
				}
				
				
			} catch (Exception e) {
				e.printStackTrace();
				
			} finally {
				// 사용한 JDBC 객체 자원 반환(close)
				//JDBCTemplate.close(rs);
				//JDBCTemplate.close(pstmt);
				close(rs);
				close(pstmt);
				
				// Connection 객체는 생성된 Service에서 close!
				
			}
			
			return user; // 결과 반환 (생성된 User 객체 또는 null)
		}

		/** 1. User 등록 DAO
		 * @param conn : DB 연결 정보가 담겨 있는 Connection 객체
		 * @param user : 입력 받은 id, pw, name이 세팅된 User 객체
		 * @return INSERT 결과 행의 개수 리턴
		 */
		public int insertUser(Connection conn, User user) throws Exception {
			
			// SQL 수행 중 발생하는 예외를
			// catch로 처리하지 않고, throws를 이용해서 호출부로 던져 처리
			// -> catch 문 필요 없다
			
			// 1. 결과 저장용 변수 선언
			int result = 0;
			
			try {
				
				// 2. SQL 작성
				String sql = """
							INSERT INTO TB_USER 
							VALUES(SEQ_USER_NO.NEXTVAL, ?, ?, ?, DEFAULT )
						""";
				
				// 3. PreparedStatement 객체 생성
				pstmt = conn.prepareStatement(sql);
				
				// 4. ? (위치홀더) 알맞은 값 대입
				pstmt.setString(1, user.getUserId());
				pstmt.setString(2, user.getUserPw());
				pstmt.setString(3, user.getUserName());
				
				// 5. SQL(INSERT) 수행(executeUpdate) 후 결과(삽입된 행의 개수) 반환 받기
				result = pstmt.executeUpdate();
				
				
			} finally {
				// 6. 사용한 jdbc 객체 자원 반환(close)
				close(pstmt);
				
				
			}
			
			// 결과 저장용 변수에 저장된 값 반환
			return result;
		}

		/** 2. User 전체 조회 DAO
		 * @param conn
		 * @return userList
		 */
		public List<User> selectAll(Connection conn) throws Exception{
			
			// 1. 결과 저장용 변수 선언
			List<User> userList = new ArrayList<>();
			
			try {
				
				// 2. SQL 작성하기
				String sql = """
						SELECT USER_NO, USER_ID, USER_PW, USER_NAME, TO_CHAR(ENROLL_DATE, 'YYYY"년" MM"월" DD"일"') ENROLL_DATE
						FROM TB_USER
						ORDER BY USER_NO
						""";
				
				// 3. PreparedStatement 생성
				pstmt = conn.prepareStatement(sql);
				
				// 4. SQL 수행 후 결과 반환
				rs = pstmt.executeQuery();
				
				// 5. 조회 결과를 1행씩 접근하여 컬럼 값 얻어오기
				// 몇행이 조회될지 모른다 while 1행만 조회된다 if
				
				while(rs.next()) {
					int userNO = rs.getInt("USER_NO");
					String userId = rs.getString("USER_ID");
					String userPw = rs.getString("USER_PW");
					String userName = rs.getString("USER_NAME");
					String enrollDate = rs.getString("ENROLL_DATE");
					// java.sql.Date타입으로 값을 저장하지 않은 이유는
					// SELECT문에서 TO_CHAR 를 통해 문자열로 변환하였기 때문
				
					// User 객체 새로 생성하여 컬럼값 세팅하기
					User user = new User(userNO, userId, userPw, userName, enrollDate);
					
					userList.add(user);
					
				}
				
			} finally {
				close(rs);
				close(pstmt);
			}
			//조회 결과가 담긴 List 반환
			return userList;
		}

		/** 3. 이름에 검색어가 포함되는 회원 모드 조회 DAO
		 * @param conn
		 * @param keyword
		 * @return
		 */
		public List<User> selectName(Connection conn, String keyword) throws Exception{
			
			List<User> name = new ArrayList<>();
			
			try {
				String sql = """
						SELECT USER_NO, USER_ID, USER_PW, USER_NAME, TO_CHAR(ENROLL_DATE, 'YYYY"년" MM"월" DD"일"') ENROLL_DATE
						FROM TB_USER
						WHERE USER_NAME LIKE '%' || ? || '%'
						ORDER BY USER_NO
						""";
				
				pstmt = conn.prepareStatement(sql);
				
				pstmt.setString(1,keyword);
				
				rs = pstmt.executeQuery();
				
				while(rs.next()) {
					
					int userNo = rs.getInt("USER_NO");
					String userId = rs.getString("USER_ID");
					String userPw = rs.getString("USER_PW");
					String userName = rs.getString("USER_NAME");
					String enrollDate = rs.getString("ENROLL_DATE");
					
					
					User usna = new User(userNo, userId, userPw, userName, enrollDate);
					
					name.add(usna);
				}
				
				
				
			} finally {
				close(rs);
				close(pstmt);
			}
			
			return name;
		}

		public User selectNo(Connection conn, int input) throws Exception{
			
			User user = null;
			
			try {
				String sql = """
						SELECT USER_NO, USER_ID, USER_PW, USER_NAME, TO_CHAR(ENROLL_DATE, 'YYYY"년" MM"월" DD"일"') ENROLL_DATE
						FROM TB_USER
						WHERE USER_NO = ?
						""";
				
				pstmt = conn.prepareStatement(sql);
				
				pstmt.setInt(1,input);
				
				rs = pstmt.executeQuery();
				
				if(rs.next()) {
				int userNo = rs.getInt("USER_NO");
				String userId = rs.getString("USER_ID");
				String userPw = rs.getString("USER_PW");
				String userName = rs.getString("USER_NAME");
				String enrollDate = rs.getString("ENROLL_DATE");
				
				user = new User(userNo, userId, userPw, userName, enrollDate);
				}
			} finally {
				
			}
			
			return user;
		}

		/** USER_NO를 입력 받아 일치하는 User 삭제 DAO
		 * @param conn
		 * @param input
		 * @return
		 */
		public int deleteNo(Connection conn, int input) throws Exception{
			
			int result = 0;
			
			try {
				
				String sql = """
						DELETE FROM TB_USER
						WHERE USER_NO = ?
						""";
				
				pstmt = conn.prepareStatement(sql);
				
				pstmt.setInt(1, input);
				
				result = pstmt.executeUpdate();
				
				
				
			} finally {
				
				close(pstmt);
				
			}
			
			return result;
		}

		/** Pw, Id가 일치하는 회원의 USER_NO 조회 DAO
		 * @param conn
		 * @param userId
		 * @param userPw
		 * @return
		 */
		public int selectUser(Connection conn, String userId, String userPw) throws Exception {
			
			int userNo = 0;
			
			try {
				
				String sql = """
						SELECT USER_NO
						FROM TB_USER
						WHERE USER_ID = ?
						AND USER_PW = ?
						""";
				
				pstmt = conn.prepareStatement(sql);
				
				pstmt.setString(1, userId);
				pstmt.setString(2, userPw);
				
				rs = pstmt.executeQuery();
				
				if(rs.next()){
				userNo = rs.getInt("USER_NO");
				}
				
				
			} finally {
				
				close(rs);
				close(pstmt);
				
			}
			
			return userNo;
		}

		public int updateName(Connection conn, String newName, int userNo) throws Exception {
			
			int result = 0;
			
			try {
				
				String sql = """
						UPDATE TB_USER
						SET USER_NAME = ?
						WHERE USER_NO = ?
						""";
				
				pstmt = conn.prepareStatement(sql);
				
				pstmt.setString(1,newName);
				pstmt.setInt(2,userNo);
				
				result = pstmt.executeUpdate();
				
			} finally {
				close(pstmt);
			}
			
			
			return result;
		}

		/** 아이디 중복확인 DAO
		 * @param conn
		 * @param userId
		 * @return count
		 * @throws Exception
		 */
		public int idCheck(Connection conn, String userId) throws Exception{
			
			int count = 0;
			
			try {
				
				String sql = """
						SELECT COUNT(*)
						FROM TB_USER
						WHERE USER_ID = ?
						""";
				
				pstmt = conn.prepareStatement(sql);
				
				pstmt.setString(1, userId);
				
				rs = pstmt.executeQuery();
				
				if(rs.next()) {
					count = rs.getInt(1); // 조회된 컬럼 순서번호를 이용해 컬럼값 얻어오기
				}
				
			} finally {
				close(rs);
				close(pstmt);
			}
			
			
			return count;
		}

	
}
