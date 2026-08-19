package kr.co.ledger.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import kr.co.ledger.dto.UserDTO;
import kr.co.ledger.util.DBManager;
import kr.co.ledger.util.SqlManager;

public class UserDAO {
    
    private static UserDAO instance = new UserDAO();
    private UserDAO() {}
    public static UserDAO getInstance() { return instance; }

    // 회원가입
    public boolean insertUser(UserDTO dto) {
        String sql = SqlManager.getSql("insertUser");
        
        try (Connection conn = DBManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
             
            pstmt.setString(1, dto.getUserId());
            pstmt.setString(2, dto.getUserPw()); 
            pstmt.setString(3, dto.getUserNickname());
            pstmt.setString(4, dto.getUserEmail());
            pstmt.setString(5, dto.getUserPhone());
            pstmt.setString(6, dto.getUserBirth());
            
            return pstmt.executeUpdate() > 0;
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // 로그인
    public UserDTO getUserById(String id) {
        String sql = SqlManager.getSql("getUserById");
        UserDTO user = null;
        
        try (Connection conn = DBManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
             
            pstmt.setString(1, id); 
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    user = new UserDTO();
                    user.setUserNum(rs.getInt("USER_NUM"));
                    user.setUserId(rs.getString("USER_ID"));
                    user.setUserPw(rs.getString("USER_PW"));
                    user.setUserNickname(rs.getString("USER_NICKNAME"));
                    user.setUserStatus(rs.getString("USER_STATUS"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return user;
    }
    
    // 아이디 찾기
    public String findUserId(String userEmail, String userPhone, String userBirth) throws Exception {
        String sql = SqlManager.getSql("findUserId");
        String foundId = null;

        try (Connection conn = DBManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, userEmail);
            pstmt.setString(2, userPhone);
            pstmt.setString(3, userBirth);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    foundId = rs.getString("user_id");
                }
            }
        }
        return foundId;
    }
    
    // 임시 비밀번호 발급
    public int updateTempPw(String tempPw, String userId, String userEmail, String userPhone, String userBirth) throws Exception {
        String sql = SqlManager.getSql("updateTempPw");
        
        try (Connection conn = DBManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, tempPw);
            pstmt.setString(2, userId);
            pstmt.setString(3, userEmail);
            pstmt.setString(4, userPhone);
            pstmt.setString(5, userBirth);
            
            return pstmt.executeUpdate();
        }
    }
    
    // 마이페이지
    public UserDTO getUserInfo(String userId) throws Exception {
        String sql = SqlManager.getSql("getUserInfo");
        UserDTO user = null;
        
        try (Connection conn = DBManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, userId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    user = new UserDTO();
                    user.setUserId(rs.getString("USER_ID"));
                    user.setUserNickname(rs.getString("USER_NICKNAME"));
                    user.setUserEmail(rs.getString("USER_EMAIL"));
                    user.setUserPhone(rs.getString("USER_PHONE"));
                    user.setUserBirth(rs.getString("USER_BIRTH"));
                }
            }
        }
        return user;
    }
    
    // 개인정보 수정
    public int updateUserInfo(UserDTO user) throws Exception {
        String sql = SqlManager.getSql("updateUserInfo");
        
        try (Connection conn = DBManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, user.getUserPw());
            pstmt.setString(2, user.getUserNickname());
            pstmt.setString(3, user.getUserEmail());
            pstmt.setString(4, user.getUserPhone());
            pstmt.setString(5, user.getUserBirth());
            pstmt.setString(6, user.getUserId());
            
            return pstmt.executeUpdate();
        }
    }
    
    // 회원탈퇴
    public int withdrawUser(String delId, String delPw, String delNick, String delEmail, String delPhone, String delBirth, String originalId) throws Exception {
        String sql = SqlManager.getSql("withdrawUser");
        
        try (Connection conn = DBManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, delId);
            pstmt.setString(2, delPw);
            pstmt.setString(3, delNick);
            pstmt.setString(4, delEmail);
            pstmt.setString(5, delPhone);
            pstmt.setString(6, delBirth);
            pstmt.setString(7, originalId);
            
            return pstmt.executeUpdate();
        }
    }
    
    // 휴면계정처리
    public int updateDormantUsers() throws Exception {
        String sql = SqlManager.getSql("updateDormantUsers");
        
        try (Connection conn = DBManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            return pstmt.executeUpdate();
        }
    }
    
    // 휴면해제처리
    public int wakeupUser(String userId) throws Exception {
        String sql = SqlManager.getSql("wakeupUser");
        
        try (Connection conn = DBManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, userId);
            return pstmt.executeUpdate();
        }
    }
    
    // 개인 가계부 공개 비공개 설정
    public void updateBookOpenYn(int userNum, String bookOpenYn) throws Exception {
        String sql = SqlManager.getSql("updateBookOpenYn"); 
        try (Connection conn = DBManager.getConnection(); 
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, bookOpenYn);
            pstmt.setInt(2, userNum);
            pstmt.executeUpdate();
        }
    }
    
}