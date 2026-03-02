package com.prerana.userservice.service;

import com.prerana.userservice.dto.CsrProfile;
import com.prerana.userservice.dto.ModeratorUserProfileDto;
import com.prerana.userservice.dto.NgoProfile;
import com.prerana.userservice.entity.UserEntity;
import com.prerana.userservice.enums.DonationOfferStatus;
import com.prerana.userservice.enums.HelpRequestStatus;
import com.prerana.userservice.enums.UserType;
import com.prerana.userservice.enums.VolunteerOfferStatus;
import com.prerana.userservice.mapper.CsrMapper;
import com.prerana.userservice.mapper.NgoMapper;
import com.prerana.userservice.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ModeratorUserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DonationOfferRepository donationRepo;

    @Autowired
    private NgoMapper ngoMapper;

    @Autowired
    private CsrMapper csrMapper;

    @Autowired
    private CSRProfileRepository csrProfileRepository;

    @Autowired
    private HelpRequestRepository helpRepo;

    @Autowired
    private VolunteerRequestRepository volunteerRepo;

    @Autowired
    private NGOProfileRepository ngoProfileRepository;


    public ModeratorUserProfileDto getUserProfile(Long userId) {

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // -------- Donation Stats --------
        Long totalDonations = donationRepo.countByUser_Id(userId);

        Long activeDonations = donationRepo.countByUser_IdAndStatusIn(
                userId,
                List.of(
                        DonationOfferStatus.OPEN,
                        DonationOfferStatus.ASSIGNED,
                        DonationOfferStatus.IN_PROGRESS,
                        DonationOfferStatus.DELIVERED,
                        DonationOfferStatus.UNDER_REVIEW
                )
        );

        Long completedDonations =
                donationRepo.countByUser_IdAndStatus(userId, DonationOfferStatus.COMPLETED);

        Long cancelledDonations =
                donationRepo.countByUser_IdAndStatusIn(
                        userId,
                        List.of(
                                DonationOfferStatus.CANCELLED,
                                DonationOfferStatus.EXPIRED
                        )
                );

        Double cancellationRate = totalDonations == 0 ? 0.0 :
                (cancelledDonations.doubleValue() / totalDonations) * 100;

        // -------- Volunteer Stats --------
        Long totalVolunteer = volunteerRepo.countByUser_Id(userId);

        Long completedVolunteer =
                volunteerRepo.countByUser_IdAndStatus(
                        userId,
                        VolunteerOfferStatus.COMPLETED
                );

        Long cancelledVolunteer =
                volunteerRepo.countByUser_IdAndStatus(
                        userId,
                        VolunteerOfferStatus.CANCELLED
                );

        Long activeVolunteer =
                volunteerRepo.countByUser_IdAndStatusIn(
                        userId,
                        List.of(
                                VolunteerOfferStatus.OPEN,
                                VolunteerOfferStatus.ASSIGNED,
                                VolunteerOfferStatus.IN_PROGRESS
                        )
                );

        // -------- Help Request Stats --------
        Long totalHelp = helpRepo.countByUser_Id(userId);

        Long completedHelp =
                helpRepo.countByUser_IdAndStatus(
                        userId,
                        HelpRequestStatus.COMPLETED
                );

        Long cancelledHelp =
                helpRepo.countByUser_IdAndStatus(
                        userId,
                        HelpRequestStatus.CANCELLED
                );

        Long activeHelp =
                helpRepo.countByUser_IdAndStatusIn(
                        userId,
                        List.of(
                                HelpRequestStatus.OPEN,
                                HelpRequestStatus.ASSIGNED,
                                HelpRequestStatus.IN_PROGRESS,
                                HelpRequestStatus.APPROVED,
                                HelpRequestStatus.DELIVERED
                        )
                );

        return ModeratorUserProfileDto.builder()
                .fullName(user.getFullName())
                .email(user.getEmail())
                .mobileNumber(user.getMobileNumber())
                .userType(user.getUserType().name())
                .role(user.getRole().name())
                .verified(user.getIsVerified())
                .profileCompleted(user.isProfileCompleted())
                .city(user.getCity())
                .state(user.getState())
                .profession(user.getProfession())
                .registeredAt(user.getCreatedAt())

                .totalDonations(totalDonations)
                .activeDonations(activeDonations)
                .completedDonations(completedDonations)
                .cancelledDonations(cancelledDonations)

                .totalVolunteerOffers(totalVolunteer)
                .activeVolunteerOffers(activeVolunteer)
                .completedVolunteerOffers(completedVolunteer)
                .cancelledVolunteerOffers(cancelledVolunteer)

                .totalHelpRequests(totalHelp)
                .activeHelpRequests(activeHelp)
                .completedHelpRequests(completedHelp)
                .cancelledHelpRequests(cancelledHelp)

                .donationCancellationRate(cancellationRate)
                .build();
    }

    public ModeratorUserProfileDto getFullUserProfile(Long userId) {

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        NgoProfile ngoProfile = null;
        CsrProfile csrProfile = null;

        if (user.getUserType() == UserType.NGO) {
            ngoProfile = ngoMapper.toDto(ngoProfileRepository.findByUserId(userId).orElse(null));
        }

        if (user.getUserType() == UserType.CSR) {
            csrProfile = csrMapper.toDto(csrProfileRepository.findByUserId(userId).orElse(null));
        }

        long donationCount = donationRepo.countByUser_Id(userId);
        long volunteerCount = volunteerRepo.countByUser_Id(userId);
        long helpRequestCount = helpRepo.countByUser_Id(userId);

        return ModeratorUserProfileDto.builder()
                .fullName(user.getFullName())
                .email(user.getEmail())
                .mobileNumber(user.getMobileNumber())
                .userType(user.getUserType().name())
                .role(user.getRole().name())
                .verified(user.getIsVerified())
                .profileCompleted(user.isProfileCompleted())
                .city(user.getCity())
                .state(user.getState())
                .profession(user.getProfession())
                .registeredAt(user.getCreatedAt())
                .totalDonations(donationCount)
                .ngoProfile(ngoProfile)
                .csrProfile(csrProfile)
                .totalVolunteerOffers(volunteerCount)
                .totalHelpRequests(helpRequestCount)
                .build();


    }
}
