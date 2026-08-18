package kr.co.ledger.service;

import java.util.List;
import kr.co.ledger.dao.PersonalTransactionDAO;
import kr.co.ledger.dto.ChartDTO;
import kr.co.ledger.dto.RatioDTO;

public class PersonalLedgerService {
	
    private static PersonalLedgerService instance = new PersonalLedgerService();
    private PersonalLedgerService() {}
    public static PersonalLedgerService getInstance() { return instance; }

    // 개인 카테고리 합계
    public List<ChartDTO> getCategorySumForChart(int userNum, String transType, String targetMonth) throws Exception {
        return PersonalTransactionDAO.getInstance().getCategorySumForChart(userNum, transType, targetMonth);
    }
    
    // 개인 흑자, 적자 비율
    public RatioDTO calculateMonthlyRatio(int userNum, String targetMonth) throws Exception {
        
        RatioDTO dto = PersonalTransactionDAO.getInstance().getMonthlyTotalInOut(userNum, targetMonth);
        
        long income = dto.getTotalIncome();
        long expense = dto.getTotalExpense();
        
        if (income == 0) {
            if (expense == 0) {
                dto.setExpenseRatio(0.0);
                dto.setStatusMessage("이번 달 수입과 지출 내역이 없습니다.");
            } else {
                dto.setExpenseRatio(-1.0);
                dto.setStatusMessage("수입이 0원이라 비율을 계산할 수 없습니다. (적자)");
            }
        } else {
            double ratio = ((double) expense / income) * 100.0;
            dto.setExpenseRatio(Math.round(ratio * 10.0) / 10.0);
            
            if (ratio <= 100) {
                dto.setStatusMessage("현재 흑자 상태입니다. 잘하고 계세요!");
            } else {
                dto.setStatusMessage("수입보다 지출이 많습니다! (적자)");
            }
        }
        
        return dto;
    }
}