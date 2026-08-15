package kr.co.ledger.service;

import org.mindrot.jbcrypt.BCrypt;
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
	    // UUID에서 하이픈(-)을 제거한 32자리 난수 생성
	    String uuid = UUID.randomUUID().toString().replace("-", ""); 
	    
	    // 1. 컬럼 길이(VARCHAR2)와 UK 제약조건에 맞춘 더미 데이터 생성
	    // USER_ID: VARCHAR2(30) -> "del_" (4자) + 25자 난수 = 29자
	    String delId = "del_" + uuid.substring(0, 25); 
	    
	    // USER_PW: VARCHAR2(100) -> 긴 난수
	    String delPw = UUID.randomUUID().toString(); 
	    
	    // USER_NICKNAME: VARCHAR2(60)
	    String delNick = "탈퇴자_" + uuid.substring(0, 6);
	    
	    // USER_EMAIL: VARCHAR2(100) -> 32자 + 8자 = 40자
	    String delEmail = uuid + "@del.com"; 
	    
	    // USER_PHONE: VARCHAR2(13) -> 동시성 방어 ThreadLocalRandom 사용 (11자리)
	    String delPhone = "010" + String.format("%08d", ThreadLocalRandom.current().nextInt(100000000));
	    
	    // USER_BIRTH: VARCHAR2(8) -> 동시성 방어 ThreadLocalRandom 사용 (8자리)
	    String delBirth = String.format("%08d", ThreadLocalRandom.current().nextInt(100000000));

	    // 2. DAO 호출
	    return UserDAO.getInstance().withdrawUser(delId, delPw, delNick, delEmail, delPhone, delBirth, userId) > 0;
	}
}
