package kr.co.ledger.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import kr.co.ledger.dto.GroupCategoryDTO;
import kr.co.ledger.util.DBManager;
import kr.co.ledger.util.SqlManager;

public class GroupCategoryDAO {
	
	private static GroupCategoryDAO instance = new GroupCategoryDAO();
	private GroupCategoryDAO() {}
	public static GroupCategoryDAO getInstance() { return instance; }
	
	// 그룹 카테고리 목록 가져오기
	public List<GroupCategoryDTO> getCategoryList(int groupNum) throws Exception {
	    List<GroupCategoryDTO> list = new ArrayList<>();
	    String sql = SqlManager.getSql("getGroupCategories");
	    
	    try (Connection conn = DBManager.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
	        pstmt.setInt(1, groupNum);
	        try (ResultSet rs = pstmt.executeQuery()) {
	            while (rs.next()) {
	                GroupCategoryDTO dto = new GroupCategoryDTO();
	                dto.setGcategoryNum(rs.getInt("GCATEGORY_NUM"));
	                dto.setCategoryName(rs.getString("CATEGORY_NAME"));
	                list.add(dto);
	            }
	        }
	    }
	    return list;
	}
	
	// 카테고리 등록
	public boolean insertCategory(int groupNum, String categoryName) throws Exception {
	    String sql = SqlManager.getSql("insertCategory");
	    try (Connection conn = DBManager.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
	        pstmt.setInt(1, groupNum);
	        pstmt.setString(2, categoryName);
	        return pstmt.executeUpdate() > 0;
	    }
	}

	// 카테고리 수정
	public boolean updateCategory(int groupNum, int categoryNum, String categoryName) throws Exception {
	    String sql = SqlManager.getSql("updateCategory");
	    try (Connection conn = DBManager.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
	        pstmt.setString(1, categoryName);
	        pstmt.setInt(2, categoryNum);
	        pstmt.setInt(3, groupNum);
	        return pstmt.executeUpdate() > 0;
	    }
	}

	// 카테고리 삭제 및 이관 (트랜잭션)
	public boolean deleteCategoryWithTransfer(int groupNum, int targetCategoryNum, String targetCategoryName, int actionUserNum) throws Exception {
	    Connection conn = null;
	    try {
	        conn = DBManager.getConnection();
	        conn.setAutoCommit(false); // 트랜잭션 시작
	        
	        // 1. '미분류' 카테고리 번호 조회
	        int uncatNum = -1;
	        try (PreparedStatement pstmt1 = conn.prepareStatement(SqlManager.getSql("getUncategorizedNum"))) {
	            pstmt1.setInt(1, groupNum);
	            try (ResultSet rs = pstmt1.executeQuery()) {
	                if (rs.next()) uncatNum = rs.getInt("GCATEGORY_NUM");
	            }
	        }
	        if (uncatNum == -1) throw new Exception("미분류 카테고리를 찾을 수 없습니다.");

	        // 2. 이관 대상 트랜잭션 로그 기록
	        try (PreparedStatement pstmt2 = conn.prepareStatement(SqlManager.getSql("insertTransferLogs"))) {
	            pstmt2.setInt(1, actionUserNum);
	            pstmt2.setString(2, targetCategoryName);
	            pstmt2.setInt(3, targetCategoryNum);
	            pstmt2.executeUpdate();
	        }

	        // 3. 지출 내역 미분류로 업데이트
	        try (PreparedStatement pstmt3 = conn.prepareStatement(SqlManager.getSql("transferTransactions"))) {
	            pstmt3.setInt(1, uncatNum);
	            pstmt3.setInt(2, targetCategoryNum);
	            pstmt3.executeUpdate();
	        }

	        // 4. 카테고리 사용 중지(삭제) 처리
	        try (PreparedStatement pstmt4 = conn.prepareStatement(SqlManager.getSql("deleteCategory"))) {
	            pstmt4.setInt(1, targetCategoryNum);
	            pstmt4.setInt(2, groupNum);
	            int result = pstmt4.executeUpdate();
	            if (result == 0) throw new Exception("기본 카테고리는 삭제할 수 없거나 이미 삭제되었습니다.");
	        }

	        conn.commit();
	        return true;
	    } catch (Exception e) {
	        if (conn != null) conn.rollback();
	        throw e;
	    } finally {
	        if (conn != null) conn.setAutoCommit(true);
	        if (conn != null) conn.close();
	    }
	}
	
	// 신규 방 생성용 기본 카테고리 자동 생성
	public void insertDefaultCategory(int groupNum) throws Exception {
	    String sql = SqlManager.getSql("insertDefaultGroupCategory");
	    try (Connection conn = DBManager.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
	        pstmt.setInt(1, groupNum);
	        pstmt.executeUpdate();
	    }
	}
	
}
