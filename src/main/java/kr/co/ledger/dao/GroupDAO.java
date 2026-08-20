package kr.co.ledger.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import kr.co.ledger.dto.GroupDTO;
import kr.co.ledger.dto.GroupMemberDTO;
import kr.co.ledger.dto.LedgerPeriodDTO;
import kr.co.ledger.dto.SettlementSnapshotDTO;
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
            dto.setGroupNum(newGroupNum);
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
    
    // 그룹 설정 업데이트
    public boolean updateGroupSettings(GroupDTO dto) throws Exception {
        String sql = SqlManager.getSql("updateGroupSettings");
        try (Connection conn = DBManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
             
            pstmt.setString(1, dto.getGroupName());
            pstmt.setString(2, dto.getGroupDesc());
            pstmt.setString(3, dto.getGroupOpenYn());
            pstmt.setInt(4, dto.getGroupNum());
            pstmt.setInt(5, dto.getGroupOwnerNum());
            
            return pstmt.executeUpdate() > 0;
        }
    }
    
    // 특정 그룹정보 하나만 가져오기
    public GroupDTO getGroupInfo(int groupNum) throws Exception {
        String sql = SqlManager.getSql("getGroupInfo");
        GroupDTO dto = null;
        
        try (Connection conn = DBManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, groupNum);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    dto = new GroupDTO();
                    dto.setGroupNum(rs.getInt("GROUP_NUM"));
                    dto.setGroupName(rs.getString("GROUP_NAME"));
                    dto.setGroupDesc(rs.getString("GROUP_DESC"));
                    dto.setGroupType(rs.getString("GROUP_TYPE"));
                    dto.setGroupOwnerNum(rs.getInt("GROUP_OWNER_NUM"));
                    dto.setGroupOpenYn(rs.getString("GROUP_OPEN_YN"));
                    dto.setCreatedAt(rs.getString("CREATED_AT"));
                }
            }
        }
        return dto;
    }
    
    // 그룹 삭제 (소프트 딜리트)
    public boolean deleteGroup(int groupNum, int ownerNum) throws Exception {
        String sql = SqlManager.getSql("deleteGroup");
        try (Connection conn = DBManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
             
            pstmt.setInt(1, groupNum);
            pstmt.setInt(2, ownerNum);
            
            return pstmt.executeUpdate() > 0;
        }
    }
    
    // 현재 멤버 수 조회
    public int getGroupMemberCount(int groupNum) throws Exception {
        String sql = SqlManager.getSql("getGroupMemberCount");
        try (Connection conn = DBManager.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, groupNum);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        }
        return 0;
    }

    // 중복 가입 및 초대 확인
    public boolean isUserAlreadyInGroupOrInvited(int groupNum, int userNum, String queryKey) throws Exception {
        String sql = SqlManager.getSql(queryKey);
        try (Connection conn = DBManager.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, groupNum);
            pstmt.setInt(2, userNum);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) return rs.getInt(1) > 0;
            }
        }
        return false;
    }

    // 초대장 DB 저장
    public void insertInvitation(int groupNum, int inviterNum, int inviteeNum) throws Exception {
        String sql = SqlManager.getSql("insertInvitation");
        try (Connection conn = DBManager.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, groupNum);
            pstmt.setInt(2, inviterNum);
            pstmt.setInt(3, inviteeNum);
            pstmt.executeUpdate();
        }
    }
    
    // 멤버 목록 가져오기
    public List<GroupMemberDTO> getGroupMemberList(int groupNum) throws Exception {
        String sql = SqlManager.getSql("getGroupMemberList");
        List<GroupMemberDTO> list = new ArrayList<>();
        
        try (Connection conn = DBManager.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, groupNum);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    GroupMemberDTO dto = new GroupMemberDTO();
                    dto.setMemberNum(rs.getInt("MEMBER_NUM"));
                    dto.setGroupNum(rs.getInt("GROUP_NUM"));
                    dto.setUserNum(rs.getInt("USER_NUM"));
                    dto.setJoinDate(rs.getString("JOIN_DATE"));
                    dto.setMemberStatus(rs.getString("MEMBER_STATUS"));
                    
                    dto.setUserId(rs.getString("USER_ID"));
                    dto.setUserNickname(rs.getString("USER_NICKNAME"));
                    list.add(dto);
                }
            }
        }
        return list;
    }

    // 멤버 강퇴 탈퇴 처리
    public boolean withdrawGroupMember(int groupNum, int userNum) throws Exception {
        String sql = SqlManager.getSql("withdrawGroupMember");
        try (Connection conn = DBManager.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, groupNum);
            pstmt.setInt(2, userNum);
            return pstmt.executeUpdate() > 0;
        }
    }
    
    // 위임받을 가장 오래된 멤버 번호 조회 (없으면 0 반환)
    public int getOldestMember(int groupNum, int currentOwnerNum) throws Exception {
        String sql = SqlManager.getSql("getOldestMember");
        try (Connection conn = DBManager.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, groupNum);
            pstmt.setInt(2, currentOwnerNum);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("USER_NUM");
                }
            }
        }
        return 0;
    }

    // 그룹 방장 변경 (권한 위임)
    public void updateGroupOwner(int groupNum, int newOwnerNum) throws Exception {
        String sql = SqlManager.getSql("updateGroupOwner");
        try (Connection conn = DBManager.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, newOwnerNum);
            pstmt.setInt(2, groupNum);
            pstmt.executeUpdate();
        }
    }
    
    // 공개 그룹 검색
    public List<GroupDTO> searchPublicGroups(String keyword) throws Exception {
        List<GroupDTO> list = new ArrayList<>();
        String sql = SqlManager.getSql("searchPublicGroups");
        try (Connection conn = DBManager.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, keyword);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    GroupDTO group = new GroupDTO();
                    group.setGroupNum(rs.getInt("GROUP_NUM"));
                    group.setGroupName(rs.getString("GROUP_NAME"));
                    group.setGroupDesc(rs.getString("GROUP_DESC"));
                    list.add(group);
                }
            }
        }
        return list;
    }
    
    // ==========================================================
    // [과거 정산 보관함 1] 마감된 회차 목록 가져오기
    // ==========================================================
    public List<LedgerPeriodDTO> getClosedPeriods(int groupNum) throws Exception {
        List<LedgerPeriodDTO> list = new ArrayList<>();
        String sql = SqlManager.getSql("getClosedPeriods"); 
        
        try (Connection conn = DBManager.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, groupNum);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    LedgerPeriodDTO dto = new LedgerPeriodDTO();
                    dto.setPeriodNum(rs.getInt("PERIOD_NUM"));
                    dto.setPeriodSeq(rs.getInt("PERIOD_SEQ"));
                    dto.setPeriodStartDate(rs.getString("START_DATE"));
                    dto.setPeriodEndDate(rs.getString("END_DATE"));
                    list.add(dto);
                }
            }
        }
        return list;
    }

    // ==========================================================
    // [과거 정산 보관함 2] 특정 회차의 정산 결과 스냅샷 가져오기
    // ==========================================================
    public List<SettlementSnapshotDTO> getSnapshots(int periodNum) throws Exception {
        List<SettlementSnapshotDTO> list = new ArrayList<>();
        String sql = SqlManager.getSql("getSettlementSnapshots"); 
        
        try (Connection conn = DBManager.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, periodNum);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    SettlementSnapshotDTO dto = new SettlementSnapshotDTO();
                    dto.setPayerNickname(rs.getString("PAYER_NICKNAME"));
                    dto.setReceiverNickname(rs.getString("RECEIVER_NICKNAME"));
                    dto.setSettleAmount(rs.getLong("SETTLE_AMOUNT"));
                    list.add(dto);
                }
            }
        }
        return list;
    }
    
}