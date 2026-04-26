package com.example.demo.common.multitenancy;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class TenantSchemaAspect {

    @PersistenceContext
    private EntityManager entityManager;

    @Before("@annotation(tenantSchema)")
    public void setSchema(TenantSchema tenantSchema) {
        Long tenantId = TenantContext.getTenantId();
        if (tenantId == null) {
            return;
        }
        String module = tenantSchema.value();
        String schemaName = "tenant_" + tenantId + "_" + module;
        String sql = "SET LOCAL search_path TO " + schemaName + ", public";
        log.debug("Setting search_path: {}", sql);
        entityManager.createNativeQuery(sql).executeUpdate();
    }
}
