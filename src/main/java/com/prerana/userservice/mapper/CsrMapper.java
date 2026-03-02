package com.prerana.userservice.mapper;

import com.prerana.userservice.dto.CsrProfile;
import com.prerana.userservice.dto.NgoProfile;
import com.prerana.userservice.entity.CSRProfileEntity;
import com.prerana.userservice.entity.NGOProfileEntity;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.springframework.data.domain.Page;

import java.util.List;

@Mapper(componentModel = "spring",
        uses = { UserEntityMapper.class }
        ,unmappedTargetPolicy = ReportingPolicy.IGNORE,
        unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface CsrMapper {
    CsrProfile toDto(CSRProfileEntity entity);

    CSRProfileEntity toEntity(CsrProfile profile);
    List<CsrProfile> toDtoList(List<CSRProfileEntity> entities);

    // Default method for Page mapping
    default Page<CsrProfile> toDtoPage(Page<CSRProfileEntity> entities) {
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
