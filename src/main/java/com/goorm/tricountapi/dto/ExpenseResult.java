package com.goorm.tricountapi.dto;

import com.goorm.tricountapi.model.Expense;
import com.goorm.tricountapi.model.Member;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class ExpenseResult {
    private Long settlementId;
    private Member payerMember;
    private BigDecimal amount;

    public ExpenseResult(Expense expense, Member member) {
        this.settlementId = expense.getSettlementId();
        this.payerMember = member;
        this.amount = expense.getAmount();
    }
}
