package com.example.demo.tenant.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import javax.sql.DataSource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flywaydb.core.Flyway;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class SchemaProvisionerService {

    private final DataSource dataSource;

    public void provisionSchemas(Long tenantId) {
        String pertumbuhanSchema = "tenant_" + tenantId + "_pertumbuhan_berat_badan";
        String nutrisiSchema = "tenant_" + tenantId + "_asupan_nutrisi";

        createSchemaIfNotExists(pertumbuhanSchema);
        createSchemaIfNotExists(nutrisiSchema);

        runFlywayMigration(pertumbuhanSchema, "classpath:db/migration/pertumbuhan");
        runFlywayMigration(nutrisiSchema, "classpath:db/migration/nutrisi");
    }

    private void createSchemaIfNotExists(String schemaName) {
        try (Connection connection = dataSource.getConnection();
             Statement stmt = connection.createStatement()) {
            stmt.execute("CREATE SCHEMA IF NOT EXISTS " + schemaName);
            log.info("Created schema: {}", schemaName);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to create schema: " + schemaName, e);
        }
    }

    private void runFlywayMigration(String schemaName, String location) {
        Flyway flyway = Flyway.configure()
                .dataSource(dataSource)
                .schemas(schemaName)
                .locations(location)
                .baselineOnMigrate(true)
                .load();
        flyway.migrate();
        log.info("Flyway migrated schema: {} from {}", schemaName, location);
    }
}
