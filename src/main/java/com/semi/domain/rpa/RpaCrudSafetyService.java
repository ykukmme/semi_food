package com.semi.domain.rpa;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RpaCrudSafetyService {

    private final RpaAsyncExecutionService rpaAsyncExecutionService;
    private final RpaLogRepository rpaLogRepository;

    public boolean isRpaRunning() {
        if (rpaAsyncExecutionService.isRunningInMemory()) {
            return true;
        }

        return rpaLogRepository.findTopByOrderByStartedAtDesc()
            .map(log -> log.getStatus() == RpaStatus.RUNNING)
            .orElse(false);
    }

    public void assertRpaNotRunning() {
        if (isRpaRunning()) {
            throw new IllegalStateException("RPA 실행 중에는 데이터 수정/삭제를 할 수 없습니다.");
        }
    }
}
