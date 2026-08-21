package kr.co.ledger.action;

import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.co.ledger.dto.CalendarDTO;
import kr.co.ledger.dto.ChartDTO;
import kr.co.ledger.dto.PersonalCategoryDTO;
import kr.co.ledger.dto.PersonalTransactionDTO;
import kr.co.ledger.dto.RatioDTO;
import kr.co.ledger.dto.TrendDTO;
import kr.co.ledger.dto.UserDTO;
import kr.co.ledger.service.PersonalLedgerService;
import kr.co.ledger.service.UserService;
import kr.co.ledger.util.UriUtil;

public class PersonalLedgerAction implements Action {

    @Override
    public String execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
        
        String command = UriUtil.getCommand(request);
        String methodName = command.substring(command.lastIndexOf("/") + 1, command.lastIndexOf("."));
        
        return switch (methodName) {
            case "getChartData"         -> getChartData(request, response);
            case "getRatioData"         -> getRatioData(request, response);
            case "statistics"           -> "/views/personal_ledger/statistics.jsp";
            case "getTrendData"         -> getTrendData(request, response);
            case "getCalendarData"      -> getCalendarData(request, response);
            case "getTransactionList"   -> getTransactionList(request, response);
            case "calendar"             -> "/views/personal_ledger/personal_calendar.jsp";
            case "saveTransaction"      -> saveTransaction(request, response);
            case "deleteTransaction"    -> deleteTransaction(request, response);
            case "getCategoryList"      -> getCategoryList(request, response);
            case "saveCategory"         -> saveCategory(request, response);
            case "deleteCategory"       -> deleteCategory(request, response);
            case "togglePublic"         -> togglePublic(request, response);
            default -> throw new IllegalArgumentException("PersonalLedgerAction에 없는 기능: " + command);
        };
    }

    // ==========================================================
    // 🌟 [리팩토링] 공통 JSON 응답 헬퍼 메서드
    // ==========================================================
    private String sendJson(HttpServletResponse response, String jsonString) throws Exception {
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();
        out.print(jsonString != null ? jsonString : "[]");
        out.flush();
        return null;
    }

    private String sendAjaxResult(HttpServletResponse response, boolean success, String message) throws Exception {
        String safeMessage = message != null ? message.replace("\"", "\\\"").replace("\n", " ") : "";
        String jsonString = "{\"success\": " + success + ", \"message\": \"" + safeMessage + "\"}";
        return sendJson(response, jsonString);
    }
    // ==========================================================

    // 개인 카테고리 합계 (차트용)
    private String getChartData(HttpServletRequest request, HttpServletResponse response) throws Exception {
        UserDTO loginUser = (UserDTO) request.getSession().getAttribute("loginUser");
        if (loginUser == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); 
            return sendJson(response, "[]");
        }

        int targetUserNum = loginUser.getUserNum(); 
        String targetNumStr = request.getParameter("targetUserNum");
        if (targetNumStr != null && !targetNumStr.isEmpty()) targetUserNum = Integer.parseInt(targetNumStr);
        
        String transType = request.getParameter("type"); 
        if(transType == null) transType = "E";
        
        String targetMonth = request.getParameter("month");
        if(targetMonth == null || targetMonth.isEmpty()) targetMonth = YearMonth.now().toString();
        
        List<ChartDTO> chartList = PersonalLedgerService.getInstance().getCategorySumForChart(targetUserNum, transType, targetMonth);
        
        StringBuilder json = new StringBuilder("[");
        for (int i = 0; i < chartList.size(); i++) {
            ChartDTO dto = chartList.get(i);
            json.append(String.format("{\"categoryName\":\"%s\", \"totalAmount\":%d}", dto.getCategoryName(), dto.getTotalAmount()));
            if (i < chartList.size() - 1) json.append(",");
        }
        json.append("]");
        
        return sendJson(response, json.toString());
    }
    
    // 개인 흑자, 적자 비율
    private String getRatioData(HttpServletRequest request, HttpServletResponse response) throws Exception {
        UserDTO loginUser = (UserDTO) request.getSession().getAttribute("loginUser");
        if (loginUser == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); 
            return sendJson(response, "{}");
        }

        int targetUserNum = loginUser.getUserNum(); 
        String targetNumStr = request.getParameter("targetUserNum");
        if (targetNumStr != null && !targetNumStr.isEmpty()) targetUserNum = Integer.parseInt(targetNumStr);

        String targetMonth = request.getParameter("month");
        if(targetMonth == null || targetMonth.isEmpty()) targetMonth = YearMonth.now().toString();

        RatioDTO ratioData = PersonalLedgerService.getInstance().calculateMonthlyRatio(targetUserNum, targetMonth);
        
        String json = String.format(
            "{\"totalIncome\":%d, \"totalExpense\":%d, \"expenseRatio\":%.1f, \"statusMessage\":\"%s\"}",
            ratioData.getTotalIncome(), ratioData.getTotalExpense(), ratioData.getExpenseRatio(), ratioData.getStatusMessage()
        );
        
        return sendJson(response, json);
    }
    
    // 개인 6개월 추이
    private String getTrendData(HttpServletRequest request, HttpServletResponse response) throws Exception {
        UserDTO loginUser = (UserDTO) request.getSession().getAttribute("loginUser");
        if (loginUser == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); 
            return sendJson(response, "[]");
        }

        int targetUserNum = loginUser.getUserNum(); 
        String targetNumStr = request.getParameter("targetUserNum");
        if (targetNumStr != null && !targetNumStr.isEmpty()) targetUserNum = Integer.parseInt(targetNumStr);

        String targetMonth = request.getParameter("month");
        if(targetMonth == null || targetMonth.isEmpty()) targetMonth = YearMonth.now().toString();

        List<TrendDTO> trendList = PersonalLedgerService.getInstance().getRecent6MonthsTrend(targetUserNum, targetMonth);
        
        StringBuilder json = new StringBuilder("[");
        for (int i = 0; i < trendList.size(); i++) {
            TrendDTO dto = trendList.get(i);
            json.append(String.format("{\"month\":\"%s\", \"totalIncome\":%d, \"totalExpense\":%d}", 
                        dto.getMonth(), dto.getTotalIncome(), dto.getTotalExpense()));
            if (i < trendList.size() - 1) json.append(",");
        }
        json.append("]");
        
        return sendJson(response, json.toString());
    }
    
    // 개인 달력 뷰
    private String getCalendarData(HttpServletRequest request, HttpServletResponse response) throws Exception {
        UserDTO loginUser = (UserDTO) request.getSession().getAttribute("loginUser");
        if (loginUser == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); 
            return sendJson(response, "[]");
        }

        int targetUserNum = loginUser.getUserNum();
        String targetNumStr = request.getParameter("targetUserNum");
        if (targetNumStr != null && !targetNumStr.isEmpty()) targetUserNum = Integer.parseInt(targetNumStr);

        String targetMonth = request.getParameter("month");
        if (targetMonth == null || targetMonth.isEmpty()) targetMonth = YearMonth.now().toString();

        String type = request.getParameter("type");
        if (type == null) type = "ALL";
        String keyword = request.getParameter("keyword");

        List<CalendarDTO> list = PersonalLedgerService.getInstance().getMonthlyCalendarData(targetUserNum, targetMonth, type, keyword);
        
        StringBuilder json = new StringBuilder("[");
        for (int i = 0; i < list.size(); i++) {
            CalendarDTO dto = list.get(i);
            json.append(String.format("{\"date\":\"%s\", \"dailyIncome\":%d, \"dailyExpense\":%d}", 
                        dto.getDate(), dto.getDailyIncome(), dto.getDailyExpense()));
            if (i < list.size() - 1) json.append(",");
        }
        json.append("]");
        
        return sendJson(response, json.toString());
    }

    // 개인 리스트 뷰
    private String getTransactionList(HttpServletRequest request, HttpServletResponse response) throws Exception {
        UserDTO loginUser = (UserDTO) request.getSession().getAttribute("loginUser");
        if (loginUser == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); 
            return sendJson(response, "[]");
        }

        int targetUserNum = loginUser.getUserNum();
        String targetNumStr = request.getParameter("targetUserNum");
        if (targetNumStr != null && !targetNumStr.isEmpty()) targetUserNum = Integer.parseInt(targetNumStr);

        String month = request.getParameter("month");
        if (month == null || month.isEmpty()) month = YearMonth.now().toString();
        
        String date = request.getParameter("date");
        String type = request.getParameter("type");
        if (type == null) type = "ALL";
        String keyword = request.getParameter("keyword");

        List<PersonalTransactionDTO> list = PersonalLedgerService.getInstance().getTransactionList(targetUserNum, month, date, type, keyword);
        
        StringBuilder json = new StringBuilder("[");
        for (int i = 0; i < list.size(); i++) {
            PersonalTransactionDTO dto = list.get(i);
            String safeMemo = dto.getTransMemo() != null ? dto.getTransMemo().replace("\\", "\\\\").replace("\"", "\\\"").replaceAll("[\\r\\n\\t]", " ") : "";
            
            json.append(String.format(
                "{\"transNum\":%d, \"transType\":\"%s\", \"categoryNum\":%d, \"categoryName\":\"%s\", \"transAmount\":%d, \"transDate\":\"%s\", \"transMemo\":\"%s\"}",
                dto.getTransNum(), dto.getTransType(), dto.getCategoryNum(), dto.getCategoryName(), dto.getTransAmount(), dto.getTransDate(), safeMemo
            ));
            if (i < list.size() - 1) json.append(",");
        }
        json.append("]");
        
        return sendJson(response, json.toString());
    }
    
    // 개인 수입지출 등록 및 수정
    private String saveTransaction(HttpServletRequest request, HttpServletResponse response) throws Exception {
        UserDTO loginUser = (UserDTO) request.getSession().getAttribute("loginUser");
        if (loginUser == null) return sendAjaxResult(response, false, "로그인이 필요합니다.");

        try {
            String transNumStr = request.getParameter("transNum");
            String transDate = request.getParameter("transDate");
            long transAmount = Long.parseLong(request.getParameter("transAmount"));
            String transMemo = request.getParameter("transMemo");
            
            LocalDate inputDate = LocalDate.parse(transDate);
            if (inputDate.isAfter(LocalDate.now())) return sendAjaxResult(response, false, "미래 날짜는 등록할 수 없습니다.");
            if (transAmount <= 0) return sendAjaxResult(response, false, "금액은 1원 이상이어야 합니다.");
            if (transMemo != null && transMemo.length() > 100) return sendAjaxResult(response, false, "메모는 100자를 초과할 수 없습니다.");

            PersonalTransactionDTO dto = new PersonalTransactionDTO();
            dto.setUserNum(loginUser.getUserNum());
            dto.setTransDate(transDate);
            dto.setTransType(request.getParameter("transType"));
            dto.setCategoryNum(Integer.parseInt(request.getParameter("categoryNum")));
            dto.setTransAmount(transAmount);
            dto.setTransMemo(transMemo);

            if (transNumStr == null || transNumStr.isEmpty()) {
                PersonalLedgerService.getInstance().insertTransaction(dto);
            } else {
                dto.setTransNum(Integer.parseInt(transNumStr));
                PersonalLedgerService.getInstance().updateTransaction(dto);
            }
            return sendAjaxResult(response, true, "내역이 저장되었습니다.");
        } catch (Exception e) {
            return sendAjaxResult(response, false, "처리 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    // 개인 수입지출 삭제
    private String deleteTransaction(HttpServletRequest request, HttpServletResponse response) throws Exception {
        UserDTO loginUser = (UserDTO) request.getSession().getAttribute("loginUser");
        if (loginUser == null) return sendAjaxResult(response, false, "로그인이 필요합니다.");
        
        try {
            int transNum = Integer.parseInt(request.getParameter("transNum"));
            PersonalLedgerService.getInstance().deleteTransaction(transNum, loginUser.getUserNum());
            return sendAjaxResult(response, true, "삭제되었습니다.");
        } catch (Exception e) {
            return sendAjaxResult(response, false, "삭제 실패: " + e.getMessage());
        }
    }
    
    // 카테고리 목록 조회
    private String getCategoryList(HttpServletRequest request, HttpServletResponse response) throws Exception {
        UserDTO loginUser = (UserDTO) request.getSession().getAttribute("loginUser");
        if (loginUser == null) return sendJson(response, "[]");
        
        String type = request.getParameter("type");
        if (type == null) type = "E"; 

        int targetUserNum = loginUser.getUserNum();
        String targetNumStr = request.getParameter("targetUserNum");
        if (targetNumStr != null && !targetNumStr.isEmpty()) targetUserNum = Integer.parseInt(targetNumStr);

        List<PersonalCategoryDTO> list = PersonalLedgerService.getInstance().getCategoryList(targetUserNum, type);

        StringBuilder json = new StringBuilder("[");
        for (int i = 0; i < list.size(); i++) {
            PersonalCategoryDTO dto = list.get(i);
            json.append(String.format("{\"categoryNum\":%d, \"categoryName\":\"%s\"}", dto.getCategoryNum(), dto.getCategoryName()));
            if (i < list.size() - 1) json.append(",");
        }
        json.append("]");
        return sendJson(response, json.toString());
    }

    // 카테고리 등록 및 수정
    private String saveCategory(HttpServletRequest request, HttpServletResponse response) throws Exception {
        UserDTO loginUser = (UserDTO) request.getSession().getAttribute("loginUser");
        if (loginUser == null) return sendAjaxResult(response, false, "로그인이 필요합니다.");

        try {
            String catNumStr = request.getParameter("categoryNum");
            String catName = request.getParameter("categoryName").trim();
            String catType = request.getParameter("categoryType");

            if ("미분류".equals(catName)) return sendAjaxResult(response, false, "'미분류'는 시스템 예약어라 사용할 수 없습니다.");
            if (catName.length() > 20) return sendAjaxResult(response, false, "카테고리명은 최대 20자까지만 가능합니다.");

            PersonalCategoryDTO dto = new PersonalCategoryDTO();
            dto.setUserNum(loginUser.getUserNum());
            dto.setCategoryName(catName);
            dto.setCategoryType(catType);
            dto.setCategoryNum(catNumStr == null || catNumStr.isEmpty() ? 0 : Integer.parseInt(catNumStr));

            PersonalLedgerService.getInstance().saveCategory(dto);
            return sendAjaxResult(response, true, "카테고리가 저장되었습니다.");
        } catch (Exception e) {
            return sendAjaxResult(response, false, "카테고리 저장 실패: " + e.getMessage());
        }
    }

    // 카테고리 삭제
    private String deleteCategory(HttpServletRequest request, HttpServletResponse response) throws Exception {
        UserDTO loginUser = (UserDTO) request.getSession().getAttribute("loginUser");
        if (loginUser == null) return sendAjaxResult(response, false, "로그인이 필요합니다.");
        
        try {
            int catNum = Integer.parseInt(request.getParameter("categoryNum"));
            String catType = request.getParameter("categoryType"); 

            PersonalLedgerService.getInstance().deleteCategory(catNum, loginUser.getUserNum(), catType);
            return sendAjaxResult(response, true, "카테고리가 삭제되었습니다.");
        } catch (Exception e) {
            return sendAjaxResult(response, false, "카테고리 삭제 실패: " + e.getMessage());
        }
    }
    
    // 개인 가계부 공개/비공개 설정
    private String togglePublic(HttpServletRequest request, HttpServletResponse response) throws Exception {
        UserDTO loginUser = (UserDTO) request.getSession().getAttribute("loginUser");
        if (loginUser == null) return sendAjaxResult(response, false, "로그인이 필요합니다.");
        
        try {
            String targetYn = request.getParameter("bookOpenYn");
            if (targetYn == null || (!targetYn.equals("Y") && !targetYn.equals("N"))) targetYn = "N";
            
            UserService.getInstance().updateBookOpenYn(loginUser.getUserNum(), targetYn);

            loginUser.setBookOpenYn(targetYn);
            request.getSession().setAttribute("loginUser", loginUser);

            // 성공했지만 return 형태가 조금 다르므로 커스텀 JSON 문자열을 만들어 sendJson 호출
            String json = String.format("{\"success\":true, \"currentYn\":\"%s\"}", targetYn);
            return sendJson(response, json);
        } catch (Exception e) {
            return sendAjaxResult(response, false, "설정 변경 실패: " + e.getMessage());
        }
    }
}