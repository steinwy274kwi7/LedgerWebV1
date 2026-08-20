package kr.co.ledger.action;

import java.io.PrintWriter;
import java.time.YearMonth;
import java.util.List;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.co.ledger.dao.GroupDAO;
import kr.co.ledger.dto.ChartDTO;
import kr.co.ledger.dto.ExpenseLogDTO;
import kr.co.ledger.dto.GroupCategoryDTO;
import kr.co.ledger.dto.GroupDTO;
import kr.co.ledger.dto.GroupTransactionDTO;
import kr.co.ledger.dto.TrendDTO;
import kr.co.ledger.dto.UserDTO;
import kr.co.ledger.service.GroupLedgerService;
import kr.co.ledger.service.GroupManageService;
import kr.co.ledger.util.UriUtil;

public class GroupLedgerAction implements Action {

    @Override
    public String execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String command = UriUtil.getCommand(request);
        String methodName = command.substring(command.lastIndexOf("/") + 1, command.lastIndexOf(".")).trim();
        
        return switch (methodName) {
            case "statistics" -> "/views/group_ledger/group_statistics.jsp"; 

            case "getCategoryChartData" -> getCategoryChartData(request, response);
            case "getTrendData" 		-> getTrendData(request, response);
            case "ledger" 				-> getGroupLedgerMain(request, response);
            case "getTransactions" 		-> getMonthlyTransactions(request, response);
            case "insert"  				-> insertTransaction(request, response);
            case "getCategoryList" 		-> getCategoryList(request, response);
            case "addCategory"     		-> addCategory(request, response);
            case "editCategory"    		-> editCategory(request, response);
            case "removeCategory"  		-> removeCategory(request, response);
            case "editTransaction"   	-> editTransaction(request, response);
            case "removeTransaction" 	-> removeTransaction(request, response);
            case "getLogs" 				-> getLogs(request, response);
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
    
    private String getGroupLedgerMain(HttpServletRequest request, HttpServletResponse response) throws Exception {

        UserDTO loginUser = (UserDTO) request.getSession().getAttribute("loginUser");
        if (loginUser == null) {
            return "redirect:" + request.getContextPath() + "/user/loginForm.do";
        }

        String groupNumStr = request.getParameter("groupNum");
        if (groupNumStr == null || groupNumStr.isEmpty()) {
            return "redirect:" + request.getContextPath() + "/group/list.do";
        }

        int groupNum = Integer.parseInt(groupNumStr);

        GroupDTO group = GroupManageService.getInstance().getGroupInfo(groupNum);
        
        GroupDAO dao = GroupDAO.getInstance();
        
        boolean isMember = dao.isUserAlreadyInGroupOrInvited(groupNum, loginUser.getUserNum(), "checkAlreadyMember");
        
        // 멤버가 아닌데 비공개 방(N)에 접근하려 하면 차단
        if (!isMember && "N".equals(group.getGroupOpenYn())) {
        	
            response.setContentType("text/html; charset=UTF-8");
            PrintWriter out = response.getWriter();
            out.println("<script>");
            out.println("alert('비공개 그룹이거나 접근 권한이 없습니다.');");
            out.println("location.href='" + request.getContextPath() + "/group/list.do';");
            out.println("</script>");
            out.flush();
            return null;
        }
        
        // JSP에서 버튼들을 숨기기 위해 멤버 여부(isMember) 전달
        request.setAttribute("isMember", isMember);
        
        // 4. 그룹 정보 전달 및 화면 이동
        request.setAttribute("group", group);
        
        return "/views/group_ledger/group_main.jsp";
    }
    
    // 그룹 달력 리스트 뷰 보기 (AJAX)
    private String getMonthlyTransactions(HttpServletRequest request, HttpServletResponse response) throws Exception {
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();
        
        try {
            int groupNum = Integer.parseInt(request.getParameter("groupNum"));
            String yearMonth = request.getParameter("yearMonth"); // 예: "2023-10"
            
            // 4단계에서 만든 서비스 메서드 호출
            List<GroupTransactionDTO> list = GroupLedgerService.getInstance().getMonthlyTransactions(groupNum, yearMonth);
            
         // JSON 배열로 직접 조립해서 반환
            StringBuilder json = new StringBuilder("[");
            for (int i = 0; i < list.size(); i++) {
                GroupTransactionDTO dto = list.get(i);
                
                json.append(String.format(
                    "{\"gtransNum\":%d, \"userNum\":%d, \"userNickname\":\"%s\", \"categoryName\":\"%s\", \"transAmount\":%d, \"transDate\":\"%s\", \"transMemo\":\"%s\", \"periodStatus\":\"%s\"}",
                    dto.getGtransNum(), dto.getUserNum(), dto.getUserNickname(), 
                    dto.getCategoryName(), dto.getTransAmount(), dto.getTransDate(), dto.getTransMemo(), dto.getPeriodStatus()
                ));
                
                if (i < list.size() - 1) json.append(",");
            }
            json.append("]");
            
            out.print(json.toString());
        } catch (Exception e) {
            out.print("[]");
        } finally {
            out.flush();
        }
        return null;
    }
    
    // 공동 지출 내역 등록 (AJAX)
    private String insertTransaction(HttpServletRequest request, HttpServletResponse response) throws Exception {
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();
        
        try {
            // 세션에서 현재 로그인한 유저 정보 가져오기 (결제자 고정)
            UserDTO loginUser = (UserDTO) request.getSession().getAttribute("loginUser");
            if (loginUser == null) throw new Exception("로그인이 필요합니다.");

            GroupTransactionDTO dto = new GroupTransactionDTO();
            dto.setGroupNum(Integer.parseInt(request.getParameter("groupNum")));
            dto.setGcategoryNum(Integer.parseInt(request.getParameter("gcategoryNum")));
            dto.setTransAmount(Long.parseLong(request.getParameter("transAmount")));
            dto.setTransDate(request.getParameter("transDate"));
            dto.setTransMemo(request.getParameter("transMemo"));
            dto.setUserNum(loginUser.getUserNum()); // 결제자는 본인으로 강제 고정!

            boolean isSuccess = GroupLedgerService.getInstance().insertTransaction(dto);
            
            if (isSuccess) {
                out.print("{\"success\": true, \"message\": \"지출 내역이 등록되었습니다.\"}");
            } else {
                out.print("{\"success\": false, \"message\": \"등록에 실패했습니다.\"}");
            }
        } catch (Exception e) {
            out.print("{\"success\": false, \"message\": \"" + e.getMessage() + "\"}");
        } finally {
            out.flush();
        }
        return null;
    }
    
    // [카테고리 목록 불러오기]
    private String getCategoryList(HttpServletRequest request, HttpServletResponse response) throws Exception {
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();
        try {
            int groupNum = Integer.parseInt(request.getParameter("groupNum"));
            List<GroupCategoryDTO> list = GroupLedgerService.getInstance().getCategoryList(groupNum);
            
            StringBuilder json = new StringBuilder("[");
            for (int i = 0; i < list.size(); i++) {
                GroupCategoryDTO dto = list.get(i);
                json.append(String.format("{\"gcategoryNum\":%d, \"categoryName\":\"%s\"}", dto.getGcategoryNum(), dto.getCategoryName()));
                if (i < list.size() - 1) json.append(",");
            }
            json.append("]");
            out.print(json.toString());
        } catch (Exception e) { out.print("[]"); } 
        finally { out.flush(); }
        return null;
    }

    // [카테고리 등록]
    private String addCategory(HttpServletRequest request, HttpServletResponse response) throws Exception {
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();
        try {
            int groupNum = Integer.parseInt(request.getParameter("groupNum"));
            String name = request.getParameter("categoryName");
            
            GroupLedgerService.getInstance().addCategory(groupNum, name);
            out.print("{\"success\": true, \"message\": \"카테고리가 등록되었습니다.\"}");
        } catch (Exception e) {
            out.print("{\"success\": false, \"message\": \"" + e.getMessage() + "\"}");
        } finally { out.flush(); }
        return null;
    }

    // [카테고리 수정]
    private String editCategory(HttpServletRequest request, HttpServletResponse response) throws Exception {
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();
        try {
            int groupNum = Integer.parseInt(request.getParameter("groupNum"));
            int categoryNum = Integer.parseInt(request.getParameter("categoryNum"));
            String name = request.getParameter("categoryName");
            
            GroupLedgerService.getInstance().editCategory(groupNum, categoryNum, name);
            out.print("{\"success\": true, \"message\": \"카테고리가 수정되었습니다.\"}");
        } catch (Exception e) {
            out.print("{\"success\": false, \"message\": \"" + e.getMessage() + "\"}");
        } finally { out.flush(); }
        return null;
    }

    // [카테고리 삭제]
    private String removeCategory(HttpServletRequest request, HttpServletResponse response) throws Exception {
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();
        try {
            UserDTO loginUser = (UserDTO) request.getSession().getAttribute("loginUser");
            int groupNum = Integer.parseInt(request.getParameter("groupNum"));
            int categoryNum = Integer.parseInt(request.getParameter("categoryNum"));
            String categoryName = request.getParameter("categoryName");
            
            GroupLedgerService.getInstance().removeCategory(groupNum, categoryNum, categoryName, loginUser.getUserNum());
            out.print("{\"success\": true, \"message\": \"삭제 완료! 관련 지출 내역은 '미분류'로 자동 이관되었습니다.\"}");
        } catch (Exception e) {
            out.print("{\"success\": false, \"message\": \"" + e.getMessage() + "\"}");
        } finally { out.flush(); }
        return null;
    }
    
    // [지출 내역 수정 API]
    private String editTransaction(HttpServletRequest request, HttpServletResponse response) throws Exception {
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();
        try {
            UserDTO loginUser = (UserDTO) request.getSession().getAttribute("loginUser");
            int actionUserNum = loginUser.getUserNum();
            
            int groupOwnerNum = Integer.parseInt(request.getParameter("groupOwnerNum"));
            String newCatName = request.getParameter("categoryName"); // 로그용 카테고리 이름
            
            GroupTransactionDTO dto = new GroupTransactionDTO();
            dto.setGtransNum(Integer.parseInt(request.getParameter("gtransNum")));
            dto.setGcategoryNum(Integer.parseInt(request.getParameter("gcategoryNum")));
            dto.setTransAmount(Long.parseLong(request.getParameter("transAmount")));
            dto.setTransDate(request.getParameter("transDate"));
            dto.setTransMemo(request.getParameter("transMemo"));
            
            GroupLedgerService.getInstance().editTransaction(dto, newCatName, actionUserNum, groupOwnerNum);
            
            out.print("{\"success\": true, \"message\": \"지출 내역이 성공적으로 수정되었습니다.\"}");
        } catch (Exception e) {
            out.print("{\"success\": false, \"message\": \"" + e.getMessage() + "\"}");
        } finally { out.flush(); }
        return null;
    }

    // [지출 내역 삭제 API]
    private String removeTransaction(HttpServletRequest request, HttpServletResponse response) throws Exception {
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();
        try {
            UserDTO loginUser = (UserDTO) request.getSession().getAttribute("loginUser");
            int actionUserNum = loginUser.getUserNum();
            
            int gtransNum = Integer.parseInt(request.getParameter("gtransNum"));
            int groupOwnerNum = Integer.parseInt(request.getParameter("groupOwnerNum"));
            
            GroupLedgerService.getInstance().removeTransaction(gtransNum, actionUserNum, groupOwnerNum);
            
            out.print("{\"success\": true, \"message\": \"지출 내역이 삭제되었습니다.\"}");
        } catch (Exception e) {
            out.print("{\"success\": false, \"message\": \"" + e.getMessage() + "\"}");
        } finally { out.flush(); }
        return null;
    }
    
    
    // [변경 이력 조회 API]
    private String getLogs(HttpServletRequest request, HttpServletResponse response) throws Exception {
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();
        
        try {
            int groupNum = Integer.parseInt(request.getParameter("groupNum"));
            List<ExpenseLogDTO> list = GroupLedgerService.getInstance().getExpenseLogs(groupNum);
            
            StringBuilder json = new StringBuilder("[");
            for (int i = 0; i < list.size(); i++) {
                ExpenseLogDTO dto = list.get(i);
                
                // 삭제('D') 시 null이 되는 필드들을 JSON 포맷에 맞게 안전하게 치환
                String afterAmtStr = (dto.getAfterAmount() == null) ? "null" : String.valueOf(dto.getAfterAmount());
                String afterCatStr = (dto.getAfterCategory() == null) ? "null" : "\"" + dto.getAfterCategory() + "\"";
                
                // 메모에 쌍따옴표나 줄바꿈이 있으면 JSON 파싱 에러가 나므로 이스케이프 처리
                String safeMemo = dto.getTransMemo() != null ? dto.getTransMemo().replace("\"", "\\\"").replace("\n", " ") : "";
                
                json.append("{");
                json.append("\"logNum\":").append(dto.getLogNum()).append(",");
                json.append("\"actionType\":\"").append(dto.getActionType()).append("\",");
                json.append("\"beforeAmount\":").append(dto.getBeforeAmount()).append(",");
                json.append("\"afterAmount\":").append(afterAmtStr).append(",");
                json.append("\"beforeCategory\":\"").append(dto.getBeforeCategory()).append("\",");
                json.append("\"afterCategory\":").append(afterCatStr).append(",");
                json.append("\"createdAtStr\":\"").append(dto.getCreatedAtStr()).append("\",");
                json.append("\"userNickname\":\"").append(dto.getUserNickname()).append("\",");
                json.append("\"transMemo\":\"").append(safeMemo).append("\"");
                json.append("}");
                
                if (i < list.size() - 1) json.append(",");
            }
            json.append("]");
            
            out.print(json.toString());
        } catch (Exception e) {
            e.printStackTrace();
            out.print("[]");
        } finally {
            out.flush();
        }
        return null;
    }
    
}