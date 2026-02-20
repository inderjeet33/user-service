package com.prerana.userservice.service;
import com.prerana.userservice.dto.CsrDashboardStatsDto;
import com.prerana.userservice.enums.AssignmentStatus;
import com.prerana.userservice.enums.DonationOfferStatus;
import com.prerana.userservice.enums.UserType;
import com.prerana.userservice.repository.DonationOfferRepository;
import com.prerana.userservice.repository.HelpRequestAssignmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CSRDashboardService {

    @Autowired
    private DonationOfferRepository donationRepo;

    @Autowired
    private HelpRequestAssignmentRepository helpAssignmentRepo;

    public CsrDashboardStatsDto getStats(Long csrUserId) {

        long totalDonations = donationRepo.countByUser_Id(csrUserId);
        long completedDonations =
                donationRepo.countByUser_IdAndStatus(
                        csrUserId, DonationOfferStatus.COMPLETED);

        long activeDonations =
                donationRepo.countByUser_IdAndStatusIn(
                        csrUserId,
                        List.of(
                                DonationOfferStatus.OPEN,
                                DonationOfferStatus.ASSIGNED,
                                DonationOfferStatus.IN_PROGRESS
                        )
                );

        Long totalAmount =
                donationRepo.sumCompletedDonationAmount(csrUserId);

        // Help requests assigned to CSR
        long helpAssigned =
                helpAssignmentRepo.countByHelper_IdAndHelperType(
                        csrUserId, UserType.CSR);

        long helpInProgress =
                helpAssignmentRepo.countByHelper_IdAndHelperTypeAndStatus(
                        csrUserId, UserType.CSR, AssignmentStatus.IN_PROGRESS);

        long helpCompleted =
                helpAssignmentRepo.countByHelper_IdAndHelperTypeAndStatus(
                        csrUserId, UserType.CSR, AssignmentStatus.COMPLETED);

        return CsrDashboardStatsDto.builder()
                .totalDonations(totalDonations)
                .completedDonations(completedDonations)
                .activeDonations(activeDonations)
                .totalDonationAmount(totalAmount == null ? 0 : totalAmount)

                .helpRequestsAssigned(helpAssigned)
                .helpRequestsInProgress(helpInProgress)
                .helpRequestsCompleted(helpCompleted)

                .ngosWorkedWith(0L)     // can add later
                .livesImpacted(0L)      // placeholder
                .build();
    }
}
