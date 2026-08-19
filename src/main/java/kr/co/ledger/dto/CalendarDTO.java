package kr.co.ledger.dto;

public class CalendarDTO {
	private String date;
    private long dailyIncome;
    private long dailyExpense;
    
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public long getDailyIncome() {
		return dailyIncome;
	}
	public void setDailyIncome(long dailyIncome) {
		this.dailyIncome = dailyIncome;
	}
	public long getDailyExpense() {
		return dailyExpense;
	}
	public void setDailyExpense(long dailyExpense) {
		this.dailyExpense = dailyExpense;
	}
    
}
