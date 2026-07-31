package kr.co.ledger.dto;

public class GroupCategoryDTO {

	private int gcategoryNum;
    private int groupNum;
    private String categoryName;
    private String isDefaultYn;
    private String useYn;
    private String createdAt;

    public GroupCategoryDTO() {}

	public int getGcategoryNum() {
		return gcategoryNum;
	}

	public void setGcategoryNum(int gcategoryNum) {
		this.gcategoryNum = gcategoryNum;
	}

	public int getGroupNum() {
		return groupNum;
	}

	public void setGroupNum(int groupNum) {
		this.groupNum = groupNum;
	}

	public String getCategoryName() {
		return categoryName;
	}

	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}

	public String getIsDefaultYn() {
		return isDefaultYn;
	}

	public void setIsDefaultYn(String isDefaultYn) {
		this.isDefaultYn = isDefaultYn;
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
