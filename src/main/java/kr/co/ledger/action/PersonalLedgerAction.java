package kr.co.ledger.action;

import java.io.PrintWriter;
import java.util.List;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import kr.co.ledger.dto.ChartDTO;
import kr.co.ledger.dto.RatioDTO;
import kr.co.ledger.dto.UserDTO;
import kr.co.ledger.service.PersonalLedgerService;
import kr.co.ledger.util.UriUtil;

public class PersonalLedgerAction implements Action {

    @Override
    public String execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
        
        String command = UriUtil.getCommand(request);
        String methodName = command.substring(command.lastIndexOf("/") + 1, command.lastIndexOf("."));
        
        return switch (methodName) {
            case "getChartData" -> getChartData(request, response);
            case "getRatioData" -> getRatioData(request, response);
            case "statistics" -> "/views/personal_ledger/statistics.jsp";
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
        if(targetMonth == null) targetMonth = "2026-08";
        
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
        if (targetMonth == null) targetMonth = "2026-08"; 

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
}