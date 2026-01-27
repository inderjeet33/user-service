package com.prerana.userservice.mapper;

import com.prerana.userservice.dto.DonationOffersRequestDto;
import com.prerana.userservice.dto.VolunteerOffersRequestDto;
import com.prerana.userservice.dto.VolunteerRequestDto;
import com.prerana.userservice.entity.DonationOfferEntity;
import com.prerana.userservice.entity.VolunteerRequestEntity;
import org.mapstruct.Mapper;
import org.springframework.data.domain.Page;

import java.util.List;

@Mapper(componentModel = "spring",
        uses = {UserEntityMapper.class})
public interface VolunteerOfferDtoMapper {

    VolunteerOffersRequestDto toDto(VolunteerRequestEntity entity);

    VolunteerRequestEntity toEntity(VolunteerOffersRequestDto dto);

    default Page<VolunteerOffersRequestDto> toDtoPage(Page<VolunteerRequestEntity> entities) {
        return entities.map(this::toDto);
    }

    default List<VolunteerOffersRequestDto> toDtoList(List<VolunteerRequestEntity> entities)
    {
        return entities.stream().map(this::toDto).toList();
    }
}
