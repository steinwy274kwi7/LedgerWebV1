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
import kr.co.ledger.util.UriUtil;

public class PersonalLedgerAction implements Action {

    @Override
    public String execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
        
        String command = UriUtil.getCommand(request);
        String methodName = command.substring(command.lastIndexOf("/") + 1, command.lastIndexOf("."));
        
        return switch (methodName) {
            case "getChartData" 		-> getChartData(request, response);
            case "getRatioData" 		-> getRatioData(request, response);
            case "statistics" 			-> "/views/personal_ledger/statistics.jsp";
            case "getTrendData" 		-> getTrendData(request, response);
            case "getCalendarData" 		-> getCalendarData(request, response);
            case "getTransactionList" 	-> getTransactionList(request, response);
            case "calendar" 			-> "/views/personal_ledger/personal_calendar.jsp";
            case "saveTransaction" 		-> saveTransaction(request, response);
            case "deleteTransaction" 	-> deleteTransaction(request, response);
            case "getCategoryList"	 	-> getCategoryList(request, response);
            case "saveCategory"			-> saveCategory(request, response);
            case "deleteCategory"		-> deleteCategory(request, response);
            default -> throw new IllegalArgumentException("PersonalLedgerAction에 없는 기능: " + command);
        };
    }

    // 개인 카테고리 합계
    private String getChartData(HttpServletRequest request, HttpServletResponse response) throws Exception {
        
        UserDTO loginUser = (UserDTO) request.getSession().getAttribute("loginUser");
        if (loginUser == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); 
            return null;
        }

        int myUserNum = loginUser.getUserNum();
        
        String transType = request.getParameter("type"); 
        if(transType == null) transType = "E";
        
        String targetMonth = request.getParameter("month");
        if(targetMonth == null || targetMonth.isEmpty()) {
            targetMonth = YearMonth.now().toString();
        }
        
        List<ChartDTO> chartList = PersonalLedgerService.getInstance().getCategorySumForChart(myUserNum, transType, targetMonth);
        
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();
        StringBuilder json = new StringBuilder();
        
        json.append("[");
        for (int i = 0; i < chartList.size(); i++) {
            ChartDTO dto = chartList.get(i);
            json.append("{");
            json.append("\"categoryName\":\"").append(dto.getCategoryName()).append("\",");
            json.append("\"totalAmount\":").append(dto.getTotalAmount());
            json.append("}");
            if (i < chartList.size() - 1) json.append(",");
        }
        json.append("]");
        
        out.print(json.toString());
        out.flush();
        
        return null;
    }
    
    // 개인 흑자, 적자 비율
    private String getRatioData(HttpServletRequest request, HttpServletResponse response) throws Exception {
        UserDTO loginUser = (UserDTO) request.getSession().getAttribute("loginUser");
        if (loginUser == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); 
            return null;
        }

        int myUserNum = loginUser.getUserNum();
        String targetMonth = request.getParameter("month");
        
        if(targetMonth == null || targetMonth.isEmpty()) {
            targetMonth = YearMonth.now().toString();
        }

        RatioDTO ratioData = PersonalLedgerService.getInstance().calculateMonthlyRatio(myUserNum, targetMonth);

        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();
        
        String json = String.format(
            "{\"totalIncome\":%d, \"totalExpense\":%d, \"expenseRatio\":%.1f, \"statusMessage\":\"%s\"}",
            ratioData.getTotalIncome(), ratioData.getTotalExpense(), ratioData.getExpenseRatio(), ratioData.getStatusMessage()
        );
        
        out.print(json);
        out.flush();
        
        return null;
    }
    
    // 개인 6개월 추이
    private String getTrendData(HttpServletRequest request, HttpServletResponse response) throws Exception {
        UserDTO loginUser = (UserDTO) request.getSession().getAttribute("loginUser");
        if (loginUser == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); 
            return null;
        }

        int myUserNum = loginUser.getUserNum();
        String targetMonth = request.getParameter("month");
        
        if(targetMonth == null || targetMonth.isEmpty()) {
            targetMonth = YearMonth.now().toString();
        }

        List<TrendDTO> trendList = PersonalLedgerService.getInstance().getRecent6MonthsTrend(myUserNum, targetMonth);

        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();
        StringBuilder json = new StringBuilder();
        
        json.append("[");
        for (int i = 0; i < trendList.size(); i++) {
            TrendDTO dto = trendList.get(i);
            json.append("{");
            json.append("\"month\":\"").append(dto.getMonth()).append("\",");
            json.append("\"totalIncome\":").append(dto.getTotalIncome()).append(",");
            json.append("\"totalExpense\":").append(dto.getTotalExpense());
            json.append("}");
            if (i < trendList.size() - 1) json.append(",");
        }
        json.append("]");
        
        out.print(json.toString());
        out.flush();
        
        return null;
    }
    
    // 개인 달력 뷰
    private String getCalendarData(HttpServletRequest request, HttpServletResponse response) throws Exception {
        UserDTO loginUser = (UserDTO) request.getSession().getAttribute("loginUser");
        if (loginUser == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); 
            return null;
        }

        String targetMonth = request.getParameter("month");
        if (targetMonth == null || targetMonth.isEmpty()) targetMonth = YearMonth.now().toString();

        String type = request.getParameter("type");
        if (type == null) type = "ALL";
        String keyword = request.getParameter("keyword");

        List<CalendarDTO> list = PersonalLedgerService.getInstance().getMonthlyCalendarData(loginUser.getUserNum(), targetMonth, type, keyword);
        
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();
        StringBuilder json = new StringBuilder();
        
        json.append("[");
        for (int i = 0; i < list.size(); i++) {
            CalendarDTO dto = list.get(i);
            json.append("{")
                .append("\"date\":\"").append(dto.getDate()).append("\",")
                .append("\"dailyIncome\":").append(dto.getDailyIncome()).append(",")
                .append("\"dailyExpense\":").append(dto.getDailyExpense())
                .append("}");
            if (i < list.size() - 1) json.append(",");
        }
        json.append("]");
        
        out.print(json.toString());
        out.flush();
        return null;
    }

    // 개인 리스트 뷰
    private String getTransactionList(HttpServletRequest request, HttpServletResponse response) throws Exception {
        UserDTO loginUser = (UserDTO) request.getSession().getAttribute("loginUser");
        if (loginUser == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); 
            return null;
        }

        String month = request.getParameter("month");
        if (month == null || month.isEmpty()) month = YearMonth.now().toString();
        
        String date = request.getParameter("date");
        String type = request.getParameter("type");
        if (type == null) type = "ALL";
        String keyword = request.getParameter("keyword");

        List<PersonalTransactionDTO> list = PersonalLedgerService.getInstance().getTransactionList(loginUser.getUserNum(), month, date, type, keyword);
        
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();
        StringBuilder json = new StringBuilder();
        
        json.append("[");
        for (int i = 0; i < list.size(); i++) {
            PersonalTransactionDTO dto = list.get(i);
            json.append("{")
                .append("\"transNum\":").append(dto.getTransNum()).append(",")
                .append("\"transType\":\"").append(dto.getTransType()).append("\",")
                .append("\"categoryNum\":").append(dto.getCategoryNum()).append(",")
                .append("\"categoryName\":\"").append(dto.getCategoryName()).append("\",")
                .append("\"transAmount\":").append(dto.getTransAmount()).append(",")
                .append("\"transDate\":\"").append(dto.getTransDate()).append("\",")
                .append("\"transMemo\":\"").append(dto.getTransMemo() == null ? "" : dto.getTransMemo().replace("\"", "\\\"")).append("\"")
                .append("}");
            if (i < list.size() - 1) json.append(",");
        }
        json.append("]");
        
        out.print(json.toString());
        out.flush();
        return null;
    }
    
    // 개인 수입지출 등록 수정
    private String saveTransaction(HttpServletRequest request, HttpServletResponse response) throws Exception {
        UserDTO loginUser = (UserDTO) request.getSession().getAttribute("loginUser");
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();

        try {
            String transNumStr = request.getParameter("transNum");
            String transDate = request.getParameter("transDate");
            long transAmount = Long.parseLong(request.getParameter("transAmount"));
            String transMemo = request.getParameter("transMemo");
            
            LocalDate inputDate = LocalDate.parse(transDate);
            if (inputDate.isAfter(LocalDate.now())) {
                out.print("{\"success\":false, \"message\":\"미래 날짜는 등록할 수 없습니다.\"}"); return null;
            }
            
            if (transAmount <= 0) {
                out.print("{\"success\":false, \"message\":\"금액은 1원 이상이어야 합니다.\"}"); return null;
            }
            
            if (transMemo != null && transMemo.length() > 100) {
                out.print("{\"success\":false, \"message\":\"메모는 100자를 초과할 수 없습니다.\"}"); return null;
            }

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
            out.print("{\"success\":true}");
        } catch (Exception e) {
            out.print("{\"success\":false, \"message\":\"처리 중 오류가 발생했습니다.\"}");
        }
        return null;
    }

    // 개인 수입지출 삭제
    private String deleteTransaction(HttpServletRequest request, HttpServletResponse response) throws Exception {
        UserDTO loginUser = (UserDTO) request.getSession().getAttribute("loginUser");
        int transNum = Integer.parseInt(request.getParameter("transNum"));
        
        PersonalLedgerService.getInstance().deleteTransaction(transNum, loginUser.getUserNum());
        
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().print("{\"success\":true}");
        return null;
    }
    
    // 카테고리 목록 조회
    private String getCategoryList(HttpServletRequest request, HttpServletResponse response) throws Exception {
        UserDTO loginUser = (UserDTO) request.getSession().getAttribute("loginUser");
        String type = request.getParameter("type");
        if (type == null) type = "E"; 

        List<PersonalCategoryDTO> list = PersonalLedgerService.getInstance().getCategoryList(loginUser.getUserNum(), type);

        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();
        StringBuilder json = new StringBuilder("[");
        for (int i = 0; i < list.size(); i++) {
            PersonalCategoryDTO dto = list.get(i);
            json.append("{\"categoryNum\":").append(dto.getCategoryNum())
                .append(", \"categoryName\":\"").append(dto.getCategoryName()).append("\"}");
            if (i < list.size() - 1) json.append(",");
        }
        json.append("]");
        out.print(json.toString());
        return null;
    }

    // 카테고리 등록 수정
    private String saveCategory(HttpServletRequest request, HttpServletResponse response) throws Exception {
        UserDTO loginUser = (UserDTO) request.getSession().getAttribute("loginUser");
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();

        String catNumStr = request.getParameter("categoryNum");
        String catName = request.getParameter("categoryName").trim();
        String catType = request.getParameter("categoryType");

        if ("미분류".equals(catName)) {
            out.print("{\"success\":false, \"message\":\"'미분류'는 시스템 예약어라 사용할 수 없습니다.\"}");
            return null;
        }
        if (catName.length() > 20) {
            out.print("{\"success\":false, \"message\":\"카테고리명은 최대 20자까지만 가능합니다.\"}");
            return null;
        }

        PersonalCategoryDTO dto = new PersonalCategoryDTO();
        dto.setUserNum(loginUser.getUserNum());
        dto.setCategoryName(catName);
        dto.setCategoryType(catType);
        dto.setCategoryNum(catNumStr == null || catNumStr.isEmpty() ? 0 : Integer.parseInt(catNumStr));

        PersonalLedgerService.getInstance().saveCategory(dto);
        out.print("{\"success\":true}");
        return null;
    }

    // 카테고리 삭제
    private String deleteCategory(HttpServletRequest request, HttpServletResponse response) throws Exception {
        UserDTO loginUser = (UserDTO) request.getSession().getAttribute("loginUser");
        int catNum = Integer.parseInt(request.getParameter("categoryNum"));
        String catType = request.getParameter("categoryType"); 

        PersonalLedgerService.getInstance().deleteCategory(catNum, loginUser.getUserNum(), catType);

        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().print("{\"success\":true}");
        return null;
    }
    
}