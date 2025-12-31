package com.prerana.userservice.mapper;

import com.prerana.userservice.dto.CampaignResponseDto;
import com.prerana.userservice.dto.DonationOffersRequestDto;
import com.prerana.userservice.entity.CampaignEntity;
import com.prerana.userservice.entity.DonationOfferEntity;
import org.mapstruct.InheritConfiguration;
import org.mapstruct.Mapper;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring",
        uses = {UserEntityMapper.class})
public interface CampaignDtoMappper {

    //    @InheritConfiguration(name="toDto")
    CampaignResponseDto toDto(CampaignEntity entity);

    CampaignEntity toEntity(CampaignResponseDto dto);

    default Page<CampaignResponseDto> toDtoPage(Page<CampaignEntity> entities) {
        return entities.map(this::toDto);
    }

//    default List<DonationOffersRequestDto> toDtoList(List<DonationOfferEntity> entities)
//    {
//        return entities.stream().map(this::toDto).toList();
//    }
}
