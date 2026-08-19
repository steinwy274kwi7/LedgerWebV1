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
}