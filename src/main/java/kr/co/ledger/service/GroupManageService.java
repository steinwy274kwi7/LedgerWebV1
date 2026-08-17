package kr.co.ledger.service;

import java.util.List;

import kr.co.ledger.dao.GroupMemberDAO;
import kr.co.ledger.dao.InvitationDAO;
import kr.co.ledger.dto.InvitationDTO;

public class GroupManageService {

	private static GroupManageService instance = new GroupManageService();
    private GroupManageService() {}
    public static GroupManageService getInstance() { return instance; }
    
    // 초대알림 목록 보기
	public List<InvitationDTO> getPendingInvitations(int userNum) throws Exception {
	    return InvitationDAO.getInstance().getPendingInvitations(userNum);
	}
	
	// 초대 수락, 거절
	public boolean respondToInvitation(int inviteNum, String status, int userNum) throws Exception {
	    
	    InvitationDTO invite = InvitationDAO.getInstance().getInvitationDetail(inviteNum);
	    if (invite == null || invite.getInviteeNum() != userNum) {
	        return false;
	    }

	    boolean isUpdated = InvitationDAO.getInstance().updateInviteStatus(inviteNum, status);

	    if (isUpdated && "A".equals(status)) {
	        boolean isInserted = GroupMemberDAO.getInstance().insertMember(invite.getGroupNum(), userNum);
	        if (!isInserted) {
	            return false;
	        }
	    }
	    
	    return isUpdated;
	}
	
}
