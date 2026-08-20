package kr.co.ledger.service;

import java.util.ArrayList;
import java.util.List;
import kr.co.ledger.dao.LedgerPeriodDAO;
import kr.co.ledger.dao.SettlementSnapshotDAO;
import kr.co.ledger.dto.MemberBalanceDTO;
import kr.co.ledger.dto.MemberExpenseDTO;
import kr.co.ledger.dto.SettlementSnapshotDTO;

public class SettlementService {
    private static SettlementService instance = new SettlementService();
    private SettlementService() {}
    public static SettlementService getInstance() { return instance; }

    // ==============================================================
    // 1. 기존 장부 마감 (closeLedgerAndSettle) - 그대로 유지 + 내부 알고리즘만 호출
    // ==============================================================
    public boolean closeLedgerAndSettle(int groupNum, int requestUserNum, int groupOwnerNum) throws Exception {
        if (requestUserNum != groupOwnerNum) {
            throw new Exception("장부 마감 및 정산은 방장만 실행할 수 있습니다.");
        }

        int[] periodInfo = LedgerPeriodDAO.getInstance().getCurrentPeriodInfo(groupNum);
        int currentPeriodNum = periodInfo[0];
        int currentPeriodSeq = periodInfo[1];
        
        // 🌟 핵심: 미리보기와 동일한 정산 계산(공통 로직)을 수행하여 결과 리스트만 받아옴
        List<SettlementSnapshotDTO> snapshots = calculateSettlementLogic(groupNum, currentPeriodNum);

        // DB에 스냅샷 저장 및 회차 닫고 새 회차 여는 트랜잭션 호출
        return SettlementSnapshotDAO.getInstance().executeSettlementTransaction(groupNum, currentPeriodNum, currentPeriodSeq + 1, snapshots);
    }
    
    // ==============================================================
    // 2. 신규 실시간 정산 미리보기 (getSettlementPreview)
    // ==============================================================
    public List<SettlementSnapshotDTO> getSettlementPreview(int groupNum) throws Exception {
        // 현재 열려있는 회차 번호 조회
        int currentPeriodNum = SettlementSnapshotDAO.getInstance().getOpenPeriodNum(groupNum);
        if (currentPeriodNum == 0) return new ArrayList<>(); 
        
        // 🌟 핵심: 장부 마감과 완벽하게 동일한 계산 로직 호출
        return calculateSettlementLogic(groupNum, currentPeriodNum);
    }

    // ==============================================================
    // 3. 공통 정산 알고리즘 (미리보기 & 장부마감 공용) - 오차독박 + 그리디
    // ==============================================================
    private List<SettlementSnapshotDTO> calculateSettlementLogic(int groupNum, int periodNum) throws Exception {
        List<MemberExpenseDTO> expenses = SettlementSnapshotDAO.getInstance().getMemberTotalExpenses(groupNum, periodNum);
        
        long totalSpent = 0;
        for (MemberExpenseDTO e : expenses) totalSpent += e.getSpentAmount();
        
        int memberCount = expenses.size();
        if (memberCount == 0 || totalSpent == 0) return new ArrayList<>(); 
        
        // 1/N 평균 및 나머지(오차) 계산
        long avgSpent = totalSpent / memberCount; 
        long remainder = totalSpent % memberCount; 
        
        // 채무자(-)와 채권자(+) 분리
        List<MemberBalanceDTO> receivers = new ArrayList<>(); // 받을 사람(+)
        List<MemberBalanceDTO> payers = new ArrayList<>();    // 낼 사람(-)
        
        for (MemberExpenseDTO e : expenses) {
            long diff = e.getSpentAmount() - avgSpent;
            // 🌟 우리가 만든 DTO에 닉네임과 UserNum 모두 담아둠
            MemberBalanceDTO balanceDTO = new MemberBalanceDTO(e.getNickname(), diff);
            balanceDTO.setUserNum(e.getUserNum()); 
            
            if (diff > 0) receivers.add(balanceDTO);
            else if (diff < 0) payers.add(balanceDTO);
        }

        // 🌟 오차(나머지) 독박 처리: 받을 돈이 가장 많은 사람(최대 채권자)에게서 잔돈을 차감
        if (remainder > 0 && !receivers.isEmpty()) {
            receivers.sort((a, b) -> Long.compare(b.getBalance(), a.getBalance()));
            MemberBalanceDTO maxReceiver = receivers.get(0);
            maxReceiver.setBalance(maxReceiver.getBalance() - remainder);
        }

        // 🌟 그리디 알고리즘 매칭 (기존 로직과 동일하게 작동)
        List<SettlementSnapshotDTO> snapshots = new ArrayList<>();
        int p = 0, r = 0;
        
        while (p < payers.size() && r < receivers.size()) {
            MemberBalanceDTO payer = payers.get(p);
            MemberBalanceDTO receiver = receivers.get(r);
            
            // 낼 돈(payer)은 음수이므로 Math.abs 절댓값 처리
            long settleAmt = Math.min(Math.abs(payer.getBalance()), receiver.getBalance());
            
            if (settleAmt == 0) {
                if (payer.getBalance() == 0) p++;
                if (receiver.getBalance() == 0) r++;
                continue;
            }
            
            SettlementSnapshotDTO snap = new SettlementSnapshotDTO();
            snap.setPeriodNum(periodNum); 
            
            // 🌟 장부 마감(DB 저장)용 데이터 세팅
            snap.setPayerUserNum(payer.getUserNum());
            snap.setReceiverUserNum(receiver.getUserNum());
            
            // 🌟 실시간 미리보기(UI 출력)용 데이터 세팅
            snap.setPayerNickname(payer.getNickname());
            snap.setReceiverNickname(receiver.getNickname());
            
            snap.setSettleAmount(settleAmt);
            snapshots.add(snap);
            
            // 잔액 업데이트
            payer.setBalance(payer.getBalance() + settleAmt);     
            receiver.setBalance(receiver.getBalance() - settleAmt);  
            
            if (payer.getBalance() == 0) p++;
            if (receiver.getBalance() == 0) r++;
        }
        
        return snapshots;
    }
}