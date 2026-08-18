package kr.co.ledger.service;

import java.util.List;
import kr.co.ledger.dao.PersonalTransactionDAO;
import kr.co.ledger.dto.ChartDTO;

public class PersonalLedgerService {
	
    private static PersonalLedgerService instance = new PersonalLedgerService();
    private PersonalLedgerService() {}
    public static PersonalLedgerService getInstance() { return instance; }

    // 개인 카테고리 합계
    public List<ChartDTO> getCategorySumForChart(int userNum, String transType, String targetMonth) throws Exception {
        return PersonalTransactionDAO.getInstance().getCategorySumForChart(userNum, transType, targetMonth);
    }
    
}