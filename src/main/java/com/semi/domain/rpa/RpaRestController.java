package com.semi.domain.rpa;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/rpa")
@Slf4j
public class RpaRestController {

    @GetMapping("/dashboard")
    public ResponseEntity<String> getDashboard() {
        // [ ]TODO: 실제 대시보드 데이터를 반환하도록 수정
        return ResponseEntity.ok("RPA Dashboard placeholder");
    }

    @GetMapping("/status")
    public ResponseEntity<String> getStatus() {
        // [ ]TODO: RPA 프로세스 상태 정보를 조회하도록 구현
        return ResponseEntity.ok("RPA status placeholder");
    }

    @PostMapping("/run")
    public ResponseEntity<String> triggerRpaJob() {
        // [ ]TODO: RPA 작업 실행 로직을 연결
        return ResponseEntity.ok("RPA job triggered placeholder");
    }
}
