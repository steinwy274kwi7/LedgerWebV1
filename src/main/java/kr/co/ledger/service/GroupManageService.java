package kr.co.ledger.service;

import java.util.ArrayList;
import java.util.List;

import kr.co.ledger.dao.GroupCategoryDAO;
import kr.co.ledger.dao.GroupDAO;
import kr.co.ledger.dao.GroupMemberDAO;
import kr.co.ledger.dao.InvitationDAO;
import kr.co.ledger.dao.UserDAO;
import kr.co.ledger.dto.GroupDTO;
import kr.co.ledger.dto.GroupMemberDTO;
import kr.co.ledger.dto.InvitationDTO;
import kr.co.ledger.dto.UserDTO;

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
	    // 1. 방 개수 10개 제한 체크 (기존 로직 유지)
	    int count = GroupDAO.getInstance().checkGroupCount(dto.getGroupOwnerNum());
	    if (count >= 10) {
	        throw new IllegalStateException("최대 가입 가능한 공동 가계부(10개)를 초과했습니다.");
	    }
	    
	    // 2. 그룹 생성 및 방장 가입 로직 실행
	    GroupDAO.getInstance().createGroup(dto);
	    
	    // 3. 새로 생성된 방 번호(dto.getGroupNum())를 꺼내서 '미분류' 카테고리 자동 생성!
	    GroupCategoryDAO.getInstance().insertDefaultCategory(dto.getGroupNum());
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
	
	// 그룹 삭제 (방 소프트 딜리트)
	public void deleteGroup(int groupNum, int ownerNum) throws Exception {
	    boolean isSuccess = kr.co.ledger.dao.GroupDAO.getInstance().deleteGroup(groupNum, ownerNum);
	    
	    if (!isSuccess) {
	        throw new IllegalAccessException("방장만 그룹을 삭제할 수 있거나, 이미 삭제된 방입니다.");
	    }
	}
	
	// 그룹 멤버 초대
	public void sendInvite(int groupNum, int inviterNum, String inviteeId) throws Exception {
	   
	    int currentMembers = GroupDAO.getInstance().getGroupMemberCount(groupNum);
	    if (currentMembers >= 50) {
	        throw new IllegalStateException("그룹 정원(50명)이 가득 차서 더 이상 초대할 수 없습니다.");
	    }
	    
	    UserDTO invitee = UserDAO.getInstance().getUserById(inviteeId);
	    if (invitee == null) {
	        throw new IllegalArgumentException("존재하지 않는 회원 아이디입니다.");
	    }
	    
	    int inviteeNum = invitee.getUserNum();
	    if (inviterNum == inviteeNum) {
	        throw new IllegalArgumentException("자기 자신은 초대할 수 없습니다.");
	    }
	    
	    boolean isMember = GroupDAO.getInstance().isUserAlreadyInGroupOrInvited(groupNum, inviteeNum, "checkAlreadyMember");
	    if (isMember) {
	        throw new IllegalStateException("이미 그룹에 참여 중인 멤버입니다.");
	    }
	    
	    boolean isInvited = GroupDAO.getInstance().isUserAlreadyInGroupOrInvited(groupNum, inviteeNum, "checkAlreadyInvited");
	    if (isInvited) {
	        throw new IllegalStateException("이미 초대장이 발송되어 대기 중인 멤버입니다.");
	    }
	    
	    GroupDAO.getInstance().insertInvitation(groupNum, inviterNum, inviteeNum);
	}
	
	// 멤버 목록 조회
	public List<GroupMemberDTO> getGroupMemberList(int groupNum) throws Exception {
	    return GroupDAO.getInstance().getGroupMemberList(groupNum);
	}

	// 강퇴 처리 (방장 전용)
	public void kickMember(int groupNum, int targetUserNum, int requestUserNum) throws Exception {
	    GroupDTO group = GroupDAO.getInstance().getGroupInfo(groupNum);
	    
	    if (group.getGroupOwnerNum() != requestUserNum) {
	        throw new IllegalAccessException("방장만 멤버를 강퇴할 수 있습니다.");
	    }
	    if (group.getGroupOwnerNum() == targetUserNum) {
	        throw new IllegalArgumentException("방장은 자신을 강퇴할 수 없습니다.");
	    }
	    
	    GroupDAO.getInstance().withdrawGroupMember(groupNum, targetUserNum);
	}

	// 자진 탈퇴 및 방장 자동 위임 로직
	public void leaveGroup(int groupNum, int requestUserNum) throws Exception {
	    GroupDAO dao = GroupDAO.getInstance();
	    GroupDTO group = dao.getGroupInfo(groupNum);
	    
	    // 1. 방장이 나가는 경우
	    if (group.getGroupOwnerNum() == requestUserNum) {
	        // 가장 오래된 멤버 번호 찾기
	        int oldestMemberNum = dao.getOldestMember(groupNum, requestUserNum);
	        
	        if (oldestMemberNum > 0) {
	            // [상황 A] 남은 멤버가 있다 -> 권한 위임 후 탈퇴
	            dao.updateGroupOwner(groupNum, oldestMemberNum);
	            dao.withdrawGroupMember(groupNum, requestUserNum);
	        } else {
	            // [상황 B] 남은 멤버가 0명이다 -> 방 삭제(비활성화) 후 탈퇴
	            dao.deleteGroup(groupNum, requestUserNum);
	            dao.withdrawGroupMember(groupNum, requestUserNum);
	        }
	    } 
	    // 2. 일반 멤버가 나가는 경우
	    else {
	        dao.withdrawGroupMember(groupNum, requestUserNum);
	    }
	}
	// 방장 수동 위임 처리
	public void transferOwner(int groupNum, int targetUserNum, int requestUserNum) throws Exception {
	    GroupDAO dao = GroupDAO.getInstance();
	    GroupDTO group = dao.getGroupInfo(groupNum);
	    
	    // 1. 요청한 사람이 방장인지 확인
	    if (group.getGroupOwnerNum() != requestUserNum) {
	        throw new IllegalAccessException("방장만 권한을 위임할 수 있습니다.");
	    }
	    // 2. 자기 자신에게 위임하려는 건 아닌지 확인
	    if (requestUserNum == targetUserNum) {
	        throw new IllegalArgumentException("본인에게 위임할 수 없습니다.");
	    }
	    // 3. 위임받을 대상이 현재 정상적인 멤버인지 확인
	    if (!dao.isUserAlreadyInGroupOrInvited(groupNum, targetUserNum, "checkAlreadyMember")) {
	        throw new IllegalArgumentException("현재 참여 중인 멤버에게만 위임할 수 있습니다.");
	    }
	    
	    // 모든 검증 통과 -> 방장 권한 위임 업데이트 (이전에 만든 updateGroupOwner 재활용)
	    dao.updateGroupOwner(groupNum, targetUserNum);
	}
	
	// 공개 그룹 검색 처리
	public List<GroupDTO> searchPublicGroups(String keyword) throws Exception {
	    if (keyword == null || keyword.trim().isEmpty()) {
	        return new ArrayList<>(); // 검색어가 없으면 빈 리스트 반환
	    }
	    return GroupDAO.getInstance().searchPublicGroups(keyword.trim());
	}
}
