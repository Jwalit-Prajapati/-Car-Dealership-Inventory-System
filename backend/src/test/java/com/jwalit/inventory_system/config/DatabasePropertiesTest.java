package com.jwalit.inventory_system.config;

import org.junit.jupiter.api.Test;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DatabasePropertiesTest {

    @Test
    void shouldHaveDatabaseAndFlywayConfigurationInApplicationYml() throws Exception {
        File yamlFile = new File("src/main/resources/application.yml");
        assertTrue(yamlFile.exists(), "application.yml should exist in src/main/resources");

        Yaml yaml = new Yaml();
        try (InputStream inputStream = new FileInputStream(yamlFile)) {
            Map<String, Object> data = yaml.load(inputStream);
            assertNotNull(data, "application.yml should not be empty");

            Map<String, Object> spring = (Map<String, Object>) data.get("spring");
            assertNotNull(spring, "spring configuration should exist");

            Map<String, Object> datasource = (Map<String, Object>) spring.get("datasource");
            assertNotNull(datasource, "spring.datasource configuration should exist");
            assertTrue(datasource.containsKey("url"), "spring.datasource.url should exist");
            assertTrue(datasource.containsKey("username"), "spring.datasource.username should exist");
            assertTrue(datasource.containsKey("password"), "spring.datasource.password should exist");

            Map<String, Object> flyway = (Map<String, Object>) spring.get("flyway");
            assertNotNull(flyway, "spring.flyway configuration should exist");
            assertTrue(flyway.containsKey("enabled"), "spring.flyway.enabled should exist");
            assertTrue(flyway.containsKey("locations"), "spring.flyway.locations should exist");
        }
    }

    @Test
    void shouldHaveInitialFlywayMigrationFile() {
        File migrationFile = new File("src/main/resources/db/migration/V1__init.sql");
        assertTrue(migrationFile.exists(), "V1__init.sql migration file should exist in src/main/resources/db/migration");
    }
}
