package com.prerana.userservice.mapper;

import com.prerana.userservice.dto.NgoProfile;
import com.prerana.userservice.dto.UserDto;
import com.prerana.userservice.entity.NGOProfileEntity;
import com.prerana.userservice.entity.UserEntity;
import org.mapstruct.Mapper;
import org.springframework.data.domain.Page;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserEntityMapper {
    UserDto toDto(UserEntity entity);

    UserEntity toEntity(UserDto dto);

//    List<UserDto> toDtoList(List<UserEntity> entities);
//
//    // Default method for Page mapping
//    default Page<UserDto> toDtoPage(Page<UserEntity> entities) {
//        return entities.map(this::toDto);
//    }
}
