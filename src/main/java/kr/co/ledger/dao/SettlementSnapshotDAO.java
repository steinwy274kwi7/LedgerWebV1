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

    // 멤버별 지출액 합계 조회
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
                    dto.setSpentAmount(rs.getLong("SPENT_AMOUNT"));
                    list.add(dto);
                }
            }
        }
        return list;
    }

    // 🌟 정산 스냅샷 저장 + 장부 마감 + 새 장부 오픈 (하나라도 실패하면 자동 롤백)
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