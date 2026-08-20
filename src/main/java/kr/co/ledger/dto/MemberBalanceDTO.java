package kr.co.ledger.dto;

public class MemberBalanceDTO {
    private String nickname;
    private long balance; // (+면 받을 돈, -면 낼 돈)
    private int userNum;
   
    public MemberBalanceDTO() {}

    // 필드를 꽉 채워서 만드는 파라미터 생성자
    public MemberBalanceDTO(String nickname, long balance) {
        this.nickname = nickname;
        this.balance = balance;
    }

    public int getUserNum() { return userNum; }
    public void setUserNum(int userNum) { this.userNum = userNum; }
    
    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }

    public long getBalance() { return balance; }
    public void setBalance(long balance) { this.balance = balance; }
}