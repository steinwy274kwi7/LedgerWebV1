package kr.co.ledger.dto;

public class PersonalTransactionDTO {
	
	private int transNum;
    private int userNum;
    private int categoryNum;
    private String transType;
    private long transAmount; 
    private String transDate;
    private String transMemo;
    private String useYn;
    private String createdAt;
    private String categoryName;
    
    public String getCategoryName() {
		return categoryName;
	}

	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}

	public PersonalTransactionDTO() {}

	public int getTransNum() {
		return transNum;
	}

	public void setTransNum(int transNum) {
		this.transNum = transNum;
	}

	public int getUserNum() {
		return userNum;
	}

	public void setUserNum(int userNum) {
		this.userNum = userNum;
	}

	public int getCategoryNum() {
		return categoryNum;
	}

	public void setCategoryNum(int categoryNum) {
		this.categoryNum = categoryNum;
	}

	public String getTransType() {
		return transType;
	}

	public void setTransType(String transType) {
		this.transType = transType;
	}

	public long getTransAmount() {
		return transAmount;
	}

	public void setTransAmount(long transAmount) {
		this.transAmount = transAmount;
	}

	public String getTransDate() {
		return transDate;
	}

	public void setTransDate(String transDate) {
		this.transDate = transDate;
	}

	public String getTransMemo() {
		return transMemo;
	}

	public void setTransMemo(String transMemo) {
		this.transMemo = transMemo;
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
