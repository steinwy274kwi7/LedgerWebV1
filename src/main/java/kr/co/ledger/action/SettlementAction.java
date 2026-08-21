package kr.co.ledger.action;

import java.io.PrintWriter;
import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.co.ledger.dto.SettlementSnapshotDTO;
import kr.co.ledger.dto.UserDTO;
import kr.co.ledger.service.SettlementService;
import kr.co.ledger.util.UriUtil;

public class SettlementAction implements Action {

    @Override
    public String execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String command = UriUtil.getCommand(request);
        String methodName = command.substring(command.lastIndexOf("/") + 1, command.lastIndexOf(".")).trim();
        
        return switch (methodName) {
            case "closePeriod" -> closePeriod(request, response);
            case "preview"     -> getPreview(request, response);
            default -> throw new IllegalArgumentException("SettlementAction에 없는 기능: " + command);
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

    private String closePeriod(HttpServletRequest request, HttpServletResponse response) throws Exception {
        // 🌟 보안 강화: 로그인 유저 검증 추가
        UserDTO loginUser = (UserDTO) request.getSession().getAttribute("loginUser");
        if (loginUser == null) return sendAjaxResult(response, false, "로그인이 필요합니다.");
        
        try {
            int requestUserNum = loginUser.getUserNum();
            int groupNum = Integer.parseInt(request.getParameter("groupNum"));
            int groupOwnerNum = Integer.parseInt(request.getParameter("groupOwnerNum"));
            
            boolean isSuccess = SettlementService.getInstance().closeLedgerAndSettle(groupNum, requestUserNum, groupOwnerNum);
            
            if (isSuccess) {
                return sendAjaxResult(response, true, "장부가 성공적으로 마감되었으며, 정산 결과가 저장되었습니다. 잔액이 0원으로 리셋됩니다.");
            } else {
                return sendAjaxResult(response, false, "장부 마감 처리에 실패했습니다.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return sendAjaxResult(response, false, e.getMessage());
        }
    }
    
    // ==========================================================
    // 실시간 정산 미리보기 API
    // ==========================================================
    private String getPreview(HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            int groupNum = Integer.parseInt(request.getParameter("groupNum"));
            
            // Service 핵심 알고리즘 호출
            List<SettlementSnapshotDTO> list = SettlementService.getInstance().getSettlementPreview(groupNum);
            
            // JSON 조립
            StringBuilder json = new StringBuilder("[");
            for (int i = 0; i < list.size(); i++) {
                SettlementSnapshotDTO s = list.get(i);
                json.append(String.format(
                    "{\"payerNickname\":\"%s\", \"receiverNickname\":\"%s\", \"settleAmount\":%d}",
                    s.getPayerNickname(), s.getReceiverNickname(), s.getSettleAmount()
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
    
}