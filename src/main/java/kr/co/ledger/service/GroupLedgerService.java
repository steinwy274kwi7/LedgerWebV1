package kr.co.ledger.service;

import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import kr.co.ledger.dto.ChartDTO;
import kr.co.ledger.dto.ExpenseLogDTO;
import kr.co.ledger.dto.GroupCategoryDTO;
import kr.co.ledger.dto.GroupTransactionDTO;
import kr.co.ledger.dto.LedgerPeriodDTO;
import kr.co.ledger.dto.SettlementSnapshotDTO;
import kr.co.ledger.dto.TrendDTO;
import kr.co.ledger.dao.GroupCategoryDAO;
import kr.co.ledger.dao.GroupDAO;
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
    
	 // 공통 검증 로직 (20자 제한, 예약어 차단)
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
	
	 // 카테고리 목록 조회
	 public List<GroupCategoryDTO> getCategoryList(int groupNum) throws Exception {
	     return GroupCategoryDAO.getInstance().getCategoryList(groupNum);
	 }
	
	 // 카테고리 등록
	 public boolean addCategory(int groupNum, String categoryName) throws Exception {
	     validateCategoryName(categoryName);
	     return GroupCategoryDAO.getInstance().insertCategory(groupNum, categoryName.trim());
	 }
	
	 // 카테고리 수정
	 public boolean editCategory(int groupNum, int categoryNum, String categoryName) throws Exception {
	     validateCategoryName(categoryName);
	     return GroupCategoryDAO.getInstance().updateCategory(groupNum, categoryNum, categoryName.trim());
	 }
	
	// 카테고리 삭제 (미분류 이관 및 벌크 로그 기록)
	    public boolean removeCategory(int groupNum, int categoryNum, String categoryName, int actionUserNum) throws Exception {
	        if ("미분류".equals(categoryName)) {
	            throw new Exception("기본 항목(미분류)은 삭제할 수 없습니다.");
	        }
	        
	        // 벌크 인서트는 (카테고리 삭제/이관이 실행되기 '직전'에 호출)
	        GroupTransactionDAO.getInstance().insertBulkLogForCategoryDelete(categoryNum, categoryName, actionUserNum);
	        
	        // 그 후 기존처럼 카테고리 삭제(이관) 진행
	        return GroupCategoryDAO.getInstance().deleteCategoryWithTransfer(groupNum, categoryNum, categoryName, actionUserNum);
	    }
 
	// [공통] 권한 검증 로직 (작성자 본인이거나 방장인지 확인)
	 private void checkTransactionAuth(int originalUserNum, int actionUserNum, int groupOwnerNum) throws Exception {
	     if (originalUserNum != actionUserNum && actionUserNum != groupOwnerNum) {
	         throw new Exception("작성자 본인 또는 방장만 수정 및 삭제할 수 있습니다.");
	     }
	 }

	 // 지출 내역 수정
	 public boolean editTransaction(GroupTransactionDTO newDto, String newCatName, int actionUserNum, int groupOwnerNum) throws Exception {
	     // 1. DB에 저장되어 있는 기존 내역 가져오기
	     GroupTransactionDTO oldDto = GroupTransactionDAO.getInstance().getTransaction(newDto.getGtransNum());
	     if (oldDto == null) {
	         throw new Exception("존재하지 않거나 이미 삭제된 지출 내역입니다.");
	     }
	     
	     // 2. 권한 검증 (백엔드 이중 방어)
	     checkTransactionAuth(oldDto.getUserNum(), actionUserNum, groupOwnerNum);
	     
	     // 3. 트랜잭션 DAO 호출 (수정 및 로그 기록 동시 진행)
	     return GroupTransactionDAO.getInstance()
	             .updateTransactionWithLog(newDto, actionUserNum, oldDto.getCategoryName(), newCatName, oldDto.getTransAmount());
	 }

	 // 지출 내역 삭제
	 public boolean removeTransaction(int gtransNum, int actionUserNum, int groupOwnerNum) throws Exception {
	        // 1. 기존 내역 가져오기
	        GroupTransactionDTO oldDto = GroupTransactionDAO.getInstance().getTransaction(gtransNum);
	        if (oldDto == null) {
	            throw new Exception("존재하지 않거나 이미 삭제된 지출 내역입니다.");
	        }
	        
	        // 2. 권한 검증
	        checkTransactionAuth(oldDto.getUserNum(), actionUserNum, groupOwnerNum);
	        
	        // 3. 트랜잭션 DAO 호출 (소프트 딜리트 및 단건 로그 기록 동시 진행)
	        return GroupTransactionDAO.getInstance()
	                .deleteTransactionWithLog(gtransNum, actionUserNum, oldDto.getCategoryName(), oldDto.getTransAmount());
	    }
	 
	 	// 이력 조회 메서드
	    public List<ExpenseLogDTO> getExpenseLogs(int groupNum) throws Exception {
	        return GroupTransactionDAO.getInstance().getExpenseLogs(groupNum);
	    }
	    
	    // ==========================================================
	    // [과거 정산 보관함 1] 마감된 회차 목록 조회
	    // ==========================================================
	    public List<LedgerPeriodDTO> getClosedPeriods(int groupNum) throws Exception {
	        return GroupDAO.getInstance().getClosedPeriods(groupNum);
	    }

	    // ==========================================================
	    // [과거 정산 보관함 2] 특정 회차의 정산 스냅샷 조회
	    // ==========================================================
	    public List<SettlementSnapshotDTO> getSnapshots(int periodNum) throws Exception {
	        return GroupDAO.getInstance().getSnapshots(periodNum);
	    }

	    // ==========================================================
	    // [과거 정산 보관함 3] 특정 회차의 지출 내역 조회
	    // ==========================================================
	    public List<GroupTransactionDTO> getTransactionsByPeriod(int periodNum) throws Exception {
	        return GroupTransactionDAO.getInstance().getTransactionsByPeriod(periodNum);
	    }
	    
}