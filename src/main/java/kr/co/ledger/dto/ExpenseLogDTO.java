package kr.co.ledger.dto;

public class ExpenseLogDTO {
	
	private int logNum;
    private int gtransNum;
    private int actionUserNum;
    private String actionType;
    private long beforeAmount;
    private long afterAmount;
    private String beforeCategory;
    private String afterCategory;
    private String createdAt;

    public ExpenseLogDTO() {}

	public int getLogNum() {
		return logNum;
	}

	public void setLogNum(int logNum) {
		this.logNum = logNum;
	}

	public int getGtransNum() {
		return gtransNum;
	}

	public void setGtransNum(int gtransNum) {
		this.gtransNum = gtransNum;
	}

	public int getActionUserNum() {
		return actionUserNum;
	}

	public void setActionUserNum(int actionUserNum) {
		this.actionUserNum = actionUserNum;
	}

	public String getActionType() {
		return actionType;
	}

	public void setActionType(String actionType) {
		this.actionType = actionType;
	}

	public long getBeforeAmount() {
		return beforeAmount;
	}

	public void setBeforeAmount(long beforeAmount) {
		this.beforeAmount = beforeAmount;
	}

	public long getAfterAmount() {
		return afterAmount;
	}

	public void setAfterAmount(long afterAmount) {
		this.afterAmount = afterAmount;
	}

	public String getBeforeCategory() {
		return beforeCategory;
	}

	public void setBeforeCategory(String beforeCategory) {
		this.beforeCategory = beforeCategory;
	}

	public String getAfterCategory() {
		return afterCategory;
	}

	public void setAfterCategory(String afterCategory) {
		this.afterCategory = afterCategory;
	}

	public String getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(String createdAt) {
		this.createdAt = createdAt;
	}
}
