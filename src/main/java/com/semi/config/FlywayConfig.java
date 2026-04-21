package com.semi.config;

import lombok.extern.slf4j.Slf4j;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.FlywayException;
import org.flywaydb.core.api.output.MigrateResult;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Slf4j
@Configuration
public class FlywayConfig {

    @Bean
    public Flyway flyway(DataSource dataSource) {
        log.info("[Flyway] migration start - classpath:db/migration");

        Flyway flyway = Flyway.configure()
                .dataSource(dataSource)
                .locations("classpath:db/migration")
                .cleanDisabled(true)
                .load();

        try {
            MigrateResult result = flyway.migrate();
            log.info("[Flyway] migration complete - applied: {}, current version: v{}",
                    result.migrationsExecuted, result.targetSchemaVersion);
        } catch (FlywayException e) {
            log.error("[Flyway] migration failed - stopping server", e);
            throw e;
        }

        return flyway;
    }
}
