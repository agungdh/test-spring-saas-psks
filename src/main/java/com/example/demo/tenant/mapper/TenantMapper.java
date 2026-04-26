package com.example.demo.tenant.mapper;

import com.example.demo.tenant.dto.TenantRequest;
import com.example.demo.tenant.dto.TenantResponse;
import com.example.demo.tenant.entity.Tenant;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface TenantMapper {

    Tenant toEntity(TenantRequest request);

    TenantResponse toResponse(Tenant entity);

    void updateEntity(@MappingTarget Tenant entity, TenantRequest request);
}
