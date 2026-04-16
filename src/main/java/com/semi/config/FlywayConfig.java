package com.semi.config;

import lombok.extern.slf4j.Slf4j;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.FlywayException;
import org.flywaydb.core.api.output.MigrateResult;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import javax.sql.DataSource;

/**
 * Spring Boot 4.x Flyway auto-configuration 우회 — 수동 Bean 등록
 * application.yml: spring.flyway.enabled=false 로 auto-config 비활성화 후 여기서 직접 실행
 */
@Slf4j
@Configuration
public class FlywayConfig {

    /**
     * JPA EntityManagerFactory보다 먼저 실행되도록 @DependsOn 제어.
     * DataSource만 주입받아 마이그레이션 후 Bean 반환 — JPA가 이 Bean에 의존하게 됨.
     */
    @Bean
    public Flyway flyway(DataSource dataSource) {
        log.info("[Flyway] 마이그레이션 시작 — classpath:db/migration");
        Flyway flyway = Flyway.configure()
                .dataSource(dataSource)
                .locations("classpath:db/migration")
                .load();
        try {
            MigrateResult result = flyway.migrate();
            log.info("[Flyway] 마이그레이션 완료 — 적용: {}건, 현재 버전: v{}",
                    result.migrationsExecuted, result.targetSchemaVersion);
        } catch (FlywayException e) {
            log.error("[Flyway] 마이그레이션 실패 — 서버를 중지합니다.", e);
            throw e;
        }
        return flyway;
    }
}
