package kr.co.ledger.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import kr.co.ledger.dto.ChartDTO;
import kr.co.ledger.util.DBManager;
import kr.co.ledger.util.SqlManager;

public class GroupTransactionDAO {
    
    private static GroupTransactionDAO instance = new GroupTransactionDAO();
    private GroupTransactionDAO() {}
    public static GroupTransactionDAO getInstance() { return instance; }

    // 그룹 카테고리 합계
    public List<ChartDTO> getAllMyGroupCategorySumForChart(int userNum, String targetMonth) throws Exception {
        String sql = SqlManager.getSql("getAllMyGroupCategorySumForChart");
        List<ChartDTO> list = new ArrayList<>();
        
        try (Connection conn = DBManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userNum);
            pstmt.setString(2, targetMonth); 
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    ChartDTO dto = new ChartDTO();
                    dto.setCategoryName(rs.getString("CATEGORY_NAME"));
                    dto.setTotalAmount(rs.getLong("TOTAL_AMOUNT"));
                    list.add(dto);
                }
            }
        }
        return list;
    }
    
}