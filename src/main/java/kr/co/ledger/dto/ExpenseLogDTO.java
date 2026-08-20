package kr.co.ledger.dto;

public class ExpenseLogDTO {
    private int logNum;
    private int gtransNum;
    private int actionUserNum;
    private String actionType;
    private long beforeAmount;
    private Long afterAmount; // null 허용을 위해 객체 타입 Long 사용
    private String beforeCategory;
    private String afterCategory;
    private String createdAtStr; // TO_CHAR로 포맷팅된 날짜
    
    // JOIN을 통해 가져올 추가 데이터
    private String userNickname;
    private String transMemo;
    
    public ExpenseLogDTO() {}

    public int getLogNum() { return logNum; }
    public void setLogNum(int logNum) { this.logNum = logNum; }

    public int getGtransNum() { return gtransNum; }
    public void setGtransNum(int gtransNum) { this.gtransNum = gtransNum; }

    public int getActionUserNum() { return actionUserNum; }
    public void setActionUserNum(int actionUserNum) { this.actionUserNum = actionUserNum; }

    public String getActionType() { return actionType; }
    public void setActionType(String actionType) { this.actionType = actionType; }

    public long getBeforeAmount() { return beforeAmount; }
    public void setBeforeAmount(long beforeAmount) { this.beforeAmount = beforeAmount; }

    public Long getAfterAmount() { return afterAmount; }
    public void setAfterAmount(Long afterAmount) { this.afterAmount = afterAmount; }

    public String getBeforeCategory() { return beforeCategory; }
    public void setBeforeCategory(String beforeCategory) { this.beforeCategory = beforeCategory; }

    public String getAfterCategory() { return afterCategory; }
    public void setAfterCategory(String afterCategory) { this.afterCategory = afterCategory; }

    public String getCreatedAtStr() { return createdAtStr; }
    public void setCreatedAtStr(String createdAtStr) { this.createdAtStr = createdAtStr; }

    public String getUserNickname() { return userNickname; }
    public void setUserNickname(String userNickname) { this.userNickname = userNickname; }

    public String getTransMemo() { return transMemo; }
    public void setTransMemo(String transMemo) { this.transMemo = transMemo; }
}