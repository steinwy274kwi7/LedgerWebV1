package kr.co.ledger.dto;

public class SettlementSnapshotDTO {

	private int settleNum;
    private int periodNum;
    private int payerUserNum;
    private int receiverUserNum;
    private long settleAmount;
    private String createdAt;

    public SettlementSnapshotDTO() {}

	public int getSettleNum() {
		return settleNum;
	}

	public void setSettleNum(int settleNum) {
		this.settleNum = settleNum;
	}

	public int getPeriodNum() {
		return periodNum;
	}

	public void setPeriodNum(int periodNum) {
		this.periodNum = periodNum;
	}

	public int getPayerUserNum() {
		return payerUserNum;
	}

	public void setPayerUserNum(int payerUserNum) {
		this.payerUserNum = payerUserNum;
	}

	public int getReceiverUserNum() {
		return receiverUserNum;
	}

	public void setReceiverUserNum(int receiverUserNum) {
		this.receiverUserNum = receiverUserNum;
	}

	public long getSettleAmount() {
		return settleAmount;
	}

	public void setSettleAmount(long settleAmount) {
		this.settleAmount = settleAmount;
	}

	public String getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(String createdAt) {
		this.createdAt = createdAt;
	}
}
