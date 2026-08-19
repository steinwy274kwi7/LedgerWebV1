package kr.co.ledger.service;

import java.util.List;

import kr.co.ledger.dao.GroupDAO;
import kr.co.ledger.dao.GroupMemberDAO;
import kr.co.ledger.dao.InvitationDAO;
import kr.co.ledger.dto.GroupDTO;
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
	
	// 내가 속한 그룹 목록 조회
	public List<GroupDTO> getMyGroupList(int userNum) throws Exception {
	    return GroupDAO.getInstance().getMyGroupList(userNum);
	}
	
	// 그룹 생성
	public void createGroup(GroupDTO dto) throws Exception {
	    int count = GroupDAO.getInstance().checkGroupCount(dto.getGroupOwnerNum());
	    if (count >= 10) {
	        throw new IllegalStateException("최대 가입 가능한 공동 가계부(10개)를 초과했습니다.");
	    }
	    
	    GroupDAO.getInstance().createGroup(dto);
	}
	
	// 그룹 설정 업데이트
	public void updateGroupSettings(GroupDTO dto) throws Exception {
	    boolean isSuccess = kr.co.ledger.dao.GroupDAO.getInstance().updateGroupSettings(dto);
	    
	    if (!isSuccess) {
	        throw new IllegalAccessException("설정을 변경할 권한이 없거나 존재하지 않는 방입니다.");
	    }
	}
	
	// 특정 그룹정보 하나만 가져오기
	public GroupDTO getGroupInfo(int groupNum) throws Exception {
	    return GroupDAO.getInstance().getGroupInfo(groupNum);
	}
	
}
