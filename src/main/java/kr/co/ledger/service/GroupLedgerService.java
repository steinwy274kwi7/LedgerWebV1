package kr.co.ledger.service;

import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import kr.co.ledger.dto.ChartDTO;
import kr.co.ledger.dto.GroupCategoryDTO;
import kr.co.ledger.dto.GroupTransactionDTO;
import kr.co.ledger.dto.TrendDTO;
import kr.co.ledger.dao.GroupCategoryDAO;
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
    
    // 공동 지출 내역 등록
    public boolean insertTransaction(GroupTransactionDTO dto) throws Exception {
        // 백엔드 2차 예외 차단 (0원 이하 차단)
        if (dto.getTransAmount() <= 0) {
            throw new Exception("결제 금액은 0원보다 커야 합니다.");
        }
        return GroupTransactionDAO.getInstance().insertTransaction(dto);
    }
    
	 // ==========================================
	 // 카테고리 관리 로직 시작
	 // ==========================================
	
	 // 1. 공통 검증 로직 (20자 제한, 예약어 차단)
	 private void validateCategoryName(String name) throws Exception {
	     if (name == null || name.trim().isEmpty()) {
	         throw new Exception("카테고리명을 입력해주세요.");
	     }
	     if (name.trim().length() > 20) {
	         throw new Exception("카테고리명은 최대 20자까지 가능합니다.");
	     }
	     if ("미분류".equals(name.trim())) {
	         throw new Exception("시스템 예약어인 '미분류'는 사용할 수 없습니다.");
	     }
	 }
	
	 // 2. 카테고리 목록 조회
	 public List<GroupCategoryDTO> getCategoryList(int groupNum) throws Exception {
	     return GroupCategoryDAO.getInstance().getCategoryList(groupNum);
	 }
	
	 // 3. 카테고리 등록
	 public boolean addCategory(int groupNum, String categoryName) throws Exception {
	     validateCategoryName(categoryName);
	     return GroupCategoryDAO.getInstance().insertCategory(groupNum, categoryName.trim());
	 }
	
	 // 4. 카테고리 수정
	 public boolean editCategory(int groupNum, int categoryNum, String categoryName) throws Exception {
	     validateCategoryName(categoryName);
	     return GroupCategoryDAO.getInstance().updateCategory(groupNum, categoryNum, categoryName.trim());
	 }
	
	 // 5. 카테고리 삭제 (미분류 이관 및 로그 기록)
	 public boolean removeCategory(int groupNum, int categoryNum, String categoryName, int actionUserNum) throws Exception {
	     if ("미분류".equals(categoryName)) throw new Exception("기본 항목(미분류)은 삭제할 수 없습니다.");
	     return GroupCategoryDAO.getInstance().deleteCategoryWithTransfer(groupNum, categoryNum, categoryName, actionUserNum);
	 }
 
}