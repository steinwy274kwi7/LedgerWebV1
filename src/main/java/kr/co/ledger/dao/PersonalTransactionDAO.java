package kr.co.ledger.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import kr.co.ledger.dto.CalendarDTO;
import kr.co.ledger.dto.ChartDTO;
import kr.co.ledger.dto.PersonalTransactionDTO;
import kr.co.ledger.dto.RatioDTO;
import kr.co.ledger.dto.TrendDTO;
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
    
    // 개인 6개월 추이
    public List<TrendDTO> getRecent6MonthsTrend(int userNum, String targetMonth) throws Exception {
        String sql = SqlManager.getSql("getRecent6MonthsTrend");
        List<TrendDTO> list = new ArrayList<>();
        
        try (Connection conn = DBManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userNum);
            pstmt.setString(2, targetMonth);
            pstmt.setString(3, targetMonth);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    TrendDTO dto = new TrendDTO();
                    dto.setMonth(rs.getString("TRANS_MONTH"));
                    dto.setTotalIncome(rs.getLong("TOTAL_INCOME"));
                    dto.setTotalExpense(rs.getLong("TOTAL_EXPENSE"));
                    list.add(dto);
                }
            }
        }
        return list;
    }
    
    // 개인 달력 뷰
    public List<CalendarDTO> getMonthlyCalendarData(int userNum, String targetMonth, String type, String keyword) throws Exception {
        StringBuilder sql = new StringBuilder(SqlManager.getSql("getMonthlyCalendarDataBase"));
        
        List<Object> params = new ArrayList<>();
        params.add(userNum);
        
        if (targetMonth != null && !targetMonth.isEmpty()) {
            sql.append(" AND TO_CHAR(TRANS_DATE, 'YYYY-MM') = ?");
            params.add(targetMonth);
        }
        if (type != null && !type.equals("ALL")) {
            sql.append(" AND TRANS_TYPE = ?");
            params.add(type);
        }
        if (keyword != null && !keyword.isEmpty()) {
            sql.append(" AND TRANS_MEMO LIKE ?");
            params.add("%" + keyword + "%");
        }
        
        sql.append(" GROUP BY TO_CHAR(TRANS_DATE, 'YYYY-MM-DD') ORDER BY TRANS_DATE");

        List<CalendarDTO> list = new ArrayList<>();
        try (Connection conn = DBManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {
            
            for (int i = 0; i < params.size(); i++) {
                pstmt.setObject(i + 1, params.get(i));
            }
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    CalendarDTO dto = new CalendarDTO();
                    dto.setDate(rs.getString("TRANS_DATE"));
                    dto.setDailyIncome(rs.getLong("DAILY_INCOME"));
                    dto.setDailyExpense(rs.getLong("DAILY_EXPENSE"));
                    list.add(dto);
                }
            }
        }
        return list;
    }

    // 개인 리스트 뷰
    public List<PersonalTransactionDTO> getTransactionList(int userNum, String month, String date, String type, String keyword) throws Exception {
        StringBuilder sql = new StringBuilder(SqlManager.getSql("getPersonalTransactionListBase"));
        
        List<Object> params = new ArrayList<>();
        params.add(userNum);
        
        if (date != null && !date.isEmpty()) {
            sql.append(" AND TO_CHAR(T.TRANS_DATE, 'YYYY-MM-DD') = ?");
            params.add(date);
        } else if (month != null && !month.isEmpty()) {
            sql.append(" AND TO_CHAR(T.TRANS_DATE, 'YYYY-MM') = ?");
            params.add(month);
        }
        
        if (type != null && !type.equals("ALL")) {
            sql.append(" AND T.TRANS_TYPE = ?");
            params.add(type);
        }
        
        if (keyword != null && !keyword.isEmpty()) {
            sql.append(" AND T.TRANS_MEMO LIKE ?");
            params.add("%" + keyword + "%");
        }
        
        sql.append(" ORDER BY T.TRANS_DATE DESC, T.TRANS_NUM DESC");

        List<PersonalTransactionDTO> list = new ArrayList<>();
        
        try (Connection conn = DBManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {
            
            for (int i = 0; i < params.size(); i++) {
                pstmt.setObject(i + 1, params.get(i));
            }
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    PersonalTransactionDTO dto = new PersonalTransactionDTO();
                    dto.setTransNum(rs.getInt("TRANS_NUM"));
                    dto.setTransType(rs.getString("TRANS_TYPE"));
                    dto.setCategoryNum(rs.getInt("CATEGORY_NUM"));
                    dto.setCategoryName(rs.getString("CATEGORY_NAME"));
                    dto.setTransAmount(rs.getLong("TRANS_AMOUNT"));
                    dto.setTransDate(rs.getString("TRANS_DATE"));
                    dto.setTransMemo(rs.getString("TRANS_MEMO"));
                    list.add(dto);
                }
            }
        }
        return list;
    }
    
 // 1. 개인 수입지출 등록
    public void insertTransaction(PersonalTransactionDTO dto) throws Exception {
        String sql = SqlManager.getSql("insertPersonalTransaction");
        try (Connection conn = DBManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, dto.getUserNum());
            pstmt.setInt(2, dto.getCategoryNum());
            pstmt.setString(3, dto.getTransType());
            pstmt.setLong(4, dto.getTransAmount());
            pstmt.setString(5, dto.getTransDate());
            pstmt.setString(6, dto.getTransMemo());
            pstmt.executeUpdate();
        }
    }

    // 개인 수입지출 수정
    public void updateTransaction(PersonalTransactionDTO dto) throws Exception {
        String sql = SqlManager.getSql("updatePersonalTransaction");
        try (Connection conn = DBManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, dto.getCategoryNum());
            pstmt.setString(2, dto.getTransType());
            pstmt.setLong(3, dto.getTransAmount());
            pstmt.setString(4, dto.getTransDate());
            pstmt.setString(5, dto.getTransMemo());
            pstmt.setInt(6, dto.getTransNum());
            pstmt.setInt(7, dto.getUserNum());
            pstmt.executeUpdate();
        }
    }

    // 개인 수입지출 삭제
    public void deleteTransaction(int transNum, int userNum) throws Exception {
        String sql = SqlManager.getSql("deletePersonalTransaction");
        try (Connection conn = DBManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, transNum);
            pstmt.setInt(2, userNum);
            pstmt.executeUpdate();
        }
    }
}