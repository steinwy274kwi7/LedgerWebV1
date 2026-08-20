package kr.co.ledger.action;

import java.io.PrintWriter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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
            default -> throw new IllegalArgumentException("SettlementAction에 없는 기능: " + command);
        };
    }

    private String closePeriod(HttpServletRequest request, HttpServletResponse response) throws Exception {
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();
        
        try {
            UserDTO loginUser = (UserDTO) request.getSession().getAttribute("loginUser");
            int requestUserNum = loginUser.getUserNum();
            int groupNum = Integer.parseInt(request.getParameter("groupNum"));
            int groupOwnerNum = Integer.parseInt(request.getParameter("groupOwnerNum"));
            
            boolean isSuccess = SettlementService.getInstance().closeLedgerAndSettle(groupNum, requestUserNum, groupOwnerNum);
            
            if(isSuccess) {
                out.print("{\"success\": true, \"message\": \"장부가 성공적으로 마감되었으며, 정산 결과가 저장되었습니다. 잔액이 0원으로 리셋됩니다.\"}");
            }
        } catch (Exception e) {
            e.printStackTrace();
            out.print("{\"success\": false, \"message\": \"" + e.getMessage() + "\"}");
        } finally {
            out.flush();
        }
        return null;
    }
}