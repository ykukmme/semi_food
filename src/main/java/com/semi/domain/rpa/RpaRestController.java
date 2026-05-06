package com.semi.domain.rpa;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.semi.domain.rpa.response.RpaRunResponse;
import com.semi.domain.rpa.response.RpaStatusResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/rpa")
@RequiredArgsConstructor
@Slf4j
public class RpaRestController {

    private final RpaAsyncExecutionService rpaAsyncExecutionService;

    @GetMapping("/dashboard")
    public ResponseEntity<String> getDashboard() {
        // [ ]TODO: 실제 대시보드 데이터를 반환하도록 수정
        return ResponseEntity.ok("RPA Dashboard placeholder");
    }

    @GetMapping("/status")
    public ResponseEntity<RpaStatusResponse> getStatus() {
        return ResponseEntity.ok(rpaAsyncExecutionService.getLatestStatus());
    }

    @PostMapping("/run")
    public ResponseEntity<RpaRunResponse> triggerRpaJob(
        @RequestParam(defaultValue = "20") int size
    ) {
        if (!rpaAsyncExecutionService.tryStartSupplierProductParsing(size)) {
            return ResponseEntity.status(409)
                .body(new RpaRunResponse(
                    RpaStatus.RUNNING.name(),
                    "이미 실행 중인 RPA 작업이 있습니다.",
                    size
                ));
        }

        return ResponseEntity.accepted()
            .body(new RpaRunResponse(
                RpaStatus.RUNNING.name(),
                "RPA 공급자/상품 파싱 작업을 비동기로 시작했습니다.",
                size
            ));
    }
}
