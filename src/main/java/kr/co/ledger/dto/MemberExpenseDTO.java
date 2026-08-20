package kr.co.ledger.dto;

public class MemberExpenseDTO {
    private int userNum;
    private long spentAmount;
    private String nickname;
    
    public String getNickname() {
		return nickname;
	}
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	public MemberExpenseDTO() {}
    public int getUserNum() { return userNum; }
    public void setUserNum(int userNum) { this.userNum = userNum; }
    public long getSpentAmount() { return spentAmount; }
    public void setSpentAmount(long spentAmount) { this.spentAmount = spentAmount; }
}