package com.prerana.userservice.mapper;

import com.prerana.userservice.dto.DemoDestinationDto;
import com.prerana.userservice.dto.DemoSourceDto;
import org.mapstruct.Mapper;

@Mapper
public interface DemoObjectMapper {

    DemoSourceDto toSourceDto(DemoDestinationDto dto);

    DemoDestinationDto toDestinationDto(DemoSourceDto dto);
}
