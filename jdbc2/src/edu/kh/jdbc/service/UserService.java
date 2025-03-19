package edu.kh.jdbc.service;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import edu.kh.jdbc.common.JDBCTemplate;
import edu.kh.jdbc.dao.UserDAO;
import edu.kh.jdbc.dto.User;

//(Model 중 하나)Service : 비즈니스 로직을 처리하는 계층,
//데이터를 가공하고 트랜잭션(commit, rollback) 관리 수행

public class UserService {
	// 필드
		private UserDAO dao = new UserDAO();

		// 메서드
		
		/** 전달받은 아이디와 일치하는 User 정보 반환 서비스
		 * @param input (입력된 아이디)
		 * @return 아이디가 일치하는 회원 정보가 담긴 User 객체,
		 * 			없으면 null 반환
		 */
		public User selectId(String input) {
			
			// 1. 커넥션 생성
			Connection conn = JDBCTemplate.getConnection();
			
			// 2. 데이터 가공(할게 없으면 넘어감)
			
			// 3. DAO 메서드 호출 결과 반환
			User user = dao.selectId(conn, input);
			
			// 4. DML(commit/rollback)
			
			// 5. 다 쓴 커넥션 자원 반환
			JDBCTemplate.close(conn);
			
			// 6. 결과를 view 리턴
			return user;
		}

		/**
		 * @param user : 입력받은 id, pw, name이 세팅된 객체
		 * @return 결과 행의 개수
		 */
		public int insertUser(User user) throws Exception{

			// 1. 커넥션 생성
			Connection conn = JDBCTemplate.getConnection();
			
			// 2. 데이터 가공 (할게 없으면 넘어감)
			
			// 3. DAO 메서드 호출 후 결과 반환 받기
			int result = dao.insertUser(conn, user);
			
			// 4. DML(INSERT) 수행 결과에 따라 트랜잭션 제어 처리
			if(result>0) {// INSERT 성공
				JDBCTemplate.commit(conn);
				
			}else {// INSERT 실패
				JDBCTemplate.rollback(conn);
			}
			
			// 5. Connection 반환하기
			JDBCTemplate.close(conn);
			
			// 6. 결과 반환
			return result;
		}

		/** 2. User 전체 조회 서비스
		 * @return 조회된 User들이 담긴 List
		 */
		public List<User> selectAll() throws Exception {
			
			// 1. 커넥션 생성
			Connection conn = JDBCTemplate.getConnection();
			
			// 2. DAO 메서드 호출
			List<User> userList = dao.selectAll(conn);
			
			// 3. Connection 반환
			JDBCTemplate.close(conn);
			
			return userList;
		}

		
		/**
		 * @param keyword
		 * @return
		 */
		public List<User> selectName(String keyword) throws Exception{
			
			Connection conn = JDBCTemplate.getConnection();
			
			List<User> name = dao.selectName(conn, keyword);
			
			JDBCTemplate.close(conn);
			
			return name;
			
		}

		public User selectNo(int input) throws Exception{
			
			Connection conn = JDBCTemplate.getConnection();
			
			User user = dao.selectNo(conn, input);
			
			JDBCTemplate.close(conn);
			
			return user;
		}

		public int deleteUser(int input) throws Exception{
			
			Connection conn = JDBCTemplate.getConnection();
			
			int result = dao.deleteNo(conn, input);
			
			// 결과에 따른 트랜잭션 처리 필요
			if(result>0) {
				JDBCTemplate.commit(conn);
			}else {
				JDBCTemplate.rollback(conn);
			}
			
			JDBCTemplate.close(conn);
			
			return result;
		}

		/** ID, PW가 일치하는 회원의 USER_NO 조회 서비스
		 * @param userId
		 * @param userPw
		 * @return
		 * @throws Exception
		 */
		public int selectUserNo(String userId, String userPw) throws Exception{
			
			Connection conn = JDBCTemplate.getConnection();
			
			int userNo = dao.selectUser(conn, userId, userPw);
			
			JDBCTemplate.close(conn);
			
			return userNo;
		}


		/** userNo가 일치하는 회원의 이름 수정 서비스
		 * @param newName
		 * @param userNo
		 * @return
		 * @throws Exception
		 */
		public int updateName(String newName, int userNo) throws Exception{
			
			Connection conn = JDBCTemplate.getConnection();
			
			int result = dao.updateName(conn, newName, userNo);
			
			if(result>0)JDBCTemplate.commit(conn);
			else JDBCTemplate.rollback(conn);
			
			JDBCTemplate.close(conn);
			
			return result;
		}

		/** 아이디 중복 확인 서비스
		 * @param userId
		 * @return
		 * @throws Exception
		 */
		public int idCheck(String userId) throws Exception{
			
			Connection conn = JDBCTemplate.getConnection();
			
			int count = dao.idCheck(conn, userId);
			
			JDBCTemplate.close(conn);
			
			return count;
		}

		public int multiInsertUser(List<User> userList) throws Exception{
			
			// 다중 INSERT 방법
			// 1) SQL 을 이용한 다중 INSERT
			// 2) Java 반복문을 이용한 다중 INSERT
			
			Connection conn = JDBCTemplate.getConnection();
			
			int count = 0; // 삽입 성공한 행의 개수
			
			// 1행씩 삽입
			for(User user : userList) {
				
				int result = dao.insertUser(conn, user); 
				count+=result;
			}
			
			if(count == userList.size()) JDBCTemplate.commit(conn);
			else JDBCTemplate.rollback(conn);
			
			JDBCTemplate.close(conn);
			
			return count;
		}

}
