package kr.co.ledger.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import kr.co.ledger.dto.PersonalCategoryDTO;
import kr.co.ledger.util.DBManager;
import kr.co.ledger.util.SqlManager;

public class PersonalCategoryDAO {

    private static PersonalCategoryDAO instance = new PersonalCategoryDAO();
    private PersonalCategoryDAO() {}
    public static PersonalCategoryDAO getInstance() { return instance; }

    // 카테고리 목록 조회
    public List<PersonalCategoryDTO> getCategoryList(int userNum, String type) throws Exception {
        String sql = SqlManager.getSql("getPersonalCategoryList");
        List<PersonalCategoryDTO> list = new ArrayList<>();
        try (Connection conn = DBManager.getConnection(); 
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userNum);
            pstmt.setString(2, type);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    PersonalCategoryDTO dto = new PersonalCategoryDTO();
                    dto.setCategoryNum(rs.getInt("CATEGORY_NUM"));
                    dto.setCategoryName(rs.getString("CATEGORY_NAME"));
                    dto.setCategoryType(rs.getString("CATEGORY_TYPE"));
                    list.add(dto);
                }
            }
        }
        return list;
    }

    // 카테고리 등록
    public void insertCategory(PersonalCategoryDTO dto) throws Exception {
        String sql = SqlManager.getSql("insertPersonalCategory");
        try (Connection conn = DBManager.getConnection(); 
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, dto.getUserNum());
            pstmt.setString(2, dto.getCategoryName());
            pstmt.setString(3, dto.getCategoryType());
            pstmt.executeUpdate();
        }
    }

    // 카테고리 수정
    public void updateCategory(PersonalCategoryDTO dto) throws Exception {
        String sql = SqlManager.getSql("updatePersonalCategory");
        try (Connection conn = DBManager.getConnection(); 
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, dto.getCategoryName());
            pstmt.setInt(2, dto.getCategoryNum());
            pstmt.setInt(3, dto.getUserNum());
            pstmt.executeUpdate();
        }
    }

    // 카테고리 삭제 - 논리적 삭제 USE_YN = 'N'
    public void deleteCategory(int categoryNum, int userNum) throws Exception {
        String sql = SqlManager.getSql("deletePersonalCategory");
        try (Connection conn = DBManager.getConnection(); 
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, categoryNum);
            pstmt.setInt(2, userNum);
            pstmt.executeUpdate();
        }
    }

    // 카테고리 삭제 - '미분류' 등 특정 이름의 카테고리 번호 찾기 (삭제 시 이관용)
    public Integer getCategoryByName(int userNum, String name, String type) throws Exception {
        String sql = SqlManager.getSql("getCategoryByName");
        try (Connection conn = DBManager.getConnection(); 
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userNum);
            pstmt.setString(2, name);
            pstmt.setString(3, type);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) return rs.getInt("CATEGORY_NUM");
            }
        }
        return null; 
    }

    // 카테고리 삭제 - 삭제된 카테고리의 내역들을 미분류로 싹 옮기기 (이관용)
    public void moveTransactionsCategory(int userNum, int oldCatNum, int newCatNum) throws Exception {
        String sql = SqlManager.getSql("moveTransactionsCategory");
        try (Connection conn = DBManager.getConnection(); 
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, newCatNum);
            pstmt.setInt(2, oldCatNum);
            pstmt.setInt(3, userNum);
            pstmt.executeUpdate();
        }
    }
}