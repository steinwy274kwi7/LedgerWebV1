package kr.co.ledger.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import kr.co.ledger.dto.InvitationDTO;
import kr.co.ledger.util.DBManager;
import kr.co.ledger.util.SqlManager;

public class InvitationDAO {

	private static InvitationDAO instance = new InvitationDAO();
    private InvitationDAO() {}
    public static InvitationDAO getInstance() { return instance; }
    
    // 초대알림 목록 보기
	public List<InvitationDTO> getPendingInvitations(int userNum) throws Exception {
	    String sql = SqlManager.getSql("getPendingInvitations");
	    List<InvitationDTO> list = new ArrayList<>();
	    
	    try (Connection conn = DBManager.getConnection();
	         PreparedStatement pstmt = conn.prepareStatement(sql)) {
	         
	        pstmt.setInt(1, userNum); 
	        
	        try (ResultSet rs = pstmt.executeQuery()) {
	            while (rs.next()) {
	                InvitationDTO dto = new InvitationDTO();
	                dto.setInviteNum(rs.getInt("INVITE_NUM"));
	                dto.setGroupName(rs.getString("GROUP_NAME"));
	                dto.setInviterName(rs.getString("INVITER_NAME"));
	                dto.setCreatedAt(rs.getString("CREATED_AT"));
	                list.add(dto);
	            }
	        }
	    }
	    return list;
	}
	
	// 초대장 status 변경
	public boolean updateInviteStatus(int inviteNum, String status) throws Exception {
	    String sql = SqlManager.getSql("updateInviteStatus");
	    try (Connection conn = DBManager.getConnection();
	         PreparedStatement pstmt = conn.prepareStatement(sql)) {
	        pstmt.setString(1, status);
	        pstmt.setInt(2, inviteNum);
	        return pstmt.executeUpdate() > 0;
	    }
	}

	// 수락할 방 번호 확인
	public InvitationDTO getInvitationDetail(int inviteNum) throws Exception {
	    String sql = SqlManager.getSql("getInvitationDetail");
	    InvitationDTO dto = null;
	    try (Connection conn = DBManager.getConnection();
	         PreparedStatement pstmt = conn.prepareStatement(sql)) {
	        pstmt.setInt(1, inviteNum);
	        try (ResultSet rs = pstmt.executeQuery()) {
	            if (rs.next()) {
	                dto = new InvitationDTO();
	                dto.setGroupNum(rs.getInt("GROUP_NUM"));
	                dto.setInviteeNum(rs.getInt("INVITEE_NUM"));
	            }
	        }
	    }
	    return dto;
	}
	
}
