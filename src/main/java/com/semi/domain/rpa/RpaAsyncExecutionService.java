package com.semi.domain.rpa;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.semi.domain.rpa.parser.RpaSupplierProductParsingService;
import com.semi.domain.rpa.response.RpaStatusResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class RpaAsyncExecutionService {

    private final AtomicBoolean running = new AtomicBoolean(false);
    private final RpaLogRepository rpaLogRepository;
    private final RpaSupplierProductParsingService rpaSupplierProductParsingService;

    public boolean tryStartSupplierProductParsing(int requestedSize) {
        if (!running.compareAndSet(false, true)) {
            return false;
        }

        boolean databaseAllowsStart = rpaLogRepository.findTopByOrderByStartedAtDesc()
            .map(log -> log.getStatus() != RpaStatus.RUNNING)
            .orElse(true);

        if (!databaseAllowsStart) {
            running.set(false);
            return false;
        }

        runSupplierProductParsingAsync(requestedSize);
        return true;
    }

    public boolean isRunningInMemory() {
        return running.get();
    }

    @Async
    public void runSupplierProductParsingAsync(int requestedSize) {
        try {
            rpaSupplierProductParsingService.parseTodaySupplierAndProducts(requestedSize);
        } catch (RuntimeException exception) {
            log.error("비동기 RPA 실행 중 예외가 발생했습니다.", exception);
        } finally {
            running.set(false);
        }
    }

    public RpaStatusResponse getLatestStatus() {
        Optional<RpaLog> latestLog = rpaLogRepository.findTopByOrderByStartedAtDesc();
        return latestLog
            .map(log -> new RpaStatusResponse(
                log.getId(),
                log.getStatus().name(),
                log.getStartedAt(),
                log.getEndedAt(),
                log.getKeywordCount(),
                log.getProductCount(),
                log.getMessage()
            ))
            .orElseGet(() -> new RpaStatusResponse(
                null,
                "NOT_STARTED",
                null,
                null,
                null,
                null,
                "RPA 실행 이력이 없습니다."
            ));
    }
}
