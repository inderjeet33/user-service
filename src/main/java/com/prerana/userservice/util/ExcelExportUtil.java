package com.prerana.userservice.util;

import com.prerana.userservice.dto.DonationCertificateDto;
import com.prerana.userservice.entity.DonationCertificateEntity;
import com.prerana.userservice.entity.DonationOfferEntity;
import com.prerana.userservice.entity.ModeratorAssignmentEntity;
import com.prerana.userservice.entity.NGOProfileEntity;
import com.prerana.userservice.repository.CertificateRepository;
import com.prerana.userservice.repository.ModeratorAssignmentRepository;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class ExcelExportUtil {

    public static byte[] generateDonationHistoryExcel(
            List<DonationOfferEntity> offers,
            CertificateRepository certificateRepo,
            ModeratorAssignmentRepository moderatorAssignmentRepository
    ) {
//        Long donorId = offers.getFirst().getUser().getId();
//        List<ModeratorAssignmentEntity> moderatorAssignmentEntities = moderatorAssignmentRepository.findByDonor_Id(donorId);
//        List<DonationCertificateEntity> certificateEntities = certificateRepo.findByAssignment_IdIn(moderatorAssignmentEntities.stream().map(ModeratorAssignmentEntity::getId).toList());
//
////        Map<Long, ModeratorAssignmentEntity> moderatorAssignmentRepositoryMap = moderatorAssignmentEntities.stream()
////                .collect(Collectors.toMap(m ->
////                                m.getDonationRequest().getId(), // Key: donationRequestId
////                        entity -> entity                               // Value: the entity itself
////                ));
//
//        Map<Long, List<ModeratorAssignmentEntity>> assignmentByOfferId =
//                moderatorAssignmentEntities.stream()
//                        .collect(Collectors.groupingBy(
//                                a -> a.getDonationRequest().getId()
//                        ));
//
//
//        Map<Long, DonationCertificateEntity> certificateDtoMap = certificateEntities.stream()
//                .collect(Collectors.toMap(m ->
//                                m.getAssignment().getId(), // Key: donationRequestId
//                        entity -> entity                               // Value: the entity itself
//                ));

        Long donorId = offers.getFirst().getUser().getId();

        List<ModeratorAssignmentEntity> assignments =
                moderatorAssignmentRepository.findByDonor_Id(donorId);

        List<DonationCertificateEntity> certificates =
                certificateRepo.findByAssignment_IdIn(
                        assignments.stream()
                                .map(ModeratorAssignmentEntity::getId)
                                .toList()
                );

// Group assignments by donation offer
        Map<Long, List<ModeratorAssignmentEntity>> assignmentByOfferId =
                assignments.stream()
                        .collect(Collectors.groupingBy(
                                a -> a.getDonationRequest().getId()
                        ));

// Certificates by assignment id
        Map<Long, DonationCertificateEntity> certificateByAssignmentId =
                certificates.stream()
                        .collect(Collectors.toMap(
                                c -> c.getAssignment().getId(),
                                c -> c,
                                (a, b) -> a
                        ));


        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("Donation History");

            // Header
            Row header = sheet.createRow(0);
            String[] columns = {
                    "Offer ID",
                    "Amount",
                    "Category",
                    "Type",
                    "Offer creation",
                    "Timeline",
                    "Status",
                    "Assigned NGO",
                    "Completed Date",
                    "Certificate ID"
            };

            for (int i = 0; i < columns.length; i++) {
                header.createCell(i).setCellValue(columns[i]);
            }

            int rowIdx = 1;

            for (DonationOfferEntity offer : offers) {

                ModeratorAssignmentEntity latestAssignment =
                        assignmentByOfferId
                                .getOrDefault(offer.getId(), List.of())
                                .stream()
                                .max((a, b) -> a.getId().compareTo(b.getId()))
                                .orElse(null);

                DonationCertificateEntity offerCertificate =
                        latestAssignment != null
                                ? certificateByAssignmentId.get(latestAssignment.getId())
                                : null;

                Row row = sheet.createRow(rowIdx++);


                row.createCell(0).setCellValue(offer.getId());
                row.createCell(1).setCellValue(
                        offer.getAmount() != null ? offer.getAmount() : 0
                );
                row.createCell(2).setCellValue(offer.getDonationCategory().name());
                row.createCell(3).setCellValue(offer.getType());
                row.createCell(4).setCellValue(offer.getCreatedAt().toString());
                row.createCell(5).setCellValue(offer.getTimeLine());
                row.createCell(6).setCellValue(
                        offer.getStatus() != null ? offer.getStatus().name() : ""
                );
//                row.createCell(6).setCellValue(
//                        Objects.nonNull( offerAssignmentEntity.getReceiver()) ? offerAssignmentEntity.getReceiver().getFullName() : ""
//                );
                row.createCell(7).setCellValue(
                        latestAssignment != null && latestAssignment.getReceiver() != null
                                ? latestAssignment.getReceiver().getFullName()
                                : ""
                );

                row.createCell(8).setCellValue(
                        offerCertificate != null
                                ? offerCertificate.getIssuedDate().toString()
                                : ""
                );
                row.createCell(9).setCellValue(
                        offerCertificate != null ? offerCertificate.getCertificateId() : ""
                );
            }

            // Auto size
            for (int i = 0; i < columns.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(out);
            return out.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Failed to generate Excel", e);
        }
    }

    public static byte[] generateNgoExcel(
            List<ModeratorAssignmentEntity> assignments,
            CertificateRepository certificateRepo
    ) {
        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("NGO Donations");

            String[] headers = {
                    "Assignment ID",
                    "Offer ID",
                    "Donor Name",
                    "Email",
                    "Mobile",
                    "Amount",
                    "Category",
                    "Type",
                    "Status",
                    "Assigned Date",
                    "Completed Date",
                    "Certificate ID"
            };

            Row header = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                header.createCell(i).setCellValue(headers[i]);
            }

            int rowIdx = 1;

            for (ModeratorAssignmentEntity a : assignments) {

                DonationOfferEntity offer = a.getDonationRequest();
                DonationCertificateEntity cert =
                        certificateRepo.findByAssignment_Id(a.getId()).orElse(null);

                Row row = sheet.createRow(rowIdx++);

                row.createCell(0).setCellValue(a.getId());
                row.createCell(1).setCellValue(offer.getId());
                row.createCell(2).setCellValue(offer.getUser().getFullName());
                row.createCell(3).setCellValue(offer.getUser().getEmail());
                row.createCell(4).setCellValue(offer.getUser().getMobileNumber());
                row.createCell(5).setCellValue(offer.getAmount());
                row.createCell(6).setCellValue(offer.getDonationCategory().name());
                row.createCell(7).setCellValue(offer.getType());
                row.createCell(8).setCellValue(offer.getStatus().name());
                row.createCell(9).setCellValue(a.getCreatedAt().toString());

                row.createCell(10).setCellValue(
                        cert != null ? cert.getIssuedDate().toString() : ""
                );

                row.createCell(11).setCellValue(
                        cert != null ? cert.getCertificateId() : ""
                );
            }

            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(out);
            return out.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Failed to generate NGO Excel", e);
        }
    }

    public static byte[] exportNgoListForModerator(List<NGOProfileEntity> ngos) {

        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("NGOs");

            String[] columns = {
                    "NGO ID",
                    "NGO Name",
                    "Registration Number",
                    "Email",
                    "Phone",
                    "District",
                    "City",
                    "State",
                    "Pincode",
                    "Categories",
                    "Created On"
            };

            Row header = sheet.createRow(0);
            for (int i = 0; i < columns.length; i++) {
                header.createCell(i).setCellValue(columns[i]);
            }

            int rowIdx = 1;

            for (NGOProfileEntity ngo : ngos) {

                Row row = sheet.createRow(rowIdx++);

                row.createCell(0).setCellValue(ngo.getId());
                row.createCell(1).setCellValue(ngo.getNgoName());
                row.createCell(2).setCellValue(ngo.getRegistrationNumber());
                row.createCell(3).setCellValue(ngo.getEmail());
                row.createCell(4).setCellValue(ngo.getPhone());
                row.createCell(5).setCellValue(ngo.getDistrict());
                row.createCell(6).setCellValue(ngo.getCity());
                row.createCell(7).setCellValue(ngo.getState());
                row.createCell(8).setCellValue(ngo.getPincode());
                System.out.println("printing pincode in 9 "+ngo.getPincode());
                row.createCell(9).setCellValue(ngo.getCategories());
                row.createCell(10).setCellValue(
                        ngo.getCreatedAt() != null
                                ? ngo.getCreatedAt().toString()
                                : ""
                );
            }

            for (int i = 0; i < columns.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(out);
            return out.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Failed to export NGO list", e);
        }
    }

    public static byte[] exportModeratorDonationOffers(
            List<DonationOfferEntity> offers,
            List<ModeratorAssignmentEntity> assignments
    ) {

        // Map latest assignment per donation
        Map<Long, ModeratorAssignmentEntity> assignmentMap =
                assignments.stream()
                        .collect(Collectors.toMap(
                                a -> a.getDonationRequest().getId(),
                                a -> a,
                                (a1, a2) -> a2 // keep latest if reassigned
                        ));

        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("Donation Offers");

            String[] columns = {
                    "Donation ID",
                    "Donor Name",
                    "Amount",
                    "Category",
                    "Offer Status",
                    "Assigned NGO",
                    "Assignment Status",
                    "Created On"
            };

            Row header = sheet.createRow(0);
            for (int i = 0; i < columns.length; i++) {
                header.createCell(i).setCellValue(columns[i]);
            }

            int rowIdx = 1;

            for (DonationOfferEntity offer : offers) {

                ModeratorAssignmentEntity assignment =
                        assignmentMap.get(offer.getId());

                Row row = sheet.createRow(rowIdx++);

                row.createCell(0).setCellValue(offer.getId());
                row.createCell(1).setCellValue(
                        offer.getUser() != null
                                ? offer.getUser().getFullName()
                                : ""
                );
                row.createCell(2).setCellValue(
                        offer.getAmount() != null ? offer.getAmount() : 0
                );
                row.createCell(3).setCellValue(offer.getDonationCategory().name());
                row.createCell(4).setCellValue(offer.getStatus().name());

                row.createCell(5).setCellValue(
                        assignment != null && assignment.getReceiver() != null
                                ? assignment.getReceiver().getFullName()
                                : "Not Assigned"
                );

                row.createCell(6).setCellValue(
                        assignment != null
                                ? assignment.getStatus().name()
                                : ""
                );

                row.createCell(7).setCellValue(
                        offer.getCreatedAt() != null
                                ? offer.getCreatedAt().toString()
                                : ""
                );
            }

            for (int i = 0; i < columns.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(out);
            return out.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Failed to export moderator donation offers", e);
        }
    }

    public static byte[] exportModeratorAssignmentHistory(
            List<ModeratorAssignmentEntity> assignments
    ) {

        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("Assignment History");

            String[] columns = {
                    "Assignment ID",
                    "Donation ID",
                    "Donor Name",
                    "Donation Amount",
                    "Category",
                    "Donation Status",
                    "Assigned NGO",
                    "Assignment Status",
                    "Assigned On"
            };

            Row header = sheet.createRow(0);
            for (int i = 0; i < columns.length; i++) {
                header.createCell(i).setCellValue(columns[i]);
            }

            int rowIdx = 1;

            for (ModeratorAssignmentEntity assignment : assignments) {

                DonationOfferEntity offer = assignment.getDonationRequest();

                Row row = sheet.createRow(rowIdx++);

                row.createCell(0).setCellValue(assignment.getId());
                row.createCell(1).setCellValue(
                        offer != null ? offer.getId() : null
                );
                row.createCell(2).setCellValue(
                        offer != null && offer.getUser() != null
                                ? offer.getUser().getFullName()
                                : ""
                );
                row.createCell(3).setCellValue(
                        offer != null && offer.getAmount() != null
                                ? offer.getAmount()
                                : 0
                );
                row.createCell(4).setCellValue(
                        offer != null ? offer.getDonationCategory().name() : ""
                );
                row.createCell(5).setCellValue(
                        offer != null ? offer.getStatus().name() : ""
                );
                row.createCell(6).setCellValue(
                        assignment.getReceiver() != null
                                ? assignment.getReceiver().getFullName()
                                : ""
                );
                row.createCell(7).setCellValue(
                        assignment.getStatus().name()
                );
                row.createCell(8).setCellValue(
                        assignment.getCreatedAt() != null
                                ? assignment.getCreatedAt().toString()
                                : ""
                );
            }

            for (int i = 0; i < columns.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(out);
            return out.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException(
                    "Failed to export assignment history", e
            );
        }
    }



}
