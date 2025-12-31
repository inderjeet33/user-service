package com.prerana.userservice.service;

import com.prerana.userservice.dto.IndividualDashboardStatsDto;
import com.prerana.userservice.dto.ModeratorDashboardStatsDto;
import com.prerana.userservice.dto.NgoDashboardStatsDto;
import com.prerana.userservice.entity.DonationOfferEntity;
import com.prerana.userservice.enums.ActivationStatus;
import com.prerana.userservice.enums.AssignmentStatus;
import com.prerana.userservice.enums.CampaignStatus;
import com.prerana.userservice.enums.DonationOfferStatus;
import com.prerana.userservice.repository.CampaignRepository;
import com.prerana.userservice.repository.DonationOfferRepository;
import com.prerana.userservice.repository.ModeratorAssignmentRepository;
import com.prerana.userservice.repository.NGOProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
public class DashboardService {

    @Autowired
    private DonationOfferRepository donationOfferRepository;
    @Autowired
    private CampaignRepository campaignRepository;

    @Autowired
    private ModeratorAssignmentRepository assignmentRepository;
    @Autowired
    private NGOProfileRepository nGOProfileRepository;

    public IndividualDashboardStatsDto getIndividualStats(Long userId) {

        List<DonationOfferEntity> offers =
                donationOfferRepository.findByUserId(userId);

        long totalDonations = offers.size();

        long totalAmount = offers.stream()
                .filter(o -> o.getAmount() != null)
                .mapToLong(DonationOfferEntity::getAmount)
                .sum();

        long activeDonations = offers.stream()
                .filter(o ->
                        o.getStatus() == DonationOfferStatus.ASSIGNED ||
                                o.getStatus() == DonationOfferStatus.IN_PROGRESS
                )
                .count();

        long ngosConnected = offers.stream()
                .filter(o -> o.getStatus() != DonationOfferStatus.OPEN)
                .count();

        return IndividualDashboardStatsDto.builder()
                .totalDonations(totalDonations)
                .totalAmount(totalAmount)
                .activeDonations(activeDonations)
                .ngosConnected(ngosConnected)
                .build();
    }

    public NgoDashboardStatsDto getNgoStats(Long ngoId) {

        long totalRequests =
                campaignRepository.countByOwner_Id(ngoId);

        long activeRequests =
                campaignRepository.countByOwner_IdAndStatus(
                        ngoId, CampaignStatus.ACTIVE
                );

        long assignedDonations =
                assignmentRepository.countByReceiver_Id(ngoId);

        long pendingDonations =
                assignmentRepository.countByReceiver_IdAndStatus(
                        ngoId, AssignmentStatus.ASSIGNED
                );

        long completedDonations =
                assignmentRepository.countByReceiver_IdAndStatus(
                        ngoId, AssignmentStatus.COMPLETED
                );

        Long campaignsCount = campaignRepository.countByOwner_Id(ngoId);
        return NgoDashboardStatsDto.builder()
                .totalRequests(totalRequests)
                .activeRequests(activeRequests)
                .assignedDonations(assignedDonations)
                .pendingDonations(pendingDonations)
                .completedDonations(completedDonations)
                .campaigns(campaignsCount)
                .build();
    }

    public ModeratorDashboardStatsDto getStats() {
        return new ModeratorDashboardStatsDto(
                donationOfferRepository.count(),
                donationOfferRepository.countByStatusIn(List.of(DonationOfferStatus.OPEN)),
                nGOProfileRepository.count(),
                nGOProfileRepository.countByActivationStatus(ActivationStatus.PENDING),
                campaignRepository.countByStatus(CampaignStatus.ACTIVE),
                donationOfferRepository.countCompletedToday(DonationOfferStatus.COMPLETED, LocalDate.now().atStartOfDay(),LocalDate.now().atTime(LocalTime.MAX))
        );
    }

}
