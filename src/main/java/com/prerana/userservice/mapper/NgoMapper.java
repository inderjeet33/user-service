package com.prerana.userservice.mapper;

import com.prerana.userservice.dto.NgoProfile;
import com.prerana.userservice.entity.NGOProfileEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.springframework.data.domain.Page;

import java.util.List;
@Mapper(componentModel = "spring",
        uses = { UserEntityMapper.class }
        ,unmappedTargetPolicy = ReportingPolicy.IGNORE,
        unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface NgoMapper {
    NgoProfile toDto(NGOProfileEntity entity);

    NGOProfileEntity toEntity(NgoProfile profile);
    List<NgoProfile> toDtoList(List<NGOProfileEntity> entities);

    // Default method for Page mapping
    default Page<NgoProfile> toDtoPage(Page<NGOProfileEntity> entities) {
        return entities.map(this::toDto);
    }
    default List<String> splitCategories(String categories) {
        if (categories == null || categories.isEmpty()) return List.of();
        return List.of(categories.split(","));
    }

    default String joinCategories(List<String> categories) {
        if (categories == null) return null;
        return String.join(",", categories);
    }

}
