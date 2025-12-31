package com.prerana.userservice.mapper;

import com.prerana.userservice.dto.DonationOffersRequestDto;
import com.prerana.userservice.entity.DonationOfferEntity;
import org.mapstruct.InheritConfiguration;
import org.mapstruct.Mapper;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring",
        uses = {UserEntityMapper.class})
public interface DonationOfferDtoMapper {

//    @InheritConfiguration(name="toDto")
    DonationOffersRequestDto toDto(DonationOfferEntity entity);

    DonationOfferEntity toEntity(DonationOffersRequestDto dto);

    default Page<DonationOffersRequestDto> toDtoPage(Page<DonationOfferEntity> entities) {
        return entities.map(this::toDto);
    }

    default List<DonationOffersRequestDto> toDtoList(List<DonationOfferEntity> entities)
    {
        return entities.stream().map(this::toDto).toList();
    }
}
