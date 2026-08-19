package kr.co.ledger.action;

import java.io.PrintWriter;
import java.time.YearMonth;
import java.util.List;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import kr.co.ledger.dto.ChartDTO;
import kr.co.ledger.dto.TrendDTO;
import kr.co.ledger.dto.UserDTO;
import kr.co.ledger.service.GroupLedgerService;
import kr.co.ledger.util.UriUtil;

public class GroupLedgerAction implements Action {

    @Override
    public String execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String command = UriUtil.getCommand(request);
        String methodName = command.substring(command.lastIndexOf("/") + 1, command.lastIndexOf("."));
        
        return switch (methodName) {
            case "statistics" -> "/views/group_ledger/group_statistics.jsp"; 

            case "getCategoryChartData" -> getCategoryChartData(request, response);
            case "getTrendData" 		-> getTrendData(request, response);
            default -> throw new IllegalArgumentException("GroupLedgerAction에 없는 기능: " + command);
        };
    }

    // 그룹 카테고리 합계
    private String getCategoryChartData(HttpServletRequest request, HttpServletResponse response) throws Exception {

        UserDTO loginUser = (UserDTO) request.getSession().getAttribute("loginUser");
        if (loginUser == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); 
            return null;
        }
        int myUserNum = loginUser.getUserNum();
        
        String targetMonth = request.getParameter("month");
        if (targetMonth == null || targetMonth.isEmpty()) {
            targetMonth = YearMonth.now().toString(); 
        }

        List<ChartDTO> chartList = GroupLedgerService.getInstance().getAllMyGroupCategorySumForChart(myUserNum, targetMonth);

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
    
    // 그룹 6개월 추이
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

        List<TrendDTO> trendList = GroupLedgerService.getInstance().getRecent6MonthsGroupTrend(myUserNum, targetMonth);

        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();
        StringBuilder json = new StringBuilder();
        
        json.append("[");
        for (int i = 0; i < trendList.size(); i++) {
            TrendDTO dto = trendList.get(i);
            json.append("{");
            json.append("\"month\":\"").append(dto.getMonth()).append("\",");
            json.append("\"totalExpense\":").append(dto.getTotalExpense());
            json.append("}");
            if (i < trendList.size() - 1) json.append(",");
        }
        json.append("]");
        
        out.print(json.toString());
        out.flush();
        
        return null;
    }
}