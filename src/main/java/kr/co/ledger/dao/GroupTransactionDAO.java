package kr.co.ledger.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import kr.co.ledger.dto.ChartDTO;
import kr.co.ledger.dto.ExpenseLogDTO;
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
                    dto.setPeriodStatus(rs.getString("PERIOD_STATUS"));
                    
                    list.add(dto);
                }
            }
        }
        return list;
    }
    
    // 공동 지출 내역 등록
    public boolean insertTransaction(GroupTransactionDTO dto) throws Exception {
        String sql = SqlManager.getSql("insertGroupTransaction");
        try (Connection conn = DBManager.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, dto.getGroupNum());
            pstmt.setInt(2, dto.getGroupNum()); // 서브쿼리용
            pstmt.setInt(3, dto.getGcategoryNum());
            pstmt.setInt(4, dto.getUserNum());  // 세션에서 가져온 본인 번호
            pstmt.setLong(5, dto.getTransAmount());
            pstmt.setString(6, dto.getTransDate());
            pstmt.setString(7, dto.getTransMemo());
            
            int count = pstmt.executeUpdate();
            return count > 0;
        }
    }
    
    // 지출 단건 조회 (수정/삭제 전 기존 데이터 및 권한 확인용)
    public GroupTransactionDTO getTransaction(int gtransNum) throws Exception {
        String sql = SqlManager.getSql("getGroupTransaction");
        GroupTransactionDTO dto = null;
        try (Connection conn = DBManager.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, gtransNum);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    dto = new GroupTransactionDTO();
                    dto.setGtransNum(rs.getInt("GTRANS_NUM"));
                    dto.setUserNum(rs.getInt("USER_NUM")); // 결제자 번호 (권한 체크용)
                    dto.setTransAmount(rs.getLong("TRANS_AMOUNT")); // 기존 금액 (로그용)
                    dto.setCategoryName(rs.getString("CATEGORY_NAME")); // 기존 카테고리명 (로그용)
                }
            }
        }
        return dto;
    }

    // 지출 수정 트랜잭션 (내역 업데이트 + 로그 기록)
    public boolean updateTransactionWithLog(GroupTransactionDTO newDto, int actionUserNum, String oldCat, String newCat, long oldAmt) throws Exception {
        Connection conn = null;
        try {
            conn = DBManager.getConnection();
            conn.setAutoCommit(false); // 트랜잭션 시작 (자동 커밋 방지)

            // A. 지출 내역 진짜 수정하기
            try (PreparedStatement pstmt1 = conn.prepareStatement(SqlManager.getSql("updateGroupTransaction"))) {
                pstmt1.setInt(1, newDto.getGcategoryNum());
                pstmt1.setLong(2, newDto.getTransAmount());
                pstmt1.setString(3, newDto.getTransDate());
                pstmt1.setString(4, newDto.getTransMemo());
                pstmt1.setInt(5, newDto.getGtransNum());
                pstmt1.executeUpdate();
            }

            // B. 변경된 히스토리를 로그 테이블에 남기기 (ACTION_TYPE = 'U')
            try (PreparedStatement pstmt2 = conn.prepareStatement(SqlManager.getSql("insertExpenseLog"))) {
                pstmt2.setInt(1, newDto.getGtransNum());
                pstmt2.setInt(2, actionUserNum);
                pstmt2.setString(3, "U"); // U: Update
                pstmt2.setLong(4, oldAmt);
                pstmt2.setLong(5, newDto.getTransAmount());
                pstmt2.setString(6, oldCat);
                pstmt2.setString(7, newCat);
                pstmt2.executeUpdate();
            }

            conn.commit(); // A와 B가 모두 성공하면 DB에 완전 저장
            return true;
        } catch (Exception e) {
            if (conn != null) conn.rollback(); // 하나라도 실패하면 모두 원상복구
            throw e;
        } finally {
            if (conn != null) { conn.setAutoCommit(true); conn.close(); }
        }
    }

    // 지출 삭제 트랜잭션 (소프트 딜리트 + 로그 기록)
    public boolean deleteTransactionWithLog(int gtransNum, int actionUserNum, String oldCat, long oldAmt) throws Exception {
        Connection conn = null;
        try {
            conn = DBManager.getConnection();
            conn.setAutoCommit(false); // 트랜잭션 시작

            // A. 지출 내역 삭제 처리 (실제 삭제가 아닌 USE_YN = 'N' 처리)
            try (PreparedStatement pstmt1 = conn.prepareStatement(SqlManager.getSql("deleteGroupTransaction"))) {
                pstmt1.setInt(1, gtransNum);
                pstmt1.executeUpdate();
            }

            // B. 삭제 히스토리를 로그 테이블에 남기기 (ACTION_TYPE = 'D')
            try (PreparedStatement pstmt2 = conn.prepareStatement(SqlManager.getSql("insertExpenseLog"))) {
                pstmt2.setInt(1, gtransNum);
                pstmt2.setInt(2, actionUserNum);
                pstmt2.setString(3, "D"); // D: Delete
                pstmt2.setLong(4, oldAmt);
                pstmt2.setNull(5, java.sql.Types.NUMERIC); // 삭제되었으므로 변경 후 금액은 NULL
                pstmt2.setString(6, oldCat);
                pstmt2.setString(7, null);                 // 삭제되었으므로 변경 후 카테고리는 NULL
                pstmt2.executeUpdate();
            }

            conn.commit();
            return true;
        } catch (Exception e) {
            if (conn != null) conn.rollback();
            throw e;
        } finally {
            if (conn != null) { conn.setAutoCommit(true); conn.close(); }
        }
    }
    
    // ==========================================================
    // [과거 정산 보관함 3] 특정 회차의 지출 내역 가져오기
    // ==========================================================
    public List<GroupTransactionDTO> getTransactionsByPeriod(int periodNum) throws Exception {
        List<GroupTransactionDTO> list = new ArrayList<>();
        String sql = SqlManager.getSql("getTransactionsByPeriod"); 
        
        try (Connection conn = DBManager.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, periodNum);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    GroupTransactionDTO dto = new GroupTransactionDTO();
                    dto.setGtransNum(rs.getInt("GTRANS_NUM"));
                    dto.setTransAmount(rs.getLong("TRANS_AMOUNT"));
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