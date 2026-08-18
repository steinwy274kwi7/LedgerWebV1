package kr.co.ledger.service;

import java.util.List;
import kr.co.ledger.dto.ChartDTO;
import kr.co.ledger.dao.GroupTransactionDAO;

public class GroupLedgerService {

    private static GroupLedgerService instance = new GroupLedgerService();
    private GroupLedgerService() {}
    public static GroupLedgerService getInstance() { return instance; }

    // 그룹 카테고리 합계
    public List<ChartDTO> getAllMyGroupCategorySumForChart(int userNum, String targetMonth) throws Exception {
        return GroupTransactionDAO.getInstance().getAllMyGroupCategorySumForChart(userNum, targetMonth);
    }
    
}