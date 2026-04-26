package com.example.demo.pertumbuhan.mapper;

import com.example.demo.pertumbuhan.dto.RiwayatBBRequest;
import com.example.demo.pertumbuhan.dto.RiwayatBBResponse;
import com.example.demo.pertumbuhan.entity.RiwayatBeratBadan;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface RiwayatBBMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "deletedBy", ignore = true)
    RiwayatBeratBadan toEntity(RiwayatBBRequest dto);

    RiwayatBBResponse toResponse(RiwayatBeratBadan entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "deletedBy", ignore = true)
    void updateEntity(@MappingTarget RiwayatBeratBadan entity, RiwayatBBRequest dto);
}
