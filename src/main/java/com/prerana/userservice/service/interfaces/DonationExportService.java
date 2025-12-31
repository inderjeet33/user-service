package com.prerana.userservice.service.interfaces;

public interface DonationExportService {

    byte[] exportMyDonationHistory(Long userId);

    byte[] generateNgoDonationsExcel(Long ngoId);

    byte[] exportModeratorNgoList();

    byte[] exportModeratorDonations(Long moderatorId);
    byte [] exportModeratorAssignmentExcel(Long moderatorId);

    }
