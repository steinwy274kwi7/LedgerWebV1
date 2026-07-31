package kr.co.ledger.util;

import java.io.InputStream;
import java.util.Properties;

public class SqlManager {
    
    private static Properties sqlProps = new Properties();

    static {
        String[] xmlPaths = {
            "/kr/co/ledger/sql/user_sql.xml",
            "/kr/co/ledger/sql/personal_category_sql.xml",
            "/kr/co/ledger/sql/personal_transaction_sql.xml",
            "/kr/co/ledger/sql/group_sql.xml",
            "/kr/co/ledger/sql/group_member_sql.xml",
            "/kr/co/ledger/sql/invitation_sql.xml",
            "/kr/co/ledger/sql/ledger_period_sql.xml",
            "/kr/co/ledger/sql/group_category_sql.xml",
            "/kr/co/ledger/sql/group_transaction_sql.xml",
            "/kr/co/ledger/sql/expense_log_sql.xml",
            "/kr/co/ledger/sql/settlement_snapshot_sql.xml"
        };
        
        for (String path : xmlPaths) {
            try (InputStream is = SqlManager.class.getResourceAsStream(path)) {
                if (is != null) sqlProps.loadFromXML(is);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static String getSql(String key) {
        return sqlProps.getProperty(key);
    }
}