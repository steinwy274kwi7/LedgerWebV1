package kr.co.ledger.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import kr.co.ledger.dto.MemberExpenseDTO;
import kr.co.ledger.dto.SettlementSnapshotDTO;
import kr.co.ledger.util.DBManager;
import kr.co.ledger.util.SqlManager;

public class SettlementSnapshotDAO {
    private static SettlementSnapshotDAO instance = new SettlementSnapshotDAO();
    private SettlementSnapshotDAO() {}
    public static SettlementSnapshotDAO getInstance() { return instance; }

    // 🌟 추가: 현재 진행 중인 회차 번호 조회
    public int getOpenPeriodNum(int groupNum) throws Exception {
        int periodNum = 0;
        String sql = SqlManager.getSql("getOpenPeriodNum");
        try (Connection conn = DBManager.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, groupNum);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) periodNum = rs.getInt("PERIOD_NUM");
            }
        }
        return periodNum;
    }

    // 기존 멤버별 지출액 합계 조회 (닉네임 세팅 추가)
    public List<MemberExpenseDTO> getMemberTotalExpenses(int groupNum, int periodNum) throws Exception {
        List<MemberExpenseDTO> list = new ArrayList<>();
        String sql = SqlManager.getSql("getMemberTotalExpenses");
        try (Connection conn = DBManager.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, periodNum);
            pstmt.setInt(2, groupNum);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    MemberExpenseDTO dto = new MemberExpenseDTO();
                    dto.setUserNum(rs.getInt("USER_NUM"));
                    dto.setNickname(rs.getString("USER_NICKNAME")); 
                    dto.setSpentAmount(rs.getLong("SPENT_AMOUNT"));
                    list.add(dto);
                }
            }
        }
        return list;
    }

 // 🌟 이 메서드를 SettlementSnapshotDAO 파일 맨 아래(클래스 닫기 전)에 다시 붙여넣어 주세요!
    public boolean executeSettlementTransaction(int groupNum, int oldPeriodNum, int newPeriodSeq, List<SettlementSnapshotDTO> snapshots) throws Exception {
        Connection conn = null;
        try {
            conn = DBManager.getConnection();
            conn.setAutoCommit(false); 

            // 1. 정산 스냅샷 벌크 INSERT
            if (!snapshots.isEmpty()) {
                String sqlSnap = SqlManager.getSql("insertSettlementSnapshot");
                try (PreparedStatement pstmtSnap = conn.prepareStatement(sqlSnap)) {
                    for (SettlementSnapshotDTO snap : snapshots) {
                        pstmtSnap.setInt(1, snap.getPeriodNum());
                        pstmtSnap.setInt(2, snap.getPayerUserNum());
                        pstmtSnap.setInt(3, snap.getReceiverUserNum());
                        pstmtSnap.setLong(4, snap.getSettleAmount());
                        pstmtSnap.addBatch();
                    }
                    pstmtSnap.executeBatch();
                }
            }

            // 2. 기존 장부 마감 (UPDATE)
            String sqlClose = SqlManager.getSql("closeCurrentPeriod");
            try (PreparedStatement pstmtClose = conn.prepareStatement(sqlClose)) {
                pstmtClose.setInt(1, oldPeriodNum);
                pstmtClose.executeUpdate();
            }

            // 3. 새 장부 회차 오픈 (INSERT)
            String sqlOpen = SqlManager.getSql("openNewPeriod");
            try (PreparedStatement pstmtOpen = conn.prepareStatement(sqlOpen)) {
                pstmtOpen.setInt(1, groupNum);
                pstmtOpen.setInt(2, newPeriodSeq);
                pstmtOpen.executeUpdate();
            }

            conn.commit(); 
            return true;
        } catch (Exception e) {
            if (conn != null) conn.rollback(); 
            throw e;
        } finally {
            if (conn != null) conn.setAutoCommit(true);
            DBManager.close(conn, null, null);
        }
    }
}