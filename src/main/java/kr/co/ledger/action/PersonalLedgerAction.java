package kr.co.ledger.action;

import java.io.PrintWriter;
import java.time.YearMonth;
import java.util.List;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.co.ledger.dto.CalendarDTO;
import kr.co.ledger.dto.ChartDTO;
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
}