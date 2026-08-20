package kr.co.ledger.service;

import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import kr.co.ledger.dto.ChartDTO;
import kr.co.ledger.dto.GroupTransactionDTO;
import kr.co.ledger.dto.TrendDTO;
import kr.co.ledger.dao.GroupTransactionDAO;

public class GroupLedgerService {

    private static GroupLedgerService instance = new GroupLedgerService();
    private GroupLedgerService() {}
    public static GroupLedgerService getInstance() { return instance; }

    // 그룹 카테고리 합계
    public List<ChartDTO> getAllMyGroupCategorySumForChart(int userNum, String targetMonth) throws Exception {
        return GroupTransactionDAO.getInstance().getAllMyGroupCategorySumForChart(userNum, targetMonth);
    }
    
    // 그룹 6개월 추이
    public List<TrendDTO> getRecent6MonthsGroupTrend(int userNum, String targetMonth) throws Exception {
        
        List<TrendDTO> dbList = GroupTransactionDAO.getInstance().getAllMyGroupTrendForChart(userNum, targetMonth);
        List<TrendDTO> resultList = new ArrayList<>();
        
        YearMonth endMonth = YearMonth.parse(targetMonth);
        
        for (int i = 5; i >= 0; i--) {
            String monthStr = endMonth.minusMonths(i).toString();
            
            TrendDTO dto = new TrendDTO();
            dto.setMonth(monthStr);
            dto.setTotalExpense(0);
            
            for (TrendDTO dbDto : dbList) {
                if (dbDto.getMonth().equals(monthStr)) {
                    dto.setTotalExpense(dbDto.getTotalExpense());
                    break;
                }
            }
            resultList.add(dto);
        }
        return resultList;
    }
    
    // 그룹 달력 리스트 뷰 보기
    public List<GroupTransactionDTO> getMonthlyTransactions(int groupNum, String yearMonth) throws Exception {
        if (yearMonth == null || yearMonth.isEmpty()) {
            return new ArrayList<>();
        }
        return GroupTransactionDAO.getInstance().getMonthlyTransactions(groupNum, yearMonth);
    }
    
}