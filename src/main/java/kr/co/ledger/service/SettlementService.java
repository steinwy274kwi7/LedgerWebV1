package kr.co.ledger.service;

import java.util.ArrayList;
import java.util.List;
import kr.co.ledger.dao.LedgerPeriodDAO;
import kr.co.ledger.dao.SettlementSnapshotDAO;
import kr.co.ledger.dto.MemberExpenseDTO;
import kr.co.ledger.dto.SettlementSnapshotDTO;

public class SettlementService {
    private static SettlementService instance = new SettlementService();
    private SettlementService() {}
    public static SettlementService getInstance() { return instance; }

    public boolean closeLedgerAndSettle(int groupNum, int requestUserNum, int groupOwnerNum) throws Exception {
        if (requestUserNum != groupOwnerNum) {
            throw new Exception("장부 마감 및 정산은 방장만 실행할 수 있습니다.");
        }

        int[] periodInfo = LedgerPeriodDAO.getInstance().getCurrentPeriodInfo(groupNum);
        int currentPeriodNum = periodInfo[0];
        int currentPeriodSeq = periodInfo[1];
        
        List<MemberExpenseDTO> expenses = SettlementSnapshotDAO.getInstance().getMemberTotalExpenses(groupNum, currentPeriodNum);
        
        if (expenses.isEmpty()) {
            throw new Exception("정산할 멤버가 존재하지 않습니다.");
        }

        long totalSpent = 0;
        for (MemberExpenseDTO e : expenses) totalSpent += e.getSpentAmount();
        
        long avgSpent = totalSpent / expenses.size(); 
        
        class Balance {
            int userNum; long amount;
            Balance(int u, long a) { this.userNum = u; this.amount = a; }
        }
        
        List<Balance> receivers = new ArrayList<>(); 
        List<Balance> payers = new ArrayList<>();    
        
        for (MemberExpenseDTO e : expenses) {
            long diff = e.getSpentAmount() - avgSpent;
            if (diff > 0) receivers.add(new Balance(e.getUserNum(), diff));
            else if (diff < 0) payers.add(new Balance(e.getUserNum(), diff));
        }

        List<SettlementSnapshotDTO> snapshots = new ArrayList<>();
        int p = 0, r = 0;
        
        while (p < payers.size() && r < receivers.size()) {
            Balance payer = payers.get(p);
            Balance receiver = receivers.get(r);
            
            long settleAmt = Math.min(Math.abs(payer.amount), receiver.amount);
            
            SettlementSnapshotDTO snap = new SettlementSnapshotDTO();
            snap.setPeriodNum(currentPeriodNum);
            snap.setPayerUserNum(payer.userNum);
            snap.setReceiverUserNum(receiver.userNum);
            snap.setSettleAmount(settleAmt);
            snapshots.add(snap);
            
            payer.amount += settleAmt;     
            receiver.amount -= settleAmt;  
            
            if (payer.amount == 0) p++;
            if (receiver.amount == 0) r++;
        }

        return SettlementSnapshotDAO.getInstance().executeSettlementTransaction(groupNum, currentPeriodNum, currentPeriodSeq + 1, snapshots);
    }
}