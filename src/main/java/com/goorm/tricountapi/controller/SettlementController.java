package com.goorm.tricountapi.controller;

import com.goorm.tricountapi.dto.BalanceResult;
import com.goorm.tricountapi.model.Settlement;
import com.goorm.tricountapi.service.SettlementService;
import com.goorm.tricountapi.util.MemberContext;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class SettlementController {
    private final SettlementService settlementService;

    /**
     * 구현 4번
     * @param settlementName
     * @return
     */
    @PostMapping("/settles/create")
    public ResponseEntity<Settlement> createSettlement(@RequestParam String settlementName) {
        return new ResponseEntity<>(settlementService.createAndJoinSettlement(settlementName, MemberContext.getCurrentMember()), HttpStatus.OK);
    }

    /**
     * 구현 5번
     * @param settlementId
     * @return
     */
    @PostMapping("/settles/{id}/join")
    public ResponseEntity<Void> joinSettlement(@PathVariable("id") Long settlementId) {
        settlementService.joinSettlement(settlementId, MemberContext.getCurrentMember().getId());
        return new ResponseEntity<>(HttpStatus.OK);
    }


    /**
     * 구현 7번
     * @param settlementId
     * @return
     */
    @GetMapping("/settles/{id}/balance")
    public ResponseEntity<List<BalanceResult>> getSettlementBalanceResult(@PathVariable("id") Long settlementId) {
        return new ResponseEntity<>(settlementService.getBalanceResult(settlementId), HttpStatus.OK);
    }

}
