package com.prerana.userservice.mapper;

import com.prerana.userservice.dto.DonationOffersRequestDto;
import com.prerana.userservice.dto.ModeratorAssignmentDto;
import com.prerana.userservice.dto.ModeratorAssignmentRequestDTO;
import com.prerana.userservice.entity.DonationOfferEntity;
import com.prerana.userservice.entity.ModeratorAssignmentEntity;
import com.prerana.userservice.entity.UserEntity;
import org.mapstruct.Mapper;
import org.springframework.data.domain.Page;

@Mapper(componentModel = "spring"
        , uses = {UserEntityMapper.class,
        DonationOfferDtoMapper.class})
public interface ModeratorAssignmentMapper {

    ModeratorAssignmentDto toDto(ModeratorAssignmentEntity entity);

    ModeratorAssignmentEntity toEntity(ModeratorAssignmentRequestDTO requestDto);

    default Page<ModeratorAssignmentDto> toDtoPage(Page<ModeratorAssignmentEntity> entities) {
        return entities.map(this::toDto);
    }
}
