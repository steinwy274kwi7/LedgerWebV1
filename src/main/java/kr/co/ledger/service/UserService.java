package kr.co.ledger.service;

import org.mindrot.jbcrypt.BCrypt;

import kr.co.ledger.dao.UserDAO;
import kr.co.ledger.dto.UserDTO;

public class UserService {

	private static UserService instance = new UserService();
	private UserService() {}
	public static UserService getInstance() { return instance; }
	
	public boolean registerUser(UserDTO dto) {
		String plainPw = dto.getUserPw();
		String hashedPw = BCrypt.hashpw(plainPw, BCrypt.gensalt());
		dto.setUserPw(hashedPw);
		return UserDAO.getInstance().insertUser(dto);
	}
	
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
}
