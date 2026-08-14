package kr.co.ledger.service;

import org.mindrot.jbcrypt.BCrypt;

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
	
}
