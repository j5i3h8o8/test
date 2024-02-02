package com.goorm.tricountapi.dto;

import com.goorm.tricountapi.util.MemberContext;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ExpenseRequest {
    @NotNull
    private String name;

    @NotNull
    private Long settlementId;

    private Long payerMemberId = MemberContext.getCurrentMember().getId();

    @NotNull
    private BigDecimal amount;

    private LocalDateTime expenseDateTime;
}
