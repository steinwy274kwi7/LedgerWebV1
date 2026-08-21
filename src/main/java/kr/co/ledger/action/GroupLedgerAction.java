package kr.co.ledger.action;

import java.io.PrintWriter;
import java.time.YearMonth;
import java.util.List;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.co.ledger.dao.GroupDAO;
import kr.co.ledger.dao.GroupTransactionDAO;
import kr.co.ledger.dao.SettlementSnapshotDAO;
import kr.co.ledger.dto.ChartDTO;
import kr.co.ledger.dto.ExpenseLogDTO;
import kr.co.ledger.dto.GroupCategoryDTO;
import kr.co.ledger.dto.GroupDTO;
import kr.co.ledger.dto.GroupTransactionDTO;
import kr.co.ledger.dto.LedgerPeriodDTO;
import kr.co.ledger.dto.SettlementSnapshotDTO;
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
            case "statistics"           -> "/views/group_ledger/group_statistics.jsp"; 

            case "getCategoryChartData" -> getCategoryChartData(request, response);
            case "getTrendData"         -> getTrendData(request, response);
            case "ledger"               -> getGroupLedgerMain(request, response);
            case "getTransactions"      -> getMonthlyTransactions(request, response);
            case "insert"               -> insertTransaction(request, response);
            case "getCategoryList"      -> getCategoryList(request, response);
            case "addCategory"          -> addCategory(request, response);
            case "editCategory"         -> editCategory(request, response);
            case "removeCategory"       -> removeCategory(request, response);
            case "editTransaction"      -> editTransaction(request, response);
            case "removeTransaction"    -> removeTransaction(request, response);
            case "getLogs"              -> getLogs(request, response);
            case "getClosedPeriods"     -> getClosedPeriods(request, response);
            case "getArchiveDetails"    -> getArchiveDetails(request, response);
            default -> throw new IllegalArgumentException("GroupLedgerAction에 없는 기능: " + command);
        };
    }

    // ==========================================================
    // 🌟 [리팩토링] 공통 JSON 응답 헬퍼 메서드 (반복 코드 제거용)
    // ==========================================================
    private String sendJson(HttpServletResponse response, String jsonString) throws Exception {
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();
        out.print(jsonString != null ? jsonString : "[]");
        out.flush();
        return null;
    }

    private String sendAjaxResult(HttpServletResponse response, boolean success, String message) throws Exception {
        // 메시지 내 따옴표 등 특수문자로 인한 JSON 파싱 에러 방지
        String safeMessage = message != null ? message.replace("\"", "\\\"").replace("\n", " ") : "";
        String jsonString = "{\"success\": " + success + ", \"message\": \"" + safeMessage + "\"}";
        return sendJson(response, jsonString);
    }
    // ==========================================================

    // 그룹 카테고리 합계 (차트)
    private String getCategoryChartData(HttpServletRequest request, HttpServletResponse response) throws Exception {
        UserDTO loginUser = (UserDTO) request.getSession().getAttribute("loginUser");
        if (loginUser == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); 
            return null;
        }
        
        String targetMonth = request.getParameter("month");
        if (targetMonth == null || targetMonth.isEmpty()) targetMonth = YearMonth.now().toString(); 

        List<ChartDTO> chartList = GroupLedgerService.getInstance().getAllMyGroupCategorySumForChart(loginUser.getUserNum(), targetMonth);

        StringBuilder json = new StringBuilder("[");
        for (int i = 0; i < chartList.size(); i++) {
            ChartDTO dto = chartList.get(i);
            json.append(String.format("{\"categoryName\":\"%s\", \"totalAmount\":%d}", dto.getCategoryName(), dto.getTotalAmount()));
            if (i < chartList.size() - 1) json.append(",");
        }
        json.append("]");
        
        return sendJson(response, json.toString());
    }
    
    // 그룹 6개월 추이 (차트)
    private String getTrendData(HttpServletRequest request, HttpServletResponse response) throws Exception {
        UserDTO loginUser = (UserDTO) request.getSession().getAttribute("loginUser");
        if (loginUser == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); 
            return null;
        }

        String targetMonth = request.getParameter("month");
        if(targetMonth == null || targetMonth.isEmpty()) targetMonth = YearMonth.now().toString();

        List<TrendDTO> trendList = GroupLedgerService.getInstance().getRecent6MonthsGroupTrend(loginUser.getUserNum(), targetMonth);

        StringBuilder json = new StringBuilder("[");
        for (int i = 0; i < trendList.size(); i++) {
            TrendDTO dto = trendList.get(i);
            json.append(String.format("{\"month\":\"%s\", \"totalExpense\":%d}", dto.getMonth(), dto.getTotalExpense()));
            if (i < trendList.size() - 1) json.append(",");
        }
        json.append("]");
        
        return sendJson(response, json.toString());
    }
    
    // 🌟 [리팩토링] 그룹 가계부 메인 화면 이동 (alert.jsp 적용)
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
        boolean isMember = GroupDAO.getInstance().isUserAlreadyInGroupOrInvited(groupNum, loginUser.getUserNum(), "checkAlreadyMember");
        
        // 🌟 더 이상 PrintWriter를 쓰지 않고 공통 alert.jsp로 넘깁니다!
        if (!isMember && "N".equals(group.getGroupOpenYn())) {
            request.setAttribute("msg", "비공개 그룹이거나 접근 권한이 없습니다.");
            request.setAttribute("url", "/group/list.do");
            return "/views/common/alert.jsp"; // ViewResolver 경로에 맞게 (필요시 /WEB-INF/ 추가)
        }
        
        request.setAttribute("isMember", isMember);
        request.setAttribute("group", group);
        
        return "/views/group_ledger/group_main.jsp";
    }
    
    // 그룹 달력 리스트 뷰 보기 (AJAX)
    private String getMonthlyTransactions(HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            int groupNum = Integer.parseInt(request.getParameter("groupNum"));
            String yearMonth = request.getParameter("yearMonth"); 
            
            List<GroupTransactionDTO> list = GroupLedgerService.getInstance().getMonthlyTransactions(groupNum, yearMonth);
            
            StringBuilder json = new StringBuilder("[");
            for (int i = 0; i < list.size(); i++) {
                GroupTransactionDTO dto = list.get(i);
                String safeMemo = dto.getTransMemo() != null ? dto.getTransMemo().replace("\\", "\\\\").replace("\"", "\\\"").replaceAll("[\\r\\n\\t]", " ") : "";
                
                json.append(String.format(
                    "{\"gtransNum\":%d, \"userNum\":%d, \"userNickname\":\"%s\", \"categoryName\":\"%s\", \"transAmount\":%d, \"transDate\":\"%s\", \"transMemo\":\"%s\", \"periodStatus\":\"%s\"}",
                    dto.getGtransNum(), dto.getUserNum(), dto.getUserNickname(), 
                    dto.getCategoryName(), dto.getTransAmount(), dto.getTransDate(), safeMemo, dto.getPeriodStatus()
                ));
                if (i < list.size() - 1) json.append(",");
            }
            json.append("]");
            return sendJson(response, json.toString());
        } catch (Exception e) {
            return sendJson(response, "[]");
        }
    }
    
    // 공동 지출 내역 등록 (AJAX)
    private String insertTransaction(HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            UserDTO loginUser = (UserDTO) request.getSession().getAttribute("loginUser");
            if (loginUser == null) throw new Exception("로그인이 필요합니다.");

            GroupTransactionDTO dto = new GroupTransactionDTO();
            dto.setGroupNum(Integer.parseInt(request.getParameter("groupNum")));
            dto.setGcategoryNum(Integer.parseInt(request.getParameter("gcategoryNum")));
            dto.setTransAmount(Long.parseLong(request.getParameter("transAmount")));
            dto.setTransDate(request.getParameter("transDate"));
            dto.setTransMemo(request.getParameter("transMemo"));
            dto.setUserNum(loginUser.getUserNum());

            boolean isSuccess = GroupLedgerService.getInstance().insertTransaction(dto);
            return sendAjaxResult(response, isSuccess, isSuccess ? "지출 내역이 등록되었습니다." : "등록에 실패했습니다.");
        } catch (Exception e) {
            return sendAjaxResult(response, false, e.getMessage());
        }
    }
    
    // [카테고리 목록 불러오기]
    private String getCategoryList(HttpServletRequest request, HttpServletResponse response) throws Exception {
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
            return sendJson(response, json.toString());
        } catch (Exception e) { 
            return sendJson(response, "[]"); 
        } 
    }

    // [카테고리 등록]
    private String addCategory(HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            int groupNum = Integer.parseInt(request.getParameter("groupNum"));
            String name = request.getParameter("categoryName");
            GroupLedgerService.getInstance().addCategory(groupNum, name);
            return sendAjaxResult(response, true, "카테고리가 등록되었습니다.");
        } catch (Exception e) {
            return sendAjaxResult(response, false, e.getMessage());
        }
    }

    // [카테고리 수정]
    private String editCategory(HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            int groupNum = Integer.parseInt(request.getParameter("groupNum"));
            int categoryNum = Integer.parseInt(request.getParameter("categoryNum"));
            String name = request.getParameter("categoryName");
            GroupLedgerService.getInstance().editCategory(groupNum, categoryNum, name);
            return sendAjaxResult(response, true, "카테고리가 수정되었습니다.");
        } catch (Exception e) {
            return sendAjaxResult(response, false, e.getMessage());
        }
    }

    // [카테고리 삭제]
    private String removeCategory(HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            UserDTO loginUser = (UserDTO) request.getSession().getAttribute("loginUser");
            int groupNum = Integer.parseInt(request.getParameter("groupNum"));
            int categoryNum = Integer.parseInt(request.getParameter("categoryNum"));
            String categoryName = request.getParameter("categoryName");
            GroupLedgerService.getInstance().removeCategory(groupNum, categoryNum, categoryName, loginUser.getUserNum());
            return sendAjaxResult(response, true, "삭제 완료! 관련 지출 내역은 '미분류'로 자동 이관되었습니다.");
        } catch (Exception e) {
            return sendAjaxResult(response, false, e.getMessage());
        }
    }
    
    // [지출 내역 수정 API]
    private String editTransaction(HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            UserDTO loginUser = (UserDTO) request.getSession().getAttribute("loginUser");
            int groupOwnerNum = Integer.parseInt(request.getParameter("groupOwnerNum"));
            String newCatName = request.getParameter("categoryName");
            
            GroupTransactionDTO dto = new GroupTransactionDTO();
            dto.setGtransNum(Integer.parseInt(request.getParameter("gtransNum")));
            dto.setGcategoryNum(Integer.parseInt(request.getParameter("gcategoryNum")));
            dto.setTransAmount(Long.parseLong(request.getParameter("transAmount")));
            dto.setTransDate(request.getParameter("transDate"));
            dto.setTransMemo(request.getParameter("transMemo"));
            
            GroupLedgerService.getInstance().editTransaction(dto, newCatName, loginUser.getUserNum(), groupOwnerNum);
            return sendAjaxResult(response, true, "지출 내역이 성공적으로 수정되었습니다.");
        } catch (Exception e) {
            return sendAjaxResult(response, false, e.getMessage());
        }
    }

    // [지출 내역 삭제 API]
    private String removeTransaction(HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            UserDTO loginUser = (UserDTO) request.getSession().getAttribute("loginUser");
            int gtransNum = Integer.parseInt(request.getParameter("gtransNum"));
            int groupOwnerNum = Integer.parseInt(request.getParameter("groupOwnerNum"));
            
            GroupLedgerService.getInstance().removeTransaction(gtransNum, loginUser.getUserNum(), groupOwnerNum);
            return sendAjaxResult(response, true, "지출 내역이 삭제되었습니다.");
        } catch (Exception e) {
            return sendAjaxResult(response, false, e.getMessage());
        }
    }
    
    // [변경 이력 조회 API]
    private String getLogs(HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            int groupNum = Integer.parseInt(request.getParameter("groupNum"));
            List<ExpenseLogDTO> list = GroupLedgerService.getInstance().getExpenseLogs(groupNum);
            
            StringBuilder json = new StringBuilder("[");
            for (int i = 0; i < list.size(); i++) {
                ExpenseLogDTO dto = list.get(i);
                String afterAmtStr = (dto.getAfterAmount() == null) ? "null" : String.valueOf(dto.getAfterAmount());
                String afterCatStr = (dto.getAfterCategory() == null) ? "null" : "\"" + dto.getAfterCategory() + "\"";
                String safeMemo = dto.getTransMemo() != null ? dto.getTransMemo().replace("\\", "\\\\").replace("\"", "\\\"").replaceAll("[\\r\\n\\t]", " ") : "";
                
                json.append(String.format(
                    "{\"logNum\":%d, \"actionType\":\"%s\", \"beforeAmount\":%d, \"afterAmount\":%s, \"beforeCategory\":\"%s\", \"afterCategory\":%s, \"createdAtStr\":\"%s\", \"userNickname\":\"%s\", \"transMemo\":\"%s\"}",
                    dto.getLogNum(), dto.getActionType(), dto.getBeforeAmount(), afterAmtStr, dto.getBeforeCategory(), afterCatStr, dto.getCreatedAtStr(), dto.getUserNickname(), safeMemo
                ));
                if (i < list.size() - 1) json.append(",");
            }
            json.append("]");
            return sendJson(response, json.toString());
        } catch (Exception e) {
            e.printStackTrace();
            return sendJson(response, "[]");
        }
    }
    
    // [과거 정산 보관함] 마감된 회차 목록 가져오기 API
    private String getClosedPeriods(HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            int groupNum = Integer.parseInt(request.getParameter("groupNum"));
            List<LedgerPeriodDTO> list = GroupLedgerService.getInstance().getClosedPeriods(groupNum);
            
            StringBuilder json = new StringBuilder("[");
            for (int i = 0; i < list.size(); i++) {
                LedgerPeriodDTO dto = list.get(i);
                json.append(String.format(
                    "{\"periodNum\":%d, \"periodSeq\":%d, \"startDate\":\"%s\", \"endDate\":\"%s\"}",
                    dto.getPeriodNum(), dto.getPeriodSeq(), dto.getPeriodStartDate(), dto.getPeriodEndDate()
                ));
                if (i < list.size() - 1) json.append(",");
            }
            json.append("]");
            return sendJson(response, json.toString());
        } catch (Exception e) {
            e.printStackTrace();
            return sendJson(response, "[]");
        }
    }

    // [과거 정산 보관함] 특정 회차의 상세 내역(스냅샷+지출) 병합 반환 API
    private String getArchiveDetails(HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            int periodNum = Integer.parseInt(request.getParameter("periodNum"));
            List<GroupTransactionDTO> transList = GroupTransactionDAO.getInstance().getTransactionsByPeriod(periodNum);
            List<SettlementSnapshotDTO> snapList = SettlementSnapshotDAO.getInstance().getSnapshotsByPeriod(periodNum);
            
            StringBuilder json = new StringBuilder("{"); 
            
            // --- [1] snapshots 배열 조립 ---
            json.append("\"snapshots\":[");
            for (int i = 0; i < snapList.size(); i++) {
                SettlementSnapshotDTO s = snapList.get(i);
                json.append(String.format(
                    "{\"payerNickname\":\"%s\", \"receiverNickname\":\"%s\", \"settleAmount\":%d}",
                    s.getPayerNickname(), s.getReceiverNickname(), s.getSettleAmount()
                ));
                if (i < snapList.size() - 1) json.append(",");
            }
            json.append("],"); 
            
            // --- [2] transactions 배열 조립 ---
            json.append("\"transactions\":[");
            for (int i = 0; i < transList.size(); i++) {
                GroupTransactionDTO t = transList.get(i);
                String safeMemo = t.getTransMemo() != null ? t.getTransMemo().replace("\"", "\\\"").replace("\n", " ") : "";
                json.append(String.format(
                    "{\"categoryName\":\"%s\", \"transMemo\":\"%s\", \"transDate\":\"%s\", \"userNickname\":\"%s\", \"transAmount\":%d}",
                    t.getCategoryName(), safeMemo, t.getTransDate(), t.getUserNickname(), t.getTransAmount()
                ));
                if (i < transList.size() - 1) json.append(",");
            }
            json.append("]"); 
            json.append("}"); 
            
            return sendJson(response, json.toString());
        } catch (Exception e) {
            e.printStackTrace();
            return sendJson(response, "{\"snapshots\":[], \"transactions\":[]}");
        }
    }
}