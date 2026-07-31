package kr.co.ledger.dto;

public class PersonalCategoryDTO {
	
	private int categoryNum;
    private int userNum;
    private String categoryName;
    private String categoryType;
    private String isDefaultYn;
    private String useYn;
    private String createdAt;

    public PersonalCategoryDTO() {}

	public int getCategoryNum() {
		return categoryNum;
	}

	public void setCategoryNum(int categoryNum) {
		this.categoryNum = categoryNum;
	}

	public int getUserNum() {
		return userNum;
	}

	public void setUserNum(int userNum) {
		this.userNum = userNum;
	}

	public String getCategoryName() {
		return categoryName;
	}

	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}

	public String getCategoryType() {
		return categoryType;
	}

	public void setCategoryType(String categoryType) {
		this.categoryType = categoryType;
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
