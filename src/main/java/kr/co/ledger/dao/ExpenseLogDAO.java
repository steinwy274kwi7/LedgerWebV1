package kr.co.ledger.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import kr.co.ledger.dto.ExpenseLogDTO;
import kr.co.ledger.util.DBManager;
import kr.co.ledger.util.SqlManager;

public class ExpenseLogDAO {
	
	private static ExpenseLogDAO instance = new ExpenseLogDAO();
    private ExpenseLogDAO() {}
    public static ExpenseLogDAO getInstance() { return instance; }
    
	// 방 전체 지출 변경 이력 조회
    public List<ExpenseLogDTO> getExpenseLogs(int groupNum) throws Exception {
        List<ExpenseLogDTO> list = new ArrayList<>();
        String sql = SqlManager.getSql("getExpenseLogs");
        
        try (Connection conn = DBManager.getConnection(); 
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, groupNum);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    ExpenseLogDTO dto = new ExpenseLogDTO();
                    dto.setLogNum(rs.getInt("LOG_NUM"));
                    dto.setGtransNum(rs.getInt("GTRANS_NUM"));
                    dto.setActionUserNum(rs.getInt("ACTION_USER_NUM"));
                    dto.setActionType(rs.getString("ACTION_TYPE"));
                    dto.setBeforeAmount(rs.getLong("BEFORE_AMOUNT"));
                    
                    // AFTER_AMOUNT는 삭제 시 null일 수 있으므로 rs.wasNull() 체크
                    long afterAmt = rs.getLong("AFTER_AMOUNT");
                    if (rs.wasNull()) {
                        dto.setAfterAmount(null);
                    } else {
                        dto.setAfterAmount(afterAmt);
                    }
                    
                    dto.setBeforeCategory(rs.getString("BEFORE_CATEGORY"));
                    dto.setAfterCategory(rs.getString("AFTER_CATEGORY")); 
                    dto.setCreatedAtStr(rs.getString("CREATED_AT_STR"));
                    dto.setUserNickname(rs.getString("USER_NICKNAME"));
                    dto.setTransMemo(rs.getString("TRANS_MEMO"));
                    
                    list.add(dto);
                }
            }
        }
        return list;
    }

    // 카테고리 삭제에 따른 이관 로그 벌크 인서트
    public void insertBulkLogForCategoryDelete(int categoryNum, String oldCategoryName, int actionUserNum) throws Exception {
        String sql = SqlManager.getSql("insertBulkLogForCategoryDelete");
        try (Connection conn = DBManager.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, actionUserNum);
            pstmt.setString(2, oldCategoryName);
            pstmt.setInt(3, categoryNum);
            pstmt.executeUpdate();
        }
    }
}
