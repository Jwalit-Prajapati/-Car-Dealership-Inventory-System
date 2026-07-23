package com.jwalit.inventory_system.config;

import javax.sql.DataSource;
import org.flywaydb.core.Flyway;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Spring Boot 4.1.0 dropped {@code FlywayAutoConfiguration} from
 * spring-boot-autoconfigure (present through 3.5.x, absent in 4.1.0's jar),
 * so migrations no longer run automatically on startup. This bean replaces
 * that lost autoconfiguration, running via {@code initMethod} during eager
 * singleton instantiation — before Tomcat accepts any request that would
 * trigger a schema-dependent query.
 */
@Configuration
public class FlywayConfig {

    @Bean(initMethod = "migrate")
    public Flyway flyway(DataSource dataSource) {
        return Flyway.configure()
                .dataSource(dataSource)
                .locations("classpath:db/migration")
                .load();
    }
}
