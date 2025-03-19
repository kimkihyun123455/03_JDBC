package edu.kh.jdbc.view;

import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

import edu.kh.jdbc.dto.User;
import edu.kh.jdbc.service.UserService;

public class UserView {
	// 필드
		private Scanner sc = new Scanner(System.in);
		private UserService service = new UserService();

		// 메서드

		/**
		 * JDBCTemplate 사용 테스트
		 */
		public void test() {

			// 입력된 ID와 일치하는 USER 정보 조회
			System.out.print("ID 입력 : ");
			String input = sc.next();

			// 서비스 호출 후 결과 반환 받기
			User user = service.selectId(input);

			// 결과에 따라 사용자에게 보여줄 응답화면 결정
			if (user == null) {
				System.out.println("없어용");
			} else {
				System.out.println(user);
			}

		}

		/**
		 * User 관리 프로그램 메인 메뉴
		 */
		public void mainMenu() {

			int input = 0;

			do {
				try {

					System.out.println("\n===== User 관리 프로그램 =====\n");
					System.out.println("1. User 등록(INSERT)");
					System.out.println("2. User 전체 조회(SELECT)");
					System.out.println("3. User 중 이름에 검색어가 포함된 회원 조회 (SELECT)");
					System.out.println("4. USER_NO를 입력 받아 일치하는 User 조회(SELECT)");
					System.out.println("5. USER_NO를 입력 받아 일치하는 User 삭제(DELETE)");
					System.out.println("6. ID, PW가 일치하는 회원이 있을 경우 이름 수정(UPDATE)");
					System.out.println("7. User 등록(아이디 중복 검사)");
					System.out.println("8. 여러 User 등록하기");
					System.out.println("0. 프로그램 종료");

					System.out.print("메뉴 선택 : ");
					input = sc.nextInt();
					sc.nextLine(); // 버퍼에 남은 개행문자 제거

					switch (input) {
					case 1: insertUser(); break;
					case 2: selectAll(); break;
					case 3: selectName(); break;
					case 4: selectUser(); break;
					case 5: deleteUser(); break;
					case 6: updateName(); break;
					case 7: insertUser2(); break;
					case 8: multiInsertUser(); break;
					case 0: System.out.println("\n[프로그램 종료]\n"); break;
					default: System.out.println("\n[메뉴 번호만 입력하세요]\n");
					}

					System.out.println("\n-------------------------------------\n");

				} catch (InputMismatchException e) {
					// Scanner를 이용한 입력 시 자료형이 잘못된 경우
					System.out.println("\n***잘못 입력 하셨습니다***\n");

					input = -1; // 잘못 입력해서 while문 멈추는걸 방지
					sc.nextLine(); // 입력 버퍼에 남아있는 잘못된 문자 제거

				} catch (Exception e) {
					// 발생되는 예외를 모두 해당 catch 구문으로 모아서 처리
					e.printStackTrace();
				}

			} while (input != 0);

		} // mainMenu() 종료

		/**
		 *  8. 여러 User 등록하기
		 */
		private void multiInsertUser() throws Exception{
			
			/*
			 * 등록할 User 수 : 2
			 * 
			 * 1번째 userId : user100
			 * -> 사용가능한 ID입니다
			 * 1번째 userPw : pass100
			 * 1번째 userName : 유저백
			 * ----------------------
			 * 2번째 userId : user200
			 * -> 사용 가능한 ID입니다
			 * 2번째 userPw : pass200
			 * 2번째 userName : 유저이백
			 * 
			 * --전체 삽입 성공시 commit / 하나라도 실패 시 rollback
			 * 
			 */
			
			System.out.println("\n=== 8. 여러 User 등록하기 ===\n");
			
			System.out.println("등록할 User 수 : ");
			int input = sc.nextInt();
			sc.nextLine();
			
			// 입력 받은 회원 정보를 저장할 List 객체 생성
			List<User> userList = new ArrayList<>();
			
			for(int i = 0; i < input; i++) {
				
				String userId = null;
				
				while(true) {
					System.out.print((i+1)+"번째 userId : ");
					userId = sc.next();
				
					// 입력받은 userId가 중복인지 검사하는 
					// 서비스 호출 후 결과 반환
					// 결과 (int, 중복 == 1, 아니면 == 0)
					int count = service.idCheck(userId);
					
					if(count == 0) {
						System.out.println("사용 가능한 아이디입니다.\n");
						break;
						
					}else {
						System.out.println("동일한 ID가 존재합니다. 다시 입력하세요.");
					}
				}
					System.out.print((i+1)+"번째 userPw : ");
					String userPw = sc.next();
					
					System.out.print((i+1)+"번째 userName : ");
					String userName = sc.next();
				
					System.out.println("-----------------------------------------");
					
					// 입력 받은 값 3개를 한번에 묶어서 전달하도록 User 객체 생성하고
					// userList에 추가
					
					User user = new User();
					
					user.setUserId(userId);
					user.setUserPw(userPw);
					user.setUserName(userName);
					
					userList.add(user);
			}
			
			// 입력 받은 모든 사용자를 insert하는 서비스 호출
			
			int result = service.multiInsertUser(userList);
			
			if (result == userList.size()) {
				System.out.println("전체 삽입 성공");
			} else {
				System.out.println("삽입 실패");
			}
			
			
			
		}

		private void insertUser2() throws Exception{

			System.out.println("\n=== 7. User 등록(중복검사) ===\n");
			
			String userId = null;
			
			while(true) {
				System.out.println("아이디 입력 : ");
				userId = sc.next();
			
				// 입력받은 userId가 중복인지 검사하는 
				// 서비스 호출 후 결과 반환
				// 결과 (int, 중복 == 1, 아니면 == 0)
				int count = service.idCheck(userId);
				
				if(count == 0) {
					System.out.println("사용 가능한 아이디입니다.\n");
					break;
					
				}else {
					System.out.println("동일한 ID가 존재합니다. 다시 입력하세요.");
				}
			}
				System.out.println("비밀번호 입력 : ");
				String userPw = sc.next();
				
				System.out.println("이름 입력 : ");
				String userName = sc.next();
				
				User user = new User();
				
				user.setUserId(userId);
				user.setUserPw(userPw);
				user.setUserName(userName);
				
				int result = service.insertUser(user);
				
				if (result>0) {
					System.out.println("\n"+userId + "사용자가 등록되었습니다\n");
					
				} else {
					System.out.println("\n***등록실패***\n");
				}
				
			
			
		}

		private void updateName() throws Exception{
			
			System.out.println("\n=== 6. User 이름 수정 ===\n");
			
			System.out.println("아이디 입력 :");
			String userId = sc.nextLine();
			
			System.out.println("비밀번호 입력 :");
			String userPw = sc.nextLine();
		
			int userNo = service.selectUserNo(userId, userPw);
			
			
			if(userNo == 0) {
				System.out.println("옳바른 아이디, 비밀번호를 입력하세요.");
			}else {
				System.out.println("바꿀 이름 입력 :");
				String newName = sc.nextLine();
				
				int result = service.updateName(newName, userNo);
				
				if(result>0)
				System.out.println("수정이 완료되었습니다");
				
				else System.out.println("수정을 실패하였습니다");
				
			}
			
			
			
		}

		private void deleteUser() throws Exception{
			
			System.out.println("\n=== 5. User 삭제 ===\n");
			System.out.println("삭제할 User 번호를 입력하세요 : ");
			int input = sc.nextInt();
			
			
			
			int result = service.deleteUser(input);
			
			if(result>0) {
				System.out.println("삭제 성공");
			}else {
				System.out.println("검색 번호가 없습니다");
			}
		}

		private void selectUser() throws Exception{
			
			System.out.println("\n=== 4. User중 유저번호에 검색어가 포함된 유저 조회===\n");
			
			System.out.println("사용자 번호 입력 : ");
			int input = sc.nextInt();
			
			User user = service.selectNo(input);
			
			if(user == null) {
				System.out.println("검색 결과 없음");
				return;
			}
			
			System.out.println(user);
			
		}

		private void selectName() throws Exception{
			
			System.out.println("\n=== 3. User 중 이름에 검색어가 포함된 유저 조회 ===\n");
			
			System.out.println("검색어 입력 : ");
			String keyword = sc.next();
			
			List<User> name = service.selectName(keyword);
			
			if(name.isEmpty()) {
				System.out.println("검색 결과 없음");
				return;
			}
			
			for(User n : name) {
				System.out.println(n);
			};
			
		}

		private void selectAll() throws Exception {
			
			System.out.println("\n=== 2. User 전체 조회 ===\n");
			
			List<User> userList = service.selectAll();
			
			// 조회 결과가 없을 경우
			if(userList.isEmpty()) { // userList가 비어있다면
				System.out.println("\n*** 조회 결과가 없습니다 ***\n");
				return;
			}
			
			// 조회 결과가 없을 경우
			for(User us : userList) {
				
				System.out.println(us);
				
			}
			
			
		}

		/**
		 * 1. User 등록 관련 View
		 */
		private void insertUser() throws Exception{
			
			System.out.println("\n=== 1. User 등록 ===\n");
			
			System.out.print("ID : ");
			String userId = sc.next();
			
			System.out.print("PW : ");
			String userPw = sc.next();
			
			System.out.print("Name : ");
			String userName = sc.next();
			
			// 입력받은 값 3개를 한번에 묶어서 전달할 수 있도록
			// User DTO 객체를 생성한 후 필드에 값을 세팅
			User user = new User();
			
			// setter 이용
			user.setUserId(userId);
			user.setUserPw(userPw);
			user.setUserName(userName);
			
			// 서비스 호출(INSERT) 후 결과 반환(int, 결과 행의 개수) 받기
			int result = service.insertUser(user);
			
			// 반환된 결과에 따라 출력할 내용 선택
			if (result>0) {
				System.out.println("\n"+userId + "사용자가 등록되었습니다\n");
				
			} else {
				System.out.println("\n***등록실패***\n");
			}
			
		}
}
