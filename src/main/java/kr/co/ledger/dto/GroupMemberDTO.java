package kr.co.ledger.dto;

public class GroupMemberDTO {
    private int memberNum;
    private int groupNum;
    private int userNum;
    private String joinDate;
    private String memberStatus;
    private String userId;
    private String userNickname;

    public int getMemberNum() { return memberNum; }
    public void setMemberNum(int memberNum) { this.memberNum = memberNum; }
    
    public int getGroupNum() { return groupNum; }
    public void setGroupNum(int groupNum) { this.groupNum = groupNum; }
    
    public int getUserNum() { return userNum; }
    public void setUserNum(int userNum) { this.userNum = userNum; }
    
    public String getJoinDate() { return joinDate; }
    public void setJoinDate(String joinDate) { this.joinDate = joinDate; }
    
    public String getMemberStatus() { return memberStatus; }
    public void setMemberStatus(String memberStatus) { this.memberStatus = memberStatus; }
    
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    
    public String getUserNickname() { return userNickname; }
    public void setUserNickname(String userNickname) { this.userNickname = userNickname; }
}