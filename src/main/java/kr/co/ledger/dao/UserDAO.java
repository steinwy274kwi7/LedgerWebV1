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

    // 1. 회원가입
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

    // 2. 로그인 검증
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
}