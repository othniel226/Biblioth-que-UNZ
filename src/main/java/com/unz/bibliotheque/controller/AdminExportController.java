package com.unz.bibliotheque.controller;

import com.unz.bibliotheque.model.enums.StatutEmprunt;
import com.unz.bibliotheque.repository.*;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import lombok.RequiredArgsConstructor;
import org.apache.poi.xssf.usermodel.*;
import org.apache.poi.xwpf.usermodel.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletResponse;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Contrôleur gérant l'export des rapports en PDF, Excel et Word.
 * Accessible uniquement aux administrateurs.
 *
 * Routes :
 *   GET /admin/rapports/export/pdf   → Rapport PDF
 *   GET /admin/rapports/export/excel → Rapport Excel
 *   GET /admin/rapports/export/word  → Rapport Word
 */
@org.springframework.stereotype.Controller
@RequestMapping("/admin/rapports/export")
@RequiredArgsConstructor
public class AdminExportController {

    private final EmpruntRepository    empruntRepo;
    private final OuvrageRepository    ouvrageRepo;
    private final UtilisateurRepository utilisateurRepo;
    private final PenaliteRepository   penaliteRepo;
    private final ReservationRepository reservationRepo;

    /**
     * Exporte le rapport en PDF (iText).
     */
    @GetMapping("/pdf")
    public void exportPdf(HttpServletResponse response) throws Exception {
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition",
            "attachment; filename=\"rapport_bibliotheque_" +
            LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmm")) + ".pdf\"");

        com.itextpdf.text.Document doc = new com.itextpdf.text.Document(com.itextpdf.text.PageSize.A4);
        PdfWriter.getInstance(doc, response.getOutputStream());
        doc.open();

        // Titre
        Font titleFont = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD, BaseColor.DARK_GRAY);
        Font headFont  = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD, BaseColor.WHITE);
        Font cellFont  = new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL, BaseColor.DARK_GRAY);

        Paragraph title = new Paragraph("Rapport — Bibliothèque UNZ", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(5);
        doc.add(title);

        Paragraph date = new Paragraph("Généré le " +
            LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy à HH:mm")),
            new Font(Font.FontFamily.HELVETICA, 10, Font.ITALIC, BaseColor.GRAY));
        date.setAlignment(Element.ALIGN_CENTER);
        date.setSpacingAfter(20);
        doc.add(date);

        // Statistiques générales
        doc.add(new Paragraph("Statistiques générales",
            new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD, BaseColor.DARK_GRAY)));
        doc.add(Chunk.NEWLINE);

        PdfPTable statsTable = new PdfPTable(2);
        statsTable.setWidthPercentage(80);
        statsTable.setSpacingAfter(20);
        statsTable.setHorizontalAlignment(Element.ALIGN_LEFT);

        BaseColor headerColor = new BaseColor(26, 35, 126);
        addStatsRow(statsTable, "Total emprunts", String.valueOf(empruntRepo.count()), headerColor, headFont, cellFont);
        addStatsRow(statsTable, "Emprunts en cours", String.valueOf(empruntRepo.countByStatut(StatutEmprunt.EN_COURS)), headerColor, headFont, cellFont);
        addStatsRow(statsTable, "Emprunts en retard", String.valueOf(empruntRepo.countByStatut(StatutEmprunt.EN_RETARD)), new BaseColor(198, 40, 40), headFont, cellFont);
        addStatsRow(statsTable, "Total ouvrages", String.valueOf(ouvrageRepo.count()), headerColor, headFont, cellFont);
        addStatsRow(statsTable, "Total utilisateurs", String.valueOf(utilisateurRepo.count()), headerColor, headFont, cellFont);
        addStatsRow(statsTable, "Réservations actives", String.valueOf(reservationRepo.count()), headerColor, headFont, cellFont);
        doc.add(statsTable);

        // Top ouvrages
        doc.add(new Paragraph("Top ouvrages les plus empruntés",
            new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD, BaseColor.DARK_GRAY)));
        doc.add(Chunk.NEWLINE);

        PdfPTable ouvragesTable = new PdfPTable(3);
        ouvragesTable.setWidthPercentage(100);
        ouvragesTable.setWidths(new float[]{1, 4, 3});

        // En-têtes
        for (String h : new String[]{"#", "Titre", "Auteur"}) {
            PdfPCell cell = new PdfPCell(new Phrase(h, headFont));
            cell.setBackgroundColor(headerColor);
            cell.setPadding(8);
            ouvragesTable.addCell(cell);
        }

        var topOuvrages = ouvrageRepo.findTopEmpruntes(
            org.springframework.data.domain.PageRequest.of(0, 10));
        int i = 1;
        for (var o : topOuvrages) {
            ouvragesTable.addCell(new PdfPCell(new Phrase(String.valueOf(i++), cellFont)));
            ouvragesTable.addCell(new PdfPCell(new Phrase(o.getTitre(), cellFont)));
            ouvragesTable.addCell(new PdfPCell(new Phrase(o.getAuteur(), cellFont)));
        }

        doc.add(ouvragesTable);
        doc.close();
    }

    private void addStatsRow(PdfPTable table, String label, String value,
                             BaseColor bg, Font hf, Font cf) {
        PdfPCell c1 = new PdfPCell(new Phrase(label, hf));
        c1.setBackgroundColor(bg);
        c1.setPadding(8);
        table.addCell(c1);

        PdfPCell c2 = new PdfPCell(new Phrase(value, cf));
        c2.setPadding(8);
        table.addCell(c2);
    }

    /**
     * Exporte le rapport en Excel (Apache POI).
     */
    @GetMapping("/excel")
    public void exportExcel(HttpServletResponse response) throws Exception {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition",
            "attachment; filename=\"rapport_bibliotheque_" +
            LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmm")) + ".xlsx\"");

        XSSFWorkbook workbook = new XSSFWorkbook();

        // Feuille statistiques
        XSSFSheet sheet = workbook.createSheet("Statistiques");
        XSSFCellStyle headerStyle = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setBold(true);
        font.setColor(org.apache.poi.ss.usermodel.IndexedColors.WHITE.getIndex());
        headerStyle.setFont(font);
        headerStyle.setFillForegroundColor(new org.apache.poi.xssf.usermodel.XSSFColor(
            new byte[]{(byte)26, (byte)35, (byte)126}, null));
        headerStyle.setFillPattern(org.apache.poi.ss.usermodel.FillPatternType.SOLID_FOREGROUND);

        String[] headers = {"Indicateur", "Valeur"};
        XSSFRow headerRow = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            XSSFCell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        String[][] data = {
            {"Total emprunts",          String.valueOf(empruntRepo.count())},
            {"Emprunts en cours",       String.valueOf(empruntRepo.countByStatut(StatutEmprunt.EN_COURS))},
            {"Emprunts en retard",      String.valueOf(empruntRepo.countByStatut(StatutEmprunt.EN_RETARD))},
            {"Emprunts rendus",         String.valueOf(empruntRepo.countByStatut(StatutEmprunt.RENDU))},
            {"Total ouvrages",          String.valueOf(ouvrageRepo.count())},
            {"Total utilisateurs",      String.valueOf(utilisateurRepo.count())},
            {"Réservations actives",    String.valueOf(reservationRepo.count())},
        };

        for (int i = 0; i < data.length; i++) {
            XSSFRow row = sheet.createRow(i + 1);
            row.createCell(0).setCellValue(data[i][0]);
            row.createCell(1).setCellValue(data[i][1]);
        }

        sheet.autoSizeColumn(0);
        sheet.autoSizeColumn(1);

        workbook.write(response.getOutputStream());
        workbook.close();
    }

    /**
     * Exporte le rapport en Word (Apache POI XWPF).
     */
    @GetMapping("/word")
    public void exportWord(HttpServletResponse response) throws Exception {
        response.setContentType("application/vnd.openxmlformats-officedocument.wordprocessingml.document");
        response.setHeader("Content-Disposition",
            "attachment; filename=\"rapport_bibliotheque_" +
            LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmm")) + ".docx\"");

        org.apache.poi.xwpf.usermodel.XWPFDocument doc = new org.apache.poi.xwpf.usermodel.XWPFDocument();

        // Titre
        XWPFParagraph titlePara = doc.createParagraph();
        titlePara.setAlignment(ParagraphAlignment.CENTER);
        XWPFRun titleRun = titlePara.createRun();
        titleRun.setText("Rapport — Bibliothèque UNZ");
        titleRun.setBold(true);
        titleRun.setFontSize(20);
        titleRun.setColor("1a237e");

        // Date
        XWPFParagraph datePara = doc.createParagraph();
        datePara.setAlignment(ParagraphAlignment.CENTER);
        XWPFRun dateRun = datePara.createRun();
        dateRun.setText("Généré le " +
            LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy à HH:mm")));
        dateRun.setItalic(true);
        dateRun.setColor("888888");

        // Section statistiques
        XWPFParagraph statTitle = doc.createParagraph();
        statTitle.setSpacingBefore(400);
        XWPFRun statRun = statTitle.createRun();
        statRun.setText("Statistiques générales");
        statRun.setBold(true);
        statRun.setFontSize(14);

        // Tableau statistiques
        XWPFTable table = doc.createTable(7, 2);
        table.setWidth("5000");
        String[][] rows = {
            {"Indicateur", "Valeur"},
            {"Total emprunts",      String.valueOf(empruntRepo.count())},
            {"En cours",            String.valueOf(empruntRepo.countByStatut(StatutEmprunt.EN_COURS))},
            {"En retard",           String.valueOf(empruntRepo.countByStatut(StatutEmprunt.EN_RETARD))},
            {"Total ouvrages",      String.valueOf(ouvrageRepo.count())},
            {"Utilisateurs",        String.valueOf(utilisateurRepo.count())},
            {"Réservations",        String.valueOf(reservationRepo.count())},
        };

        for (int i = 0; i < rows.length; i++) {
            XWPFTableRow row = table.getRow(i);
            row.getCell(0).setText(rows[i][0]);
            row.getCell(1).setText(rows[i][1]);
        }

        doc.write(response.getOutputStream());
        doc.close();
    }
}
