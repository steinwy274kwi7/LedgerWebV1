package kr.co.ledger.dto;

public class UserDTO {
	
	private int userNum;
    private String userId;
    private String userPw;
    private String userNickname;
    private String userEmail;
    private String userPhone;
    private String userBirth;
    private String userStatus;
    private String userCreatedAt;
    private String userLastLoginAt;
    private String bookOpenYn;
    
    public UserDTO() {}

	public int getUserNum() {
		return userNum;
	}

	public void setUserNum(int userNum) {
		this.userNum = userNum;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getUserPw() {
		return userPw;
	}

	public void setUserPw(String userPw) {
		this.userPw = userPw;
	}

	public String getUserNickname() {
		return userNickname;
	}

	public void setUserNickname(String userNickname) {
		this.userNickname = userNickname;
	}

	public String getUserEmail() {
		return userEmail;
	}

	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
	}

	public String getUserPhone() {
		return userPhone;
	}

	public void setUserPhone(String userPhone) {
		this.userPhone = userPhone;
	}

	public String getUserBirth() {
		return userBirth;
	}

	public void setUserBirth(String userBirth) {
		this.userBirth = userBirth;
	}

	public String getUserStatus() {
		return userStatus;
	}

	public void setUserStatus(String userStatus) {
		this.userStatus = userStatus;
	}

	public String getUserCreatedAt() {
		return userCreatedAt;
	}

	public void setUserCreatedAt(String userCreatedAt) {
		this.userCreatedAt = userCreatedAt;
	}

	public String getUserLastLoginAt() {
		return userLastLoginAt;
	}

	public void setUserLastLoginAt(String userLastLoginAt) {
		this.userLastLoginAt = userLastLoginAt;
	}

	public String getBookOpenYn() {
		return bookOpenYn;
	}

	public void setBookOpenYn(String bookOpenYn) {
		this.bookOpenYn = bookOpenYn;
	}
}
