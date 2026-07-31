package kr.co.ledger.dto;

public class InvitationDTO {
	
	private int inviteNum;
    private int groupNum;
    private int inviterNum;
    private int inviteeNum;
    private String inviteStatus;
    private String createdAt;

    public InvitationDTO() {}

	public int getInviteNum() {
		return inviteNum;
	}

	public void setInviteNum(int inviteNum) {
		this.inviteNum = inviteNum;
	}

	public int getGroupNum() {
		return groupNum;
	}

	public void setGroupNum(int groupNum) {
		this.groupNum = groupNum;
	}

	public int getInviterNum() {
		return inviterNum;
	}

	public void setInviterNum(int inviterNum) {
		this.inviterNum = inviterNum;
	}

	public int getInviteeNum() {
		return inviteeNum;
	}

	public void setInviteeNum(int inviteeNum) {
		this.inviteeNum = inviteeNum;
	}

	public String getInviteStatus() {
		return inviteStatus;
	}

	public void setInviteStatus(String inviteStatus) {
		this.inviteStatus = inviteStatus;
	}

	public String getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(String createdAt) {
		this.createdAt = createdAt;
	}
}
