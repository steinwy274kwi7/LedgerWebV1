package kr.co.ledger.util;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

public class DBManager {
    private static DataSource ds;
    
    static {
        try {
            Context initCtx = new InitialContext();
            Context envCtx = (Context) initCtx.lookup("java:comp/env");
            ds = (DataSource) envCtx.lookup("jdbc/oracle");
            System.out.println("DBManager - 오라클 커넥션 풀 로드 완벽 성공");
        } catch (Exception e) {
            System.out.println("DBManager - 커넥션 풀 로드 실패");
            e.printStackTrace();
        }
    }
    
    // 커넥션 풀에서 커넥션을 빌려오는 메서드
    public static Connection getConnection() throws Exception {
        return ds.getConnection();
    }
    
    // 사용이 끝난 자원(Connection, Statement, ResultSet)을 안전하게 반납하는 메서드
    public static void close(Connection conn, Statement stmt, ResultSet rs) {
        try {
            if (rs != null) rs.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            if (stmt != null) stmt.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            if (conn != null) conn.close(); // 커넥션 풀로 반납
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    // ResultSet이 없는 경우(Insert, Update, Delete)를 위한 오버로딩 메서드
    public static void close(Connection conn, Statement stmt) {
        close(conn, stmt, null);
    }
}