package kr.co.ledger.util;

import java.sql.Connection;

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
	
	public static Connection getConnection() throws Exception {
		return ds.getConnection();
	}
}
