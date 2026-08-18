package kr.co.ledger.dto;

public class ChartDTO {
  
	private String categoryName;
    private long totalAmount;

    public ChartDTO() {}

	public String getCategoryName() {
		return categoryName;
	}

	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}

	public long getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(long totalAmount) {
		this.totalAmount = totalAmount;
	}

}