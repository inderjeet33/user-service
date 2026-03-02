package com.prerana.userservice.certificate;

import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.prerana.userservice.dto.DonationCertificateDto;
import com.prerana.userservice.dto.VolunteerCertificateDto;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.time.Year;

@Component
public class CertificatePdfGenerator {

    public byte[] generate(DonationCertificateDto dto) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        PdfWriter writer = new PdfWriter(out);
        PdfDocument pdf = new PdfDocument(writer);

        // ✅ MUST create page first
        PdfPage page = pdf.addNewPage(PageSize.A4);

        Document doc = new Document(pdf);
        doc.setMargins(50, 50, 50, 50);

        // Border
        PdfCanvas canvas = new PdfCanvas(page);
        canvas.setStrokeColor(new DeviceRgb(180, 150, 90));
        canvas.setLineWidth(3);
        canvas.rectangle(30, 30, 535, 782);
        canvas.stroke();

        // ---- CONTENT ----
        doc.add(new Paragraph("CERTIFICATE OF APPRECIATION")
                .setFontSize(26)
                .setBold()
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(30));

        doc.add(new Paragraph(
                "This certificate is proudly presented to")
                .setTextAlignment(TextAlignment.CENTER));

        doc.add(new Paragraph(dto.getDonorName())
                .setFontSize(22)
                .setBold()
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(20));

        doc.add(new Paragraph(
                "For successfully completing a donation through\n" +
                        "Prerana Helpline Foundation")
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(30));

        doc.add(new Paragraph("Donation Amount: ₹" + dto.getAmount())
                .setTextAlignment(TextAlignment.CENTER));

        doc.add(new Paragraph("Supported NGO: " + dto.getNgoName())
                .setTextAlignment(TextAlignment.CENTER));

        doc.add(new Paragraph("Donation Id: "+dto.getCertificateId())
                .setTextAlignment(TextAlignment.CENTER));

        doc.add(new Paragraph("Date: " + dto.getCompletedDate())
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginTop(40));


        doc.close();
        return out.toByteArray();
    }

    public static String generateCertificateId(Long dbId) {
        return "PRERANA-" + Year.now().getValue() + "-" + String.format("%06d", dbId);
    }

    public static byte[] generateVolunteerCertificate(VolunteerCertificateDto dto){
            ByteArrayOutputStream out = new ByteArrayOutputStream();

            PdfWriter writer = new PdfWriter(out);
            PdfDocument pdf = new PdfDocument(writer);
            PdfPage page = pdf.addNewPage(PageSize.A4);

            Document doc = new Document(pdf);
            doc.setMargins(60, 60, 60, 60);

            PdfCanvas canvas = new PdfCanvas(page);
            canvas.setStrokeColor(new DeviceRgb(40, 90, 150));
            canvas.setLineWidth(4);
            canvas.rectangle(25, 25, 545, 792);
            canvas.stroke();

            doc.add(new Paragraph("CERTIFICATE OF VOLUNTEER RECOGNITION")
                    .setFontSize(24)
                    .setBold()
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(25));

            doc.add(new Paragraph("This certificate is proudly awarded to")
                    .setTextAlignment(TextAlignment.CENTER));

            doc.add(new Paragraph(dto.getVolunteerName())
                    .setFontSize(22)
                    .setBold()
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(20));

            doc.add(new Paragraph(
                    "For dedicated voluntary service in the field of " +
                            dto.getVolunteerType())
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(20));

            doc.add(new Paragraph("In collaboration with NGO: " + dto.getNgoName())
                    .setTextAlignment(TextAlignment.CENTER));

            doc.add(new Paragraph("Certificate ID: " + dto.getCertificateId())
                    .setTextAlignment(TextAlignment.CENTER));

            doc.add(new Paragraph("Issued on: " + dto.getCompletedDate())
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginTop(40));

            doc.close();
            return out.toByteArray();
    }

}
