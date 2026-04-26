package com.example.demo.tenant.service;

import com.example.demo.common.exception.BusinessException;
import com.example.demo.common.multitenancy.TenantContext;
import com.example.demo.tenant.dto.TenantRequest;
import com.example.demo.tenant.dto.TenantResponse;
import com.example.demo.tenant.entity.Tenant;
import com.example.demo.tenant.mapper.TenantMapper;
import com.example.demo.tenant.repository.TenantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TenantService {

    private final TenantRepository tenantRepository;
    private final TenantMapper tenantMapper;
    private final SchemaProvisionerService schemaProvisionerService;

    @Transactional
    public TenantResponse create(TenantRequest request) {
        if (tenantRepository.findBySubdomainAndDeletedAtIsNull(request.subdomain()).isPresent()) {
            throw new BusinessException("Subdomain already exists");
        }

        Tenant tenant = tenantMapper.toEntity(request);
        tenant.setActive(true);
        tenant = tenantRepository.save(tenant);

        // Auto-provision schemas for this tenant
        schemaProvisionerService.provisionSchemas(tenant.getId());

        return tenantMapper.toResponse(tenant);
    }

    @Transactional(readOnly = true)
    public Page<TenantResponse> findAll(Pageable pageable) {
        return tenantRepository.findAllByDeletedAtIsNull(pageable)
                .map(tenantMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public TenantResponse findById(Long id) {
        Tenant tenant = tenantRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Tenant not found", HttpStatus.NOT_FOUND));
        return tenantMapper.toResponse(tenant);
    }

    @Transactional
    public TenantResponse update(Long id, TenantRequest request) {
        Tenant tenant = tenantRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Tenant not found", HttpStatus.NOT_FOUND));
        tenantMapper.updateEntity(tenant, request);
        tenant = tenantRepository.save(tenant);
        return tenantMapper.toResponse(tenant);
    }

    @Transactional
    public void delete(Long id) {
        Tenant tenant = tenantRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Tenant not found", HttpStatus.NOT_FOUND));
        tenant.setDeletedAt(java.time.LocalDateTime.now());
        tenantRepository.save(tenant);
    }
}
