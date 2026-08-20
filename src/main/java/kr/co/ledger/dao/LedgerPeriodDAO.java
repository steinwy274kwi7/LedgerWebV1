package kr.co.ledger.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import kr.co.ledger.util.DBManager;
import kr.co.ledger.util.SqlManager;

public class LedgerPeriodDAO {
    private static LedgerPeriodDAO instance = new LedgerPeriodDAO();
    private LedgerPeriodDAO() {}
    public static LedgerPeriodDAO getInstance() { return instance; }

    // 현재 열려있는 장부 회차 번호와 시퀀스 가져오기
    public int[] getCurrentPeriodInfo(int groupNum) throws Exception {
        String sql = SqlManager.getSql("getCurrentPeriod");
        try (Connection conn = DBManager.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, groupNum);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) return new int[]{rs.getInt("PERIOD_NUM"), rs.getInt("PERIOD_SEQ")};
            }
        }
        throw new Exception("현재 진행 중인 장부 회차를 찾을 수 없습니다.");
    }
}