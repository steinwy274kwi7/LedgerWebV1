package kr.co.ledger.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import kr.co.ledger.dto.ChartDTO;
import kr.co.ledger.dto.GroupTransactionDTO;
import kr.co.ledger.dto.TrendDTO;
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
    
    // 그룹 6개월 추이
    public List<TrendDTO> getAllMyGroupTrendForChart(int userNum, String targetMonth) throws Exception {
        String sql = SqlManager.getSql("getAllMyGroupTrendForChart");
        List<TrendDTO> list = new ArrayList<>();
        
        try (Connection conn = DBManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userNum);
            pstmt.setString(2, targetMonth);
            pstmt.setString(3, targetMonth);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    TrendDTO dto = new TrendDTO();
                    dto.setMonth(rs.getString("TARGET_MONTH"));
                    dto.setTotalExpense(rs.getLong("TOTAL_AMOUNT"));
                    list.add(dto);
                }
            }
        }
        return list;
    }
    
    // 그룹 달력 리스트 뷰 보기
    public List<GroupTransactionDTO> getMonthlyTransactions(int groupNum, String yearMonth) throws Exception {
        List<GroupTransactionDTO> list = new ArrayList<>();
        String sql = SqlManager.getSql("getMonthlyGroupTransactions");
        
        try (Connection conn = DBManager.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, groupNum);
            pstmt.setString(2, yearMonth);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    GroupTransactionDTO dto = new GroupTransactionDTO();
                    dto.setGtransNum(rs.getInt("GTRANS_NUM"));
                    dto.setUserNum(rs.getInt("USER_NUM"));
                    dto.setTransAmount(rs.getInt("TRANS_AMOUNT"));
                    dto.setTransDate(rs.getString("TRANS_DATE"));
                    dto.setTransMemo(rs.getString("TRANS_MEMO"));
                    dto.setUserNickname(rs.getString("USER_NICKNAME"));
                    dto.setCategoryName(rs.getString("CATEGORY_NAME"));
                    
                    list.add(dto);
                }
            }
        }
        return list;
    }
    
}