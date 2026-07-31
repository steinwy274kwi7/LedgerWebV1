package kr.co.ledger.dto;

public class LedgerPeriodDTO {

	private int periodNum;
    private int groupNum;
    private int periodSeq;
    private String periodStartDate;
    private String periodEndDate;
    private String periodStatus;
    private String createdAt;

    public LedgerPeriodDTO() {}

	public int getPeriodNum() {
		return periodNum;
	}

	public void setPeriodNum(int periodNum) {
		this.periodNum = periodNum;
	}

	public int getGroupNum() {
		return groupNum;
	}

	public void setGroupNum(int groupNum) {
		this.groupNum = groupNum;
	}

	public int getPeriodSeq() {
		return periodSeq;
	}

	public void setPeriodSeq(int periodSeq) {
		this.periodSeq = periodSeq;
	}

	public String getPeriodStartDate() {
		return periodStartDate;
	}

	public void setPeriodStartDate(String periodStartDate) {
		this.periodStartDate = periodStartDate;
	}

	public String getPeriodEndDate() {
		return periodEndDate;
	}

	public void setPeriodEndDate(String periodEndDate) {
		this.periodEndDate = periodEndDate;
	}

	public String getPeriodStatus() {
		return periodStatus;
	}

	public void setPeriodStatus(String periodStatus) {
		this.periodStatus = periodStatus;
	}

	public String getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(String createdAt) {
		this.createdAt = createdAt;
	}
}
