package kr.co.ledger.service;

import org.mindrot.jbcrypt.BCrypt;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import kr.co.ledger.dao.UserDAO;
import kr.co.ledger.dto.UserDTO;

public class UserService {

	private static UserService instance = new UserService();
	private UserService() {}
	public static UserService getInstance() { return instance; }
	
	// 회원가입
	public boolean registerUser(UserDTO dto) {
		String plainPw = dto.getUserPw();
		String hashedPw = BCrypt.hashpw(plainPw, BCrypt.gensalt());
		dto.setUserPw(hashedPw);
		return UserDAO.getInstance().insertUser(dto);
	}
	
	// 로그인
	public UserDTO login(String id, String plainPw) {
		UserDTO user = UserDAO.getInstance().getUserById(id);
		
		if (user != null) {
			if(BCrypt.checkpw(plainPw, user.getUserPw())) {
				user.setUserPw(null);
				return user;
			}
		}
		return null;
	}
	
	// 아이디 찾기
	public String findUserId(String userEmail, String userPhone, String userBirth) throws Exception {
	    return UserDAO.getInstance().findUserId(userEmail, userPhone, userBirth);
	}
	
	// 임시 비밀번호 발급
	public String issueTempPassword(String userId, String userEmail, String userPhone, String userBirth) throws Exception {
	    String tempPw = java.util.UUID.randomUUID().toString().substring(0, 8);
	    String hashedTempPw = org.mindrot.jbcrypt.BCrypt.hashpw(tempPw, org.mindrot.jbcrypt.BCrypt.gensalt());
	    int result = UserDAO.getInstance().updateTempPw(hashedTempPw, userId, userEmail, userPhone, userBirth);
	    if (result > 0) {
	        return tempPw; 
	    }
	    return null;
	}
	
	// 마이페이지
	public UserDTO getUserInfo(String userId) throws Exception {
	    return UserDAO.getInstance().getUserInfo(userId);
	}
	
	// 개인정보 수정
	public boolean updateUserInfo(UserDTO user) throws Exception {
	    String hashedPw = org.mindrot.jbcrypt.BCrypt.hashpw(user.getUserPw(), org.mindrot.jbcrypt.BCrypt.gensalt());
	    user.setUserPw(hashedPw);
	    
	    return UserDAO.getInstance().updateUserInfo(user) > 0;
	}
	
	// 회원탈퇴
	public boolean withdrawUser(String userId) throws Exception {
	    
	    String uuid = UUID.randomUUID().toString().replace("-", ""); 
	    String delId = "del_" + uuid.substring(0, 25); 
	    String delPw = UUID.randomUUID().toString(); 
	    String delNick = "탈퇴자_" + uuid.substring(0, 6);
	    String delEmail = uuid + "@del.com"; 
	    String delPhone = "010" + String.format("%08d", ThreadLocalRandom.current().nextInt(100000000));
	    String delBirth = String.format("%08d", ThreadLocalRandom.current().nextInt(100000000));

	    return UserDAO.getInstance().withdrawUser(delId, delPw, delNick, delEmail, delPhone, delBirth, userId) > 0;
	}
	
	// 휴면계정처리
	public void processDormantUsers() {
	    try {
	        int count = UserDAO.getInstance().updateDormantUsers();
	        if (count > 0) {
	            System.out.println("[시스템] " + count + "명의 회원이 휴면(D) 상태로 전환되었습니다.");
	        }
	    } catch (Exception e) {
	        System.out.println("[시스템 에러] 휴면 계정 업데이트 중 오류 발생");
	        e.printStackTrace();
	    }
	}
	
	// 휴면해제처리
	public boolean wakeupUser(String userId) throws Exception {
	    return UserDAO.getInstance().wakeupUser(userId) > 0;
	}
	
	// 개인 가계부 공개 비공개 설정
	public void updateBookOpenYn(int userNum, String bookOpenYn) throws Exception {
	    UserDAO.getInstance().updateBookOpenYn(userNum, bookOpenYn);
	}
	
	// 타인 가계부 검색 후 열람
	public List<UserDTO> searchPublicUsersById(String keyword) throws Exception {
	    return UserDAO.getInstance().searchPublicUsersById(keyword);
	}
}
