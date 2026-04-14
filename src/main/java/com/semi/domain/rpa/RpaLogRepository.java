package com.semi.domain.rpa;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RpaLogRepository extends JpaRepository<RpaLog, Long> {

    /** 가장 최근 로그 조회 (대시보드 가동 여부 판별용) */
    Optional<RpaLog> findTopByOrderByStartedAtDesc();
}
