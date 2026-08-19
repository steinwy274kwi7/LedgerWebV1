package kr.co.ledger.service;

import java.util.List;

import kr.co.ledger.dao.PersonalCategoryDAO;
import kr.co.ledger.dao.PersonalTransactionDAO;
import kr.co.ledger.dto.CalendarDTO;
import kr.co.ledger.dto.ChartDTO;
import kr.co.ledger.dto.PersonalCategoryDTO;
import kr.co.ledger.dto.PersonalTransactionDTO;
import kr.co.ledger.dto.RatioDTO;
import kr.co.ledger.dto.TrendDTO;

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
    
    // 개인 6개월 추이
    public List<TrendDTO> getRecent6MonthsTrend(int userNum, String targetMonth) throws Exception {
        return PersonalTransactionDAO.getInstance().getRecent6MonthsTrend(userNum, targetMonth);
    }
    
    // 개인 달력 뷰
    public List<CalendarDTO> getMonthlyCalendarData(int userNum, String targetMonth, String type, String keyword) throws Exception {
        return PersonalTransactionDAO.getInstance().getMonthlyCalendarData(userNum, targetMonth, type, keyword);
    }

    // 개인 리스트 뷰
    public List<PersonalTransactionDTO> getTransactionList(int userNum, String month, String date, String type, String keyword) throws Exception {
        return PersonalTransactionDAO.getInstance().getTransactionList(userNum, month, date, type, keyword);
    }
    
    // 개인 수입지출 등록
    public void insertTransaction(PersonalTransactionDTO dto) throws Exception {
        PersonalTransactionDAO.getInstance().insertTransaction(dto);
    }
    
    // 개인 수입지출 수정
    public void updateTransaction(PersonalTransactionDTO dto) throws Exception {
        PersonalTransactionDAO.getInstance().updateTransaction(dto);
    }
    
    // 개인 수입지출 삭제
    public void deleteTransaction(int transNum, int userNum) throws Exception {
        PersonalTransactionDAO.getInstance().deleteTransaction(transNum, userNum);
    }
    
    // 카테고리 목록 조회
    public List<PersonalCategoryDTO> getCategoryList(int userNum, String type) throws Exception {
        return PersonalCategoryDAO.getInstance().getCategoryList(userNum, type);
    }

    // 카테고리 등록 수정
    public void saveCategory(PersonalCategoryDTO dto) throws Exception {
        if (dto.getCategoryNum() == 0) {
            PersonalCategoryDAO.getInstance().insertCategory(dto);
        } else {
            PersonalCategoryDAO.getInstance().updateCategory(dto);
        }
    }

    // 카테고리 삭제
    public void deleteCategory(int categoryNum, int userNum, String type) throws Exception {
        PersonalCategoryDAO dao = PersonalCategoryDAO.getInstance();
        
        Integer unclassifiedNum = dao.getCategoryByName(userNum, "미분류", type);
        
        if (unclassifiedNum == null) {
            PersonalCategoryDTO newCat = new PersonalCategoryDTO();
            newCat.setUserNum(userNum);
            newCat.setCategoryName("미분류");
            newCat.setCategoryType(type);
            dao.insertCategory(newCat);
            
            unclassifiedNum = dao.getCategoryByName(userNum, "미분류", type);
        }
        
        dao.moveTransactionsCategory(userNum, categoryNum, unclassifiedNum);
        dao.deleteCategory(categoryNum, userNum);
    }
    
}