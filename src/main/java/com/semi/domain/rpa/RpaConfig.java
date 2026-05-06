package com.semi.domain.rpa;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "rpa_config")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RpaConfig {

    @Id
    private Long id;

    @Column(name = "is_auto_run_enabled", nullable = false)
    private boolean autoRunEnabled;

    @Column(name = "run_times", nullable = false)
    private String runTimes;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Builder
    public RpaConfig(Long id, boolean autoRunEnabled, String runTimes, LocalDateTime updatedAt) {
        this.id = id;
        this.autoRunEnabled = autoRunEnabled;
        this.runTimes = runTimes;
        this.updatedAt = updatedAt;
    }

    public void updateConfig(boolean autoRunEnabled, String runTimes) {
        this.autoRunEnabled = autoRunEnabled;
        this.runTimes = runTimes;
        this.updatedAt = LocalDateTime.now();
    }
}
