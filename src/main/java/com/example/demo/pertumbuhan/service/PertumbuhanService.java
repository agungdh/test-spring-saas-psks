package com.example.demo.pertumbuhan.service;

import com.example.demo.common.exception.BusinessException;
import com.example.demo.common.multitenancy.TenantContext;
import com.example.demo.common.multitenancy.TenantSchema;
import com.example.demo.common.security.AuthUser;
import com.example.demo.common.security.SecurityUtils;
import com.example.demo.common.web.PageResponse;
import com.example.demo.pertumbuhan.dto.RiwayatBBRequest;
import com.example.demo.pertumbuhan.dto.RiwayatBBResponse;
import com.example.demo.pertumbuhan.entity.RiwayatBeratBadan;
import com.example.demo.pertumbuhan.mapper.RiwayatBBMapper;
import com.example.demo.pertumbuhan.repository.RiwayatBeratBadanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PertumbuhanService {

    private final RiwayatBeratBadanRepository repository;
    private final RiwayatBBMapper mapper;

    @TenantSchema("pertumbuhan_berat_badan")
    @Transactional
    public RiwayatBBResponse create(RiwayatBBRequest request) {
        AuthUser currentUser = SecurityUtils.getCurrentUser();
        if (currentUser == null) {
            throw new BusinessException("Unauthorized", HttpStatus.UNAUTHORIZED);
        }

        RiwayatBeratBadan entity = mapper.toEntity(request);
        entity.setUserId(currentUser.isSuperAdmin() || currentUser.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_PETUGAS") || a.getAuthority().equals("ROLE_ADMIN"))
                ? request.userId() != null ? request.userId() : currentUser.getUserId()
                : currentUser.getUserId());
        entity.setCreatedBy(currentUser.getUserId());
        entity = repository.save(entity);
        return mapper.toResponse(entity);
    }

    @TenantSchema("pertumbuhan_berat_badan")
    @Transactional(readOnly = true)
    public PageResponse<RiwayatBBResponse> findAll(Pageable pageable) {
        AuthUser currentUser = SecurityUtils.getCurrentUser();
        Page<RiwayatBeratBadan> page;
        if (currentUser != null && currentUser.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN") || a.getAuthority().equals("ROLE_PETUGAS"))) {
            page = repository.findAllActive(pageable);
        } else {
            page = repository.findAllByUserIdAndDeletedAtIsNull(currentUser.getUserId(), pageable);
        }
        return PageResponse.from(page.map(mapper::toResponse));
    }

    @TenantSchema("pertumbuhan_berat_badan")
    @Transactional(readOnly = true)
    public RiwayatBBResponse findById(Long id) {
        RiwayatBeratBadan entity = repository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new BusinessException("Data not found", HttpStatus.NOT_FOUND));
        return mapper.toResponse(entity);
    }

    @TenantSchema("pertumbuhan_berat_badan")
    @Transactional
    public RiwayatBBResponse update(Long id, RiwayatBBRequest request) {
        AuthUser currentUser = SecurityUtils.getCurrentUser();
        RiwayatBeratBadan entity = repository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new BusinessException("Data not found", HttpStatus.NOT_FOUND));

        // Authorization: BUMIL can only edit own data
        if (currentUser.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_BUMIL"))
                && !entity.getUserId().equals(currentUser.getUserId())) {
            throw new BusinessException("Forbidden", HttpStatus.FORBIDDEN);
        }

        mapper.updateEntity(entity, request);
        entity.setUpdatedBy(currentUser.getUserId());
        entity = repository.save(entity);
        return mapper.toResponse(entity);
    }

    @TenantSchema("pertumbuhan_berat_badan")
    @Transactional
    public void delete(Long id) {
        AuthUser currentUser = SecurityUtils.getCurrentUser();
        RiwayatBeratBadan entity = repository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new BusinessException("Data not found", HttpStatus.NOT_FOUND));

        if (currentUser.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_BUMIL"))
                && !entity.getUserId().equals(currentUser.getUserId())) {
            throw new BusinessException("Forbidden", HttpStatus.FORBIDDEN);
        }

        entity.setDeletedAt(java.time.LocalDateTime.now());
        entity.setDeletedBy(currentUser.getUserId());
        repository.save(entity);
    }
}
