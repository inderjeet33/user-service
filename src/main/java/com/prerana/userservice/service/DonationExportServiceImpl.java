package com.prerana.userservice.service;

import com.prerana.userservice.entity.DonationOfferEntity;
import com.prerana.userservice.entity.ModeratorAssignmentEntity;
import com.prerana.userservice.entity.NGOProfileEntity;
import com.prerana.userservice.enums.AssignmentStatus;
import com.prerana.userservice.repository.CertificateRepository;
import com.prerana.userservice.repository.DonationOfferRepository;
import com.prerana.userservice.repository.ModeratorAssignmentRepository;
import com.prerana.userservice.repository.NGOProfileRepository;
import com.prerana.userservice.service.interfaces.DonationExportService;
import com.prerana.userservice.util.ExcelExportUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.cert.Certificate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DonationExportServiceImpl implements DonationExportService {

    @Autowired
    private final DonationOfferRepository offerRepository;

    @Autowired
    private final CertificateRepository certificateRepository;

    @Autowired
    private ModeratorAssignmentRepository moderatorAssignmentRepository;
    @Autowired
    private NGOProfileRepository nGOProfileRepository;


    @Override
    public byte[] exportMyDonationHistory(Long userId) {

        List<DonationOfferEntity> offers =
                offerRepository.findByUserId(userId);

        return ExcelExportUtil.generateDonationHistoryExcel(
                offers,
                certificateRepository,
                moderatorAssignmentRepository
        );
    }

    public byte[] generateNgoDonationsExcel(Long ngoId) {

        List<ModeratorAssignmentEntity> assignments =
                moderatorAssignmentRepository.findByReceiver_Id(ngoId);

        return ExcelExportUtil.generateNgoExcel(assignments, certificateRepository);
    }

    @Override
    public byte[] exportModeratorNgoList() {
            List<NGOProfileEntity> ngos = nGOProfileRepository.findAll();

        return ExcelExportUtil.exportNgoListForModerator(ngos);
    }

    @Override
    public byte[] exportModeratorDonations(Long moderatorId) {

        List<DonationOfferEntity> donationOfferEntities = offerRepository.findAll();
        List<ModeratorAssignmentEntity> assignmentEntityList = moderatorAssignmentRepository.findByDonationRequest_IdInAndStatusIn(donationOfferEntities.stream().map(DonationOfferEntity::getId).toList(),
                List.of(AssignmentStatus.ASSIGNED,AssignmentStatus.IN_PROGRESS,AssignmentStatus.COMPLETED));
        return ExcelExportUtil.exportModeratorDonationOffers(donationOfferEntities,assignmentEntityList);
    }

    @Override
    public byte[] exportModeratorAssignmentExcel(Long moderatorId){
        List<ModeratorAssignmentEntity> assignments =
                moderatorAssignmentRepository.findByModerator_IdOrderByCreatedAtDesc(moderatorId);

        return ExcelExportUtil.exportModeratorAssignmentHistory(assignments);

    }


}
