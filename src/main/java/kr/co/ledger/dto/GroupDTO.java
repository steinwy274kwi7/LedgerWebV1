package kr.co.ledger.dto;

public class GroupDTO {
	
	private int groupNum;
    private String groupName;
    private String groupDesc;
    private String groupType;
    private int groupOwnerNum;
    private String groupOpenYn;
    private String settleUseYn;
    private String useYn;
    private String createdAt;
    private int memberCount;
    
	public GroupDTO() {}
	
    public int getMemberCount() {
		return memberCount;
	}

	public void setMemberCount(int memberCount) {
		this.memberCount = memberCount;
	}

	public int getGroupNum() {
		return groupNum;
	}

	public void setGroupNum(int groupNum) {
		this.groupNum = groupNum;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public String getGroupDesc() {
		return groupDesc;
	}

	public void setGroupDesc(String groupDesc) {
		this.groupDesc = groupDesc;
	}

	public String getGroupType() {
		return groupType;
	}

	public void setGroupType(String groupType) {
		this.groupType = groupType;
	}

	public int getGroupOwnerNum() {
		return groupOwnerNum;
	}

	public void setGroupOwnerNum(int groupOwnerNum) {
		this.groupOwnerNum = groupOwnerNum;
	}

	public String getGroupOpenYn() {
		return groupOpenYn;
	}

	public void setGroupOpenYn(String groupOpenYn) {
		this.groupOpenYn = groupOpenYn;
	}

	public String getSettleUseYn() {
		return settleUseYn;
	}

	public void setSettleUseYn(String settleUseYn) {
		this.settleUseYn = settleUseYn;
	}

	public String getUseYn() {
		return useYn;
	}

	public void setUseYn(String useYn) {
		this.useYn = useYn;
	}

	public String getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(String createdAt) {
		this.createdAt = createdAt;
	} 
}
