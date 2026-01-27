package com.prerana.userservice.service;

import com.prerana.userservice.dto.HelpRequestCreateDto;
import com.prerana.userservice.dto.HelpRequestHistoryDto;
import com.prerana.userservice.dto.HelpRequestResponseDto;
import com.prerana.userservice.entity.HelpRequestEntity;
import com.prerana.userservice.entity.UserEntity;
import com.prerana.userservice.enums.DonationCategory;
import com.prerana.userservice.enums.HelpRequestStatus;
import com.prerana.userservice.enums.HelpType;
import com.prerana.userservice.repository.HelpRequestRepository;
import com.prerana.userservice.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HelpRequestService {

    private final HelpRequestRepository helpRequestRepository;
    private final UserRepository userRepository;

    @Transactional
    public HelpRequestResponseDto createHelpRequest(Long userId, HelpRequestCreateDto dto) {

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        HelpRequestEntity entity = HelpRequestEntity.builder()
                .user(user)
                .donationCategory(DonationCategory.valueOf(dto.getDonationCategory()))
                .helpType(HelpType.valueOf(dto.getHelpType()))
                .amount(dto.getAmount())
                .itemDetails(dto.getItemDetails())
                .quantity(dto.getQuantity())
                .urgency(dto.getUrgency())
                .location(dto.getLocation())
                .preferredContact(dto.getPreferredContact())
                .reason(dto.getReason())
                .status(HelpRequestStatus.OPEN)
                .build();

        entity = helpRequestRepository.save(entity);

        return toDto(entity);
    }

    public List<HelpRequestHistoryDto> getMyHelpRequests(Long userId) {

        return helpRequestRepository
                .findByUser_IdOrderByCreatedAtDesc(userId)
                .stream()
                .map(this::mapToHistoryDto)

                .collect(Collectors.toList());
    }

    private HelpRequestHistoryDto mapToHistoryDto(HelpRequestEntity e) {

        HelpRequestHistoryDto dto = new HelpRequestHistoryDto();

        dto.setId(e.getId());
        dto.setDonationCategory(e.getDonationCategory().name());
        dto.setHelpType(e.getHelpType().name());
        dto.setAmount(e.getAmount());
        dto.setItemDetails(e.getItemDetails());
        dto.setQuantity(e.getQuantity());
        dto.setUrgency(e.getUrgency());
        dto.setLocation(e.getLocation());
        dto.setPreferredContact(e.getPreferredContact());
        dto.setReason(e.getReason());
        dto.setStatus(e.getStatus());
        dto.setCreatedAt(e.getCreatedAt());

        return dto;

    }

        private HelpRequestResponseDto toDto(HelpRequestEntity entity) {

        return HelpRequestResponseDto.builder()
                .id(entity.getId())
                .donationCategory(entity.getDonationCategory().name())
                .helpType(entity.getHelpType().name())
                .amount(entity.getAmount())
                .itemDetails(entity.getItemDetails())
                .quantity(entity.getQuantity())
                .urgency(entity.getUrgency())
                .location(entity.getLocation())
                .preferredContact(entity.getPreferredContact())
                .reason(entity.getReason())
                .status(entity.getStatus())

                .userId(entity.getUser().getId())
                .userName(entity.getUser().getFullName())
                .userEmail(entity.getUser().getEmail())
                .userMobile(entity.getUser().getMobileNumber())
                .build();
    }

}
