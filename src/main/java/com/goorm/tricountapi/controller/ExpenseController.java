package com.goorm.tricountapi.controller;

import com.goorm.tricountapi.dto.ExpenseRequest;
import com.goorm.tricountapi.dto.ExpenseResult;
import com.goorm.tricountapi.service.ExpenseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
public class ExpenseController {
    private final ExpenseService expenseService;

    /**
     * 구현 6번
     * @param expenseRequest
     * @return
     */
    // 정산 추가
    @PostMapping("/expenses/add")
    public ResponseEntity<ExpenseResult> addExpenseToSettlement(
            @Valid @RequestBody ExpenseRequest expenseRequest
    ) {
        return new ResponseEntity<>(expenseService.addExpense(expenseRequest), HttpStatus.OK);
    }

}
