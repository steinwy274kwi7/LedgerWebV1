package kr.co.ledger.dto;

public class GroupTransactionDTO {

	private int gtransNum;
    private int groupNum;
    private int periodNum;
    private int gcategoryNum;
    private int userNum;
    private long transAmount;
    private String transDate;
    private String transMemo;
    private String useYn;
    private String createdAt;

    public GroupTransactionDTO() {}

	public int getGtransNum() {
		return gtransNum;
	}

	public void setGtransNum(int gtransNum) {
		this.gtransNum = gtransNum;
	}

	public int getGroupNum() {
		return groupNum;
	}

	public void setGroupNum(int groupNum) {
		this.groupNum = groupNum;
	}

	public int getPeriodNum() {
		return periodNum;
	}

	public void setPeriodNum(int periodNum) {
		this.periodNum = periodNum;
	}

	public int getGcategoryNum() {
		return gcategoryNum;
	}

	public void setGcategoryNum(int gcategoryNum) {
		this.gcategoryNum = gcategoryNum;
	}

	public int getUserNum() {
		return userNum;
	}

	public void setUserNum(int userNum) {
		this.userNum = userNum;
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
