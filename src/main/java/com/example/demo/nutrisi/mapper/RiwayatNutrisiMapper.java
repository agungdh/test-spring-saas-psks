package com.example.demo.nutrisi.mapper;

import com.example.demo.nutrisi.dto.RiwayatNutrisiRequest;
import com.example.demo.nutrisi.dto.RiwayatNutrisiResponse;
import com.example.demo.nutrisi.entity.RiwayatAsupanNutrisi;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface RiwayatNutrisiMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "deletedBy", ignore = true)
    RiwayatAsupanNutrisi toEntity(RiwayatNutrisiRequest dto);

    RiwayatNutrisiResponse toResponse(RiwayatAsupanNutrisi entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "deletedBy", ignore = true)
    void updateEntity(@MappingTarget RiwayatAsupanNutrisi entity, RiwayatNutrisiRequest dto);
}
