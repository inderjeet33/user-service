package com.prerana.userservice.service;

import com.prerana.userservice.dto.SupportQueryRequestDto;
import com.prerana.userservice.entity.SupportQueryEntity;
import com.prerana.userservice.repository.SupportQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SupportQueryService {

    private final SupportQueryRepository repository;

    public void submitQuery(
            SupportQueryRequestDto dto,
            String role,
            Long userId
    ) {
        SupportQueryEntity query = SupportQueryEntity.builder()
                .name(dto.getName())
                .type(role)
                .emailOrPhone(dto.getEmailOrPhone())
                .concernType(dto.getConcernType())
                .message(dto.getMessage())
                .build();

        repository.save(query);
    }
}
