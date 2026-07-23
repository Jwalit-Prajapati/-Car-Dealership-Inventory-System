package com.jwalit.inventory_system.config;

import jakarta.persistence.EntityManagerFactory;
import org.springframework.boot.autoconfigure.AbstractDependsOnBeanFactoryPostProcessor;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.stereotype.Component;

/**
 * Spring Boot 4.1.0 also dropped {@code FlywayJpaDependencyConfiguration}, which used
 * to make {@code entityManagerFactory} depend on Flyway migration so Hibernate never
 * touches the schema first. Without it, bean initialization order between our
 * {@link FlywayConfig#flyway} bean and the autoconfigured {@code entityManagerFactory}
 * is unspecified, so Hibernate can create tables before Flyway runs, which then fails
 * with "non-empty schema but no schema history table". This restores that ordering.
 */
@Component
class FlywayJpaDependencyPostProcessor extends AbstractDependsOnBeanFactoryPostProcessor {

    FlywayJpaDependencyPostProcessor() {
        super(EntityManagerFactory.class, LocalContainerEntityManagerFactoryBean.class, "flyway");
    }
}
