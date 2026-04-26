package com.example.demo.nutrisi.service;

import com.example.demo.common.exception.BusinessException;
import com.example.demo.common.multitenancy.TenantSchema;
import com.example.demo.common.security.AuthUser;
import com.example.demo.common.security.SecurityUtils;
import com.example.demo.common.web.PageResponse;
import com.example.demo.nutrisi.dto.RiwayatNutrisiRequest;
import com.example.demo.nutrisi.dto.RiwayatNutrisiResponse;
import com.example.demo.nutrisi.entity.RiwayatAsupanNutrisi;
import com.example.demo.nutrisi.mapper.RiwayatNutrisiMapper;
import com.example.demo.nutrisi.repository.RiwayatAsupanNutrisiRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NutrisiService {

    private final RiwayatAsupanNutrisiRepository repository;
    private final RiwayatNutrisiMapper mapper;

    @TenantSchema("asupan_nutrisi")
    @Transactional
    public RiwayatNutrisiResponse create(RiwayatNutrisiRequest request) {
        AuthUser currentUser = SecurityUtils.getCurrentUser();
        if (currentUser == null) {
            throw new BusinessException("Unauthorized", HttpStatus.UNAUTHORIZED);
        }

        RiwayatAsupanNutrisi entity = mapper.toEntity(request);
        entity.setUserId(currentUser.isSuperAdmin() || currentUser.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_PETUGAS") || a.getAuthority().equals("ROLE_ADMIN"))
                ? request.userId() != null ? request.userId() : currentUser.getUserId()
                : currentUser.getUserId());
        entity.setCreatedBy(currentUser.getUserId());
        entity = repository.save(entity);
        return mapper.toResponse(entity);
    }

    @TenantSchema("asupan_nutrisi")
    @Transactional(readOnly = true)
    public PageResponse<RiwayatNutrisiResponse> findAll(Pageable pageable) {
        AuthUser currentUser = SecurityUtils.getCurrentUser();
        Page<RiwayatAsupanNutrisi> page;
        if (currentUser != null && currentUser.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN") || a.getAuthority().equals("ROLE_PETUGAS"))) {
            page = repository.findAllActive(pageable);
        } else {
            page = repository.findAllByUserIdAndDeletedAtIsNull(currentUser.getUserId(), pageable);
        }
        return PageResponse.from(page.map(mapper::toResponse));
    }

    @TenantSchema("asupan_nutrisi")
    @Transactional(readOnly = true)
    public RiwayatNutrisiResponse findById(Long id) {
        RiwayatAsupanNutrisi entity = repository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new BusinessException("Data not found", HttpStatus.NOT_FOUND));
        return mapper.toResponse(entity);
    }

    @TenantSchema("asupan_nutrisi")
    @Transactional
    public RiwayatNutrisiResponse update(Long id, RiwayatNutrisiRequest request) {
        AuthUser currentUser = SecurityUtils.getCurrentUser();
        RiwayatAsupanNutrisi entity = repository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new BusinessException("Data not found", HttpStatus.NOT_FOUND));

        if (currentUser.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_BUMIL"))
                && !entity.getUserId().equals(currentUser.getUserId())) {
            throw new BusinessException("Forbidden", HttpStatus.FORBIDDEN);
        }

        mapper.updateEntity(entity, request);
        entity.setUpdatedBy(currentUser.getUserId());
        entity = repository.save(entity);
        return mapper.toResponse(entity);
    }

    @TenantSchema("asupan_nutrisi")
    @Transactional
    public void delete(Long id) {
        AuthUser currentUser = SecurityUtils.getCurrentUser();
        RiwayatAsupanNutrisi entity = repository.findByIdAndDeletedAtIsNull(id)
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
