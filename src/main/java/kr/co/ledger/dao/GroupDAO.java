package kr.co.ledger.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import kr.co.ledger.dto.GroupDTO;
import kr.co.ledger.util.DBManager;
import kr.co.ledger.util.SqlManager;

public class GroupDAO {
	
    private static GroupDAO instance = new GroupDAO();
    private GroupDAO() {}
    public static GroupDAO getInstance() { return instance; }

    // 내가 속한 그룹 목록 조회
    public List<GroupDTO> getMyGroupList(int userNum) throws Exception {

        String sql = SqlManager.getSql("getMyGroupList");
        List<GroupDTO> list = new ArrayList<>();
        
        try (Connection conn = DBManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userNum);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    GroupDTO dto = new GroupDTO();
                    dto.setGroupNum(rs.getInt("GROUP_NUM"));
                    dto.setGroupName(rs.getString("GROUP_NAME"));
                    dto.setGroupDesc(rs.getString("GROUP_DESC"));
                    dto.setGroupType(rs.getString("GROUP_TYPE"));
                    dto.setGroupOwnerNum(rs.getInt("GROUP_OWNER_NUM"));
                    dto.setCreatedAt(rs.getString("CREATED_AT"));
                    dto.setMemberCount(rs.getInt("MEMBER_COUNT"));
                    
                    list.add(dto);
                }
            }
        }
        return list;
    }
    
    // 방 개수 체크
    public int checkGroupCount(int userNum) throws Exception {
        String sql = SqlManager.getSql("checkGroupCount");
        try (Connection conn = DBManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userNum);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        }
        return 0;
    }

    // 그룹 생성 및 방장 가입 트랜잭션
    public void createGroup(GroupDTO dto) throws Exception {
        String sqlSeq = SqlManager.getSql("getGroupSeq");
        String sqlGroup = SqlManager.getSql("insertGroup");
        String sqlMember = SqlManager.getSql("insertGroupMemberOwner");

        Connection conn = null;
        try {
            conn = DBManager.getConnection();
            conn.setAutoCommit(false);

            int newGroupNum = 0;
            
            try (PreparedStatement pstmtSeq = conn.prepareStatement(sqlSeq);
                 ResultSet rs = pstmtSeq.executeQuery()) {
                if (rs.next()) newGroupNum = rs.getInt(1);
            }
            
            try (PreparedStatement pstmtGroup = conn.prepareStatement(sqlGroup)) {
                pstmtGroup.setInt(1, newGroupNum);
                pstmtGroup.setString(2, dto.getGroupName());
                pstmtGroup.setString(3, dto.getGroupDesc());
                pstmtGroup.setString(4, dto.getGroupType());
                pstmtGroup.setInt(5, dto.getGroupOwnerNum());
                pstmtGroup.setString(6, dto.getGroupOpenYn() != null ? dto.getGroupOpenYn() : "N");
                pstmtGroup.executeUpdate();
            }

            try (PreparedStatement pstmtMember = conn.prepareStatement(sqlMember)) {
                pstmtMember.setInt(1, newGroupNum);
                pstmtMember.setInt(2, dto.getGroupOwnerNum());
                pstmtMember.executeUpdate();
            }

            conn.commit();
            
        } catch (Exception e) {
            if (conn != null) conn.rollback();
            throw e;
        } finally {
            if (conn != null) {
                conn.setAutoCommit(true);
                conn.close();
            }
        }
    }
    
}