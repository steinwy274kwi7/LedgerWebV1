package kr.co.ledger.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import kr.co.ledger.util.DBManager;
import kr.co.ledger.util.SqlManager;

public class GroupMemberDAO {
	
    private static GroupMemberDAO instance = new GroupMemberDAO();
    private GroupMemberDAO() {}
    public static GroupMemberDAO getInstance() { return instance; }

    // 수락 시 그룹 멤버 등록
    public boolean insertMember(int groupNum, int userNum) throws Exception {
        String sql = SqlManager.getSql("insertGroupMember");
        try (Connection conn = DBManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, groupNum);
            pstmt.setInt(2, userNum);
            return pstmt.executeUpdate() > 0;
        }
    }
}