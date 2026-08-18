package kr.co.ledger.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import kr.co.ledger.dto.ChartDTO;
import kr.co.ledger.dto.RatioDTO;
import kr.co.ledger.util.DBManager;
import kr.co.ledger.util.SqlManager;

public class PersonalTransactionDAO {
	
    private static PersonalTransactionDAO instance = new PersonalTransactionDAO();
    private PersonalTransactionDAO() {}
    public static PersonalTransactionDAO getInstance() { return instance; }

    // 개인 카테고리 합계
    public List<ChartDTO> getCategorySumForChart(int userNum, String transType, String targetMonth) throws Exception {
        String sql = SqlManager.getSql("getCategorySumForChart");
        List<ChartDTO> list = new ArrayList<>();
        
        try (Connection conn = DBManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userNum);
            pstmt.setString(2, transType);
            pstmt.setString(3, targetMonth);
            
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
    
    // 개인 흑자, 적자 비율
    public RatioDTO getMonthlyTotalInOut(int userNum, String targetMonth) throws Exception {
        String sql = SqlManager.getSql("getMonthlyTotalInOut");
        RatioDTO dto = new RatioDTO(); 
        
        try (Connection conn = DBManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userNum);
            pstmt.setString(2, targetMonth); 
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    dto.setTotalIncome(rs.getLong("TOTAL_INCOME"));
                    dto.setTotalExpense(rs.getLong("TOTAL_EXPENSE"));
                }
            }
        }
        return dto;
    }
    
}