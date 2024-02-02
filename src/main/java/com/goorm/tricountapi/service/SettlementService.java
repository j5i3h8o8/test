package com.goorm.tricountapi.service;

import com.goorm.tricountapi.dto.BalanceResult;
import com.goorm.tricountapi.dto.ExpenseResult;
import com.goorm.tricountapi.model.Member;
import com.goorm.tricountapi.model.Settlement;
import com.goorm.tricountapi.repository.ExpenseRepository;
import com.goorm.tricountapi.repository.SettlementRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class SettlementService {
    private final SettlementRepository settlementRepository;
    private final ExpenseRepository expenseRepository;

    // create and join settlement
    @Transactional
    public Settlement createAndJoinSettlement(String settlementName, Member member) {
        Settlement settlement = settlementRepository.create(settlementName);
        settlementRepository.addParticipantToSettlement(settlement.getId(), member.getId());
        settlement.getParticipants().add(member);
        return settlement;
    }

    // join settlement
    @Transactional
    public void joinSettlement(Long settlementId, Long memberId) {
        // TODO 없는 아이디를 요청했을 때 예외 처리, 없는 정산 아이디를 요청했을 때 예외 처리
        settlementRepository.addParticipantToSettlement(settlementId, memberId);
    }

    // balance 계산
    public List<BalanceResult> getBalanceResult(Long settlementId){
        Map<Member, List<ExpenseResult>> collected = expenseRepository.findExpensesWithMemberBySettlementId(settlementId)
                .stream()
                .collect(groupingBy(ExpenseResult::getPayerMember));

        if(CollectionUtils.isEmpty(collected)) {
            throw new RuntimeException("정산 할 정보가 없습니다.");
        }

        Map<Member, BigDecimal> memberAmountSumMap = collected.entrySet().stream()
                .collect(toMap(Map.Entry::getKey, memberListEntry ->
                    memberListEntry.getValue().stream()
                            .map(ExpenseResult::getAmount)
                            .reduce(BigDecimal.ZERO, BigDecimal::add)
                ));

        //소수점 처리는 정책에 맞김 현재는 구현 편의를 위해 버린다.
        //실제 프로덕션에 나가기 위해서는 해당 부분에대한 정책을 꼭 잡아야한다.
        BigDecimal sumAmount = memberAmountSumMap.values().stream().reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal averageAmount = sumAmount.divide(BigDecimal.valueOf(memberAmountSumMap.size()), RoundingMode.DOWN);


        //받을돈 줄돈 계산하여 나누기
        Map<Member, BigDecimal> calcuratedAountMap = memberAmountSumMap.entrySet().stream()
                .collect(toMap(Map.Entry::getKey, memberBigDecimalEntry ->
                memberBigDecimalEntry.getValue().subtract(averageAmount)));

        List<Map.Entry<Member, BigDecimal>> receiver = calcuratedAountMap.entrySet().stream()
                .filter(memberBigDecimalEntry -> memberBigDecimalEntry.getValue().signum() > 0)
                .sorted((o1, o2) -> o2.getValue().subtract(o1.getValue()).signum())
                .collect(toList());

        List<Map.Entry<Member, BigDecimal>> sender = calcuratedAountMap.entrySet().stream()
                .filter(memberBigDecimalEntry -> memberBigDecimalEntry.getValue().signum() < 0)
                .sorted((o1, o2) -> o1.getValue().subtract(o2.getValue()).signum())
                .collect(toList());



        List<BalanceResult> balanceResults = new ArrayList<>();
        int receiverIndex = 0;
        int senderIndex = 0;
        while (receiverIndex < receiver.size() && senderIndex < sender.size()) {
            BigDecimal amountToTransfer = receiver.get(receiverIndex).getValue()
                    .add(sender.get(senderIndex).getValue());

            if(amountToTransfer.signum() < 0) {
                balanceResults.add(new BalanceResult(
                        sender.get(senderIndex).getKey().getId(),
                        sender.get(senderIndex).getKey().getName(),
                        receiver.get(receiverIndex).getValue().abs(),
                        receiver.get(receiverIndex).getKey().getId(),
                        receiver.get(receiverIndex).getKey().getName()
                ));
                receiver.get(receiverIndex).setValue(BigDecimal.ZERO);
                sender.get(senderIndex).setValue(amountToTransfer);
                receiverIndex++;
            } else if(amountToTransfer.signum() > 0) {
                balanceResults.add(new BalanceResult(
                        sender.get(senderIndex).getKey().getId(),
                        sender.get(senderIndex).getKey().getName(),
                        sender.get(senderIndex).getValue().abs(),
                        receiver.get(receiverIndex).getKey().getId(),
                        receiver.get(receiverIndex).getKey().getName()
                ));
                receiver.get(receiverIndex).setValue(amountToTransfer);
                sender.get(senderIndex).setValue(BigDecimal.ZERO);
                senderIndex++;
            } else {
                balanceResults.add(new BalanceResult(
                        sender.get(senderIndex).getKey().getId(),
                        sender.get(senderIndex).getKey().getName(),
                        sender.get(senderIndex).getValue().abs(),
                        receiver.get(receiverIndex).getKey().getId(),
                        receiver.get(receiverIndex).getKey().getName()
                ));
                receiver.get(receiverIndex).setValue(BigDecimal.ZERO);
                sender.get(senderIndex).setValue(BigDecimal.ZERO);
                receiverIndex++;
                senderIndex++;
            }
        }
        return balanceResults;
    }
}
