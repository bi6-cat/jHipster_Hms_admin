package com.hospital.web.rest;

import com.hospital.domain.AppointmentsByCalendarMonth;
import com.hospital.domain.DiseaseByGender;
import com.hospital.domain.RevenueByMonth;
import com.hospital.service.ReportService;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;
import javax.imageio.ImageIO;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/report")
public class ReportResource {

    private final ReportService reportService;

    public ReportResource(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/appointments-by-month/pdf")
    public ResponseEntity<byte[]> exportAppointmentsByMonthPdf() throws Exception {
        List<AppointmentsByCalendarMonth> data = reportService.getCalendarMonthData();

        BaseFont baseFont = BaseFont.createFont("src/main/resources/fonts/arial.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
        Font titleFont = new Font(baseFont, 18, Font.BOLD, new BaseColor(0, 75, 155));
        Font bodyFont = new Font(baseFont, 12, Font.NORMAL);
        Font boldFont = new Font(baseFont, 12, Font.BOLD);
        Font infoFont = new Font(baseFont, 12, Font.NORMAL);

        // Cấu hình tài liệu
        Document document = new Document(PageSize.A4, 36, 36, 72, 72);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter writer = PdfWriter.getInstance(document, baos);

        // Thêm header và footer
        HeaderFooterPageEvent event = new HeaderFooterPageEvent();
        writer.setPageEvent(event);

        document.open();

        // Thêm logo nếu có
        // Thay thế bằng:
        byte[] logoBytes = org.apache.commons.io.IOUtils.toByteArray(new ClassPathResource("static/logo.png").getInputStream());
        Image logo = Image.getInstance(logoBytes);
        logo.scaleToFit(150, 150);
        logo.setAlignment(Element.ALIGN_CENTER);
        document.add(logo);
        // Thêm tiêu đề báo cáo

        Paragraph title = new Paragraph("BÁO CÁO LỊCH HẸN THEO THÁNG", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingBefore(10);
        title.setSpacingAfter(20);
        document.add(title);

        // Thêm thông tin báo cáo

        PdfPTable infoTable = new PdfPTable(2);
        infoTable.setWidthPercentage(100);
        infoTable.setWidths(new float[] { 1, 3 });

        LocalDate today = LocalDate.now();
        String formattedDate = today.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));

        addInfoRow(infoTable, "Ngày tạo:", formattedDate, boldFont, infoFont);
        addInfoRow(infoTable, "Người tạo:", "Admin", boldFont, infoFont);
        addInfoRow(infoTable, "Bệnh viện:", "Bệnh viện Đa khoa Quốc tế", boldFont, infoFont);

        infoTable.setSpacingAfter(20);
        document.add(infoTable);

        // Tạo bảng dữ liệu
        PdfPTable dataTable = new PdfPTable(2);
        dataTable.setWidthPercentage(80);
        dataTable.setHorizontalAlignment(Element.ALIGN_CENTER);
        dataTable.setSpacingBefore(10);
        dataTable.setSpacingAfter(20);

        // Thiết lập header của bảng
        Font tableHeaderFont = new Font(baseFont, 12, Font.BOLD, BaseColor.WHITE);

        PdfPCell headerCell1 = new PdfPCell(new Phrase("Tháng", tableHeaderFont));
        headerCell1.setBackgroundColor(new BaseColor(0, 75, 155));
        headerCell1.setPadding(8);
        headerCell1.setHorizontalAlignment(Element.ALIGN_CENTER);

        PdfPCell headerCell2 = new PdfPCell(new Phrase("Số lịch hẹn", tableHeaderFont));
        headerCell2.setBackgroundColor(new BaseColor(0, 75, 155));
        headerCell2.setPadding(8);
        headerCell2.setHorizontalAlignment(Element.ALIGN_CENTER);

        dataTable.addCell(headerCell1);
        dataTable.addCell(headerCell2);

        // Thêm dữ liệu vào bảng
        Font tableContentFont = new Font(baseFont, 12, Font.NORMAL);
        int totalAppointments = 0;

        for (AppointmentsByCalendarMonth record : data) {
            String monthName = getMonthName(record.getCalendarMonth());

            PdfPCell cell1 = new PdfPCell(new Phrase(monthName, tableContentFont));
            cell1.setPadding(7);
            cell1.setHorizontalAlignment(Element.ALIGN_CENTER);

            PdfPCell cell2 = new PdfPCell(new Phrase(String.valueOf(record.getTotalAppointments()), tableContentFont));
            cell2.setPadding(7);
            cell2.setHorizontalAlignment(Element.ALIGN_CENTER);

            dataTable.addCell(cell1);
            dataTable.addCell(cell2);

            totalAppointments += record.getTotalAppointments();
        }

        // Thêm dòng tổng
        PdfPCell totalLabelCell = new PdfPCell(new Phrase("Tổng cộng", new Font(baseFont, 12, Font.BOLD)));
        totalLabelCell.setPadding(7);
        totalLabelCell.setHorizontalAlignment(Element.ALIGN_CENTER);

        PdfPCell totalValueCell = new PdfPCell(new Phrase(String.valueOf(totalAppointments), new Font(baseFont, 12, Font.BOLD)));
        totalValueCell.setPadding(7);
        totalValueCell.setHorizontalAlignment(Element.ALIGN_CENTER);

        dataTable.addCell(totalLabelCell);
        dataTable.addCell(totalValueCell);

        document.add(dataTable);

        // Tạo và thêm biểu đồ
        document.add(new Paragraph("Biểu đồ số lượng lịch hẹn theo tháng", boldFont));
        document.add(new Paragraph(" "));

        Image chartImage = createBarChart(data);
        chartImage.setAlignment(Element.ALIGN_CENTER);
        chartImage.scaleToFit(500, 300);
        document.add(chartImage);

        // Thêm nhận xét
        document.add(new Paragraph(" "));
        document.add(new Paragraph("Nhận xét:", boldFont));

        int maxAppointmentMonth = data
            .stream()
            .max((a, b) -> a.getTotalAppointments().compareTo(b.getTotalAppointments()))
            .map(AppointmentsByCalendarMonth::getCalendarMonth)
            .orElse(0);

        int minAppointmentMonth = data
            .stream()
            .filter(a -> a.getTotalAppointments() > 0)
            .min((a, b) -> a.getTotalAppointments().compareTo(b.getTotalAppointments()))
            .map(AppointmentsByCalendarMonth::getCalendarMonth)
            .orElse(0);

        document.add(new Paragraph("- Tháng có lịch hẹn nhiều nhất: " + getMonthName(maxAppointmentMonth), infoFont));
        document.add(new Paragraph("- Tháng có lịch hẹn ít nhất: " + getMonthName(minAppointmentMonth), infoFont));
        document.add(new Paragraph("- Tổng số lịch hẹn trong năm: " + totalAppointments, infoFont));

        document.close();

        return ResponseEntity.ok()
            .contentType(MediaType.APPLICATION_PDF)
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=appointments_by_month.pdf")
            .body(baos.toByteArray());
    }

    // Phương thức tạo biểu đồ
    private Image createBarChart(List<AppointmentsByCalendarMonth> data) throws Exception {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        // Thêm dữ liệu vào dataset cho biểu đồ
        for (AppointmentsByCalendarMonth record : data) {
            dataset.addValue(record.getTotalAppointments(), "Lịch hẹn", getMonthName(record.getCalendarMonth()));
        }

        // Tạo biểu đồ
        JFreeChart chart = ChartFactory.createBarChart(
            "", // Tiêu đề
            "Tháng", // Label trục x
            "Số lịch hẹn", // Label trục y
            dataset,
            PlotOrientation.VERTICAL,
            false, // Hiển thị legend
            true, // Hiển thị tooltip
            false // Hiển thị URLs
        );

        // Chuyển đổi biểu đồ thành hình ảnh
        BufferedImage bufferedImage = chart.createBufferedImage(600, 300);
        ByteArrayOutputStream chartImageStream = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, "png", chartImageStream);

        // Chuyển đổi từ byte array sang Image của iText
        return Image.getInstance(chartImageStream.toByteArray());
    }

    // Phương thức để lấy tên tháng từ số tháng
    private String getMonthName(int month) {
        switch (month) {
            case 1:
                return "1";
            case 2:
                return "2";
            case 3:
                return "3";
            case 4:
                return "4";
            case 5:
                return "5";
            case 6:
                return "6";
            case 7:
                return "7";
            case 8:
                return "8";
            case 9:
                return "9";
            case 10:
                return "10";
            case 11:
                return "11";
            case 12:
                return "12";
            default:
                return "Không xác định";
        }
    }

    // Phương thức hỗ trợ thêm dòng thông tin vào bảng
    private void addInfoRow(PdfPTable table, String label, String value, Font labelFont, Font valueFont) {
        PdfPCell labelCell = new PdfPCell(new Phrase(label, labelFont));
        labelCell.setBorder(Rectangle.NO_BORDER);
        labelCell.setPadding(5);

        PdfPCell valueCell = new PdfPCell(new Phrase(value, valueFont));
        valueCell.setBorder(Rectangle.NO_BORDER);
        valueCell.setPadding(5);

        table.addCell(labelCell);
        table.addCell(valueCell);
    }

    @GetMapping("/revenue-by-month/pdf")
    public ResponseEntity<byte[]> exportRevenueByMonthPdf() throws Exception {
        List<RevenueByMonth> data = reportService.getRevenueByMonthData();

        BaseFont baseFont = BaseFont.createFont("src/main/resources/fonts/arial.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
        Font titleFont = new Font(baseFont, 18, Font.BOLD, new BaseColor(0, 75, 155));
        Font bodyFont = new Font(baseFont, 12, Font.NORMAL);
        Font boldFont = new Font(baseFont, 12, Font.BOLD);
        Font infoFont = new Font(baseFont, 12, Font.NORMAL);

        // Cấu hình tài liệu
        Document document = new Document(PageSize.A4, 36, 36, 72, 72);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter writer = PdfWriter.getInstance(document, baos);

        // Thêm header và footer
        HeaderFooterPageEvent event = new HeaderFooterPageEvent();
        writer.setPageEvent(event);

        document.open();

        // Thêm logo
        byte[] logoBytes = org.apache.commons.io.IOUtils.toByteArray(new ClassPathResource("static/logo.png").getInputStream());
        Image logo = Image.getInstance(logoBytes);
        logo.scaleToFit(150, 150);
        logo.setAlignment(Element.ALIGN_CENTER);
        document.add(logo);

        // Thêm tiêu đề báo cáo
        Paragraph title = new Paragraph("BÁO CÁO DOANH THU THEO THÁNG", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingBefore(30);
        title.setSpacingAfter(20);
        document.add(title);

        // Thêm thông tin báo cáo
        PdfPTable infoTable = new PdfPTable(2);
        infoTable.setWidthPercentage(100);
        infoTable.setWidths(new float[] { 1, 3 });

        LocalDate today = LocalDate.now();
        String formattedDate = today.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));

        addInfoRow(infoTable, "Ngày tạo:", formattedDate, boldFont, infoFont);
        addInfoRow(infoTable, "Người tạo:", "Admin", boldFont, infoFont);
        addInfoRow(infoTable, "Bệnh viện:", "Bệnh viện Đa khoa Quốc tế", boldFont, infoFont);

        infoTable.setSpacingAfter(20);
        document.add(infoTable);

        // Tạo bảng dữ liệu
        PdfPTable dataTable = new PdfPTable(3); // 3 cột: Năm, Tháng, Doanh thu
        dataTable.setWidthPercentage(80);
        dataTable.setHorizontalAlignment(Element.ALIGN_CENTER);
        dataTable.setSpacingBefore(10);
        dataTable.setSpacingAfter(20);

        // Thiết lập header của bảng
        Font tableHeaderFont = new Font(baseFont, 12, Font.BOLD, BaseColor.WHITE);

        PdfPCell headerCell1 = new PdfPCell(new Phrase("Năm", tableHeaderFont));
        headerCell1.setBackgroundColor(new BaseColor(0, 75, 155));
        headerCell1.setPadding(8);
        headerCell1.setHorizontalAlignment(Element.ALIGN_CENTER);

        PdfPCell headerCell2 = new PdfPCell(new Phrase("Tháng", tableHeaderFont));
        headerCell2.setBackgroundColor(new BaseColor(0, 75, 155));
        headerCell2.setPadding(8);
        headerCell2.setHorizontalAlignment(Element.ALIGN_CENTER);

        PdfPCell headerCell3 = new PdfPCell(new Phrase("Doanh thu (VNĐ)", tableHeaderFont));
        headerCell3.setBackgroundColor(new BaseColor(0, 75, 155));
        headerCell3.setPadding(8);
        headerCell3.setHorizontalAlignment(Element.ALIGN_CENTER);

        dataTable.addCell(headerCell1);
        dataTable.addCell(headerCell2);
        dataTable.addCell(headerCell3);

        // Thêm dữ liệu vào bảng
        Font tableContentFont = new Font(baseFont, 12, Font.NORMAL);
        BigDecimal totalRevenue = BigDecimal.ZERO;

        for (RevenueByMonth record : data) {
            PdfPCell cell1 = new PdfPCell(new Phrase(String.valueOf(record.getYear()), tableContentFont));
            cell1.setPadding(7);
            cell1.setHorizontalAlignment(Element.ALIGN_CENTER);

            PdfPCell cell2 = new PdfPCell(new Phrase(getMonthName(record.getMonth()), tableContentFont));
            cell2.setPadding(7);
            cell2.setHorizontalAlignment(Element.ALIGN_CENTER);

            String formattedRevenue = String.format("%,d", record.getTotalRevenue().longValue());
            PdfPCell cell3 = new PdfPCell(new Phrase(formattedRevenue, tableContentFont));
            cell3.setPadding(7);
            cell3.setHorizontalAlignment(Element.ALIGN_RIGHT);

            dataTable.addCell(cell1);
            dataTable.addCell(cell2);
            dataTable.addCell(cell3);

            totalRevenue = totalRevenue.add(record.getTotalRevenue());
        }

        // Thêm dòng tổng
        PdfPCell totalLabelCell1 = new PdfPCell(new Phrase("Tổng cộng", new Font(baseFont, 12, Font.BOLD)));
        totalLabelCell1.setPadding(7);
        totalLabelCell1.setHorizontalAlignment(Element.ALIGN_CENTER);
        totalLabelCell1.setColspan(2);

        String formattedTotalRevenue = String.format("%,d", totalRevenue.longValue());
        PdfPCell totalValueCell = new PdfPCell(new Phrase(formattedTotalRevenue, new Font(baseFont, 12, Font.BOLD)));
        totalValueCell.setPadding(7);
        totalValueCell.setHorizontalAlignment(Element.ALIGN_RIGHT);

        dataTable.addCell(totalLabelCell1);
        dataTable.addCell(totalValueCell);

        document.add(dataTable);

        // Tạo và thêm biểu đồ
        document.add(new Paragraph("Biểu đồ doanh thu theo tháng", boldFont));
        document.add(new Paragraph(" "));

        Image chartImage = createRevenueBarChart(data);
        chartImage.setAlignment(Element.ALIGN_CENTER);
        chartImage.scaleToFit(500, 300);
        document.add(chartImage);

        // Thêm nhận xét
        document.add(new Paragraph(" "));
        document.add(new Paragraph("Nhận xét:", boldFont));

        RevenueByMonth maxRevenue = data.stream().max((a, b) -> a.getTotalRevenue().compareTo(b.getTotalRevenue())).orElse(null);

        RevenueByMonth minRevenue = data
            .stream()
            .filter(r -> r.getTotalRevenue().compareTo(BigDecimal.ZERO) > 0)
            .min((a, b) -> a.getTotalRevenue().compareTo(b.getTotalRevenue()))
            .orElse(null);

        if (maxRevenue != null) {
            document.add(
                new Paragraph(
                    "- Tháng có doanh thu cao nhất: " +
                    getMonthName(maxRevenue.getMonth()) +
                    "/" +
                    maxRevenue.getYear() +
                    " (" +
                    String.format("%,d", maxRevenue.getTotalRevenue().longValue()) +
                    " VNĐ)",
                    infoFont
                )
            );
        }

        if (minRevenue != null) {
            document.add(
                new Paragraph(
                    "- Tháng có doanh thu thấp nhất: " +
                    getMonthName(minRevenue.getMonth()) +
                    "/" +
                    minRevenue.getYear() +
                    " (" +
                    String.format("%,d", minRevenue.getTotalRevenue().longValue()) +
                    " VNĐ)",
                    infoFont
                )
            );
        }

        document.add(new Paragraph("- Tổng doanh thu: " + formattedTotalRevenue + " VNĐ", infoFont));

        // Dự đoán doanh thu 3 tháng tiếp theo
        // In ra 3 dòng để trống điền sau
        document.add(new Paragraph(" "));

        document.add(new Paragraph("Dự đoán doanh thu 3 tháng tiếp theo:", boldFont));
        for (int i = 1; i <= 3; i++) {
            String nextMonth = getMonthName((data.get(data.size() - 1).getMonth() + i) % 12);
            document.add(new Paragraph("- " + nextMonth + ": __________ VNĐ", infoFont));
        }
        document.add(new Paragraph(" ", infoFont));
        document.add(new Paragraph("Nhận xét:", boldFont));
        document.add(
            new Paragraph(
                "- Dựa trên xu hướng doanh thu hiện tại, dự đoán doanh thu trong 3 tháng tới sẽ tăng/giảm tùy thuộc vào các yếu tố như mùa vụ, dịch bệnh, v.v.",
                infoFont
            )
        );

        document.close();

        return ResponseEntity.ok()
            .contentType(MediaType.APPLICATION_PDF)
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=revenue_by_month.pdf")
            .body(baos.toByteArray());
    }

    // Thêm phương thức tạo biểu đồ doanh thu
    private Image createRevenueBarChart(List<RevenueByMonth> data) throws Exception {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        // Thêm dữ liệu vào dataset cho biểu đồ
        for (RevenueByMonth record : data) {
            dataset.addValue(record.getTotalRevenue(), "Doanh thu", getMonthName(record.getMonth()) + "/" + record.getYear());
        }

        // Tạo biểu đồ
        JFreeChart chart = ChartFactory.createBarChart(
            "", // Tiêu đề
            "Tháng/Năm", // Label trục x
            "Doanh thu (VNĐ)", // Label trục y
            dataset,
            PlotOrientation.VERTICAL,
            false, // Hiển thị legend
            true, // Hiển thị tooltip
            false // Hiển thị URLs
        );

        // Chuyển đổi biểu đồ thành hình ảnh
        BufferedImage bufferedImage = chart.createBufferedImage(600, 300);
        ByteArrayOutputStream chartImageStream = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, "png", chartImageStream);

        // Chuyển đổi từ byte array sang Image của iText
        return Image.getInstance(chartImageStream.toByteArray());
    }

    // Thêm endpoint cho báo cáo bệnh theo giới tính
    @GetMapping("/disease-by-gender/pdf")
    public ResponseEntity<byte[]> exportDiseaseByGenderPdf() throws Exception {
        List<DiseaseByGender> data = reportService.getDiseaseByGenderData();

        BaseFont baseFont = BaseFont.createFont("src/main/resources/fonts/arial.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
        Font titleFont = new Font(baseFont, 18, Font.BOLD, new BaseColor(0, 75, 155));
        Font bodyFont = new Font(baseFont, 12, Font.NORMAL);
        Font boldFont = new Font(baseFont, 12, Font.BOLD);
        Font infoFont = new Font(baseFont, 12, Font.NORMAL);

        // Cấu hình tài liệu
        Document document = new Document(PageSize.A4, 36, 36, 72, 72);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter writer = PdfWriter.getInstance(document, baos);

        // Thêm header và footer
        HeaderFooterPageEvent event = new HeaderFooterPageEvent();
        writer.setPageEvent(event);

        document.open();

        // Thêm logo
        byte[] logoBytes = org.apache.commons.io.IOUtils.toByteArray(new ClassPathResource("static/logo.png").getInputStream());
        Image logo = Image.getInstance(logoBytes);
        logo.scaleToFit(150, 150);
        logo.setAlignment(Element.ALIGN_CENTER);
        document.add(logo);

        // Thêm tiêu đề báo cáo
        Paragraph title = new Paragraph("BÁO CÁO BỆNH THEO GIỚI TÍNH", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingBefore(30);
        title.setSpacingAfter(20);
        document.add(title);

        // Thêm thông tin báo cáo
        PdfPTable infoTable = new PdfPTable(2);
        infoTable.setWidthPercentage(100);
        infoTable.setWidths(new float[] { 1, 3 });

        LocalDate today = LocalDate.now();
        String formattedDate = today.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));

        addInfoRow(infoTable, "Ngày tạo:", formattedDate, boldFont, infoFont);
        addInfoRow(infoTable, "Người tạo:", "Admin", boldFont, infoFont);
        addInfoRow(infoTable, "Bệnh viện:", "Bệnh viện Đa khoa Quốc tế", boldFont, infoFont);

        infoTable.setSpacingAfter(20);
        document.add(infoTable);

        // Nhóm dữ liệu theo tên bệnh
        Map<String, Map<String, Integer>> diseaseData = new HashMap<>();
        Set<String> allGenders = new HashSet<>();

        for (DiseaseByGender record : data) {
            String diseaseName = record.getDiseaseName();
            String gender = record.getGender();
            Integer cases = record.getTotalCases();

            diseaseData.putIfAbsent(diseaseName, new HashMap<>());
            diseaseData.get(diseaseName).put(gender, cases);
            allGenders.add(gender);
        }

        List<String> sortedGenders = new ArrayList<>(allGenders);
        Collections.sort(sortedGenders);

        // Tạo bảng dữ liệu
        int numColumns = 2 + sortedGenders.size(); // Disease Name + Gender columns + Total
        PdfPTable dataTable = new PdfPTable(numColumns);
        dataTable.setWidthPercentage(100);
        dataTable.setHorizontalAlignment(Element.ALIGN_CENTER);
        dataTable.setSpacingBefore(10);
        dataTable.setSpacingAfter(20);

        // Thiết lập header của bảng
        Font tableHeaderFont = new Font(baseFont, 12, Font.BOLD, BaseColor.WHITE);

        PdfPCell headerCell1 = new PdfPCell(new Phrase("Tên bệnh", tableHeaderFont));
        headerCell1.setBackgroundColor(new BaseColor(0, 75, 155));
        headerCell1.setPadding(8);
        headerCell1.setHorizontalAlignment(Element.ALIGN_CENTER);
        dataTable.addCell(headerCell1);

        for (String gender : sortedGenders) {
            PdfPCell genderCell = new PdfPCell(new Phrase(gender, tableHeaderFont));
            genderCell.setBackgroundColor(new BaseColor(0, 75, 155));
            genderCell.setPadding(8);
            genderCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            dataTable.addCell(genderCell);
        }

        PdfPCell headerCellTotal = new PdfPCell(new Phrase("Tổng", tableHeaderFont));
        headerCellTotal.setBackgroundColor(new BaseColor(0, 75, 155));
        headerCellTotal.setPadding(8);
        headerCellTotal.setHorizontalAlignment(Element.ALIGN_CENTER);
        dataTable.addCell(headerCellTotal);

        // Thêm dữ liệu vào bảng
        Font tableContentFont = new Font(baseFont, 11);
        Map<String, Integer> totalsByGender = new HashMap<>();
        int grandTotal = 0;

        List<String> sortedDiseases = new ArrayList<>(diseaseData.keySet());
        Collections.sort(sortedDiseases);

        for (String disease : sortedDiseases) {
            PdfPCell cell1 = new PdfPCell(new Phrase(disease, tableContentFont));
            cell1.setPadding(7);
            cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
            dataTable.addCell(cell1);

            int rowTotal = 0;

            for (String gender : sortedGenders) {
                Integer cases = diseaseData.get(disease).getOrDefault(gender, 0);
                rowTotal += cases;
                totalsByGender.put(gender, totalsByGender.getOrDefault(gender, 0) + cases);

                PdfPCell cell = new PdfPCell(new Phrase(String.valueOf(cases), tableContentFont));
                cell.setPadding(7);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                dataTable.addCell(cell);
            }

            grandTotal += rowTotal;

            PdfPCell totalCell = new PdfPCell(new Phrase(String.valueOf(rowTotal), tableContentFont));
            totalCell.setPadding(7);
            totalCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            dataTable.addCell(totalCell);
        }

        // Thêm dòng tổng cộng
        PdfPCell totalLabelCell = new PdfPCell(new Phrase("Tổng cộng", boldFont));
        totalLabelCell.setPadding(7);
        totalLabelCell.setHorizontalAlignment(Element.ALIGN_LEFT);
        dataTable.addCell(totalLabelCell);

        for (String gender : sortedGenders) {
            PdfPCell totalGenderCell = new PdfPCell(new Phrase(String.valueOf(totalsByGender.getOrDefault(gender, 0)), boldFont));
            totalGenderCell.setPadding(7);
            totalGenderCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            dataTable.addCell(totalGenderCell);
        }

        PdfPCell grandTotalCell = new PdfPCell(new Phrase(String.valueOf(grandTotal), boldFont));
        grandTotalCell.setPadding(7);
        grandTotalCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        dataTable.addCell(grandTotalCell);

        document.add(dataTable);

        // Tạo và thêm biểu đồ
        document.add(new Paragraph("Biểu đồ số ca bệnh theo giới tính", boldFont));
        document.add(new Paragraph(" "));

        Image chartImage = createDiseaseByGenderChart(data);
        chartImage.setAlignment(Element.ALIGN_CENTER);
        chartImage.scaleToFit(500, 300);
        document.add(chartImage);

        // Thêm nhận xét
        document.add(new Paragraph(" "));
        document.add(new Paragraph("Nhận xét:", boldFont));

        // Tìm bệnh phổ biến nhất
        String mostCommonDisease = findMostCommonDisease(diseaseData);
        int mostCommonDiseaseCount = calculateTotalCasesForDisease(diseaseData, mostCommonDisease);

        // Tìm giới tính có nhiều ca bệnh nhất
        String mostAffectedGender = findMostAffectedGender(totalsByGender);

        document.add(new Paragraph("- Bệnh phổ biến nhất: " + mostCommonDisease + " (" + mostCommonDiseaseCount + " ca)", infoFont));
        document.add(
            new Paragraph(
                "- Giới tính có nhiều ca bệnh nhất: " +
                mostAffectedGender +
                " (" +
                totalsByGender.getOrDefault(mostAffectedGender, 0) +
                " ca)",
                infoFont
            )
        );
        document.add(new Paragraph("- Tổng số ca bệnh: " + grandTotal + " ca", infoFont));

        document.close();

        return ResponseEntity.ok()
            .contentType(MediaType.APPLICATION_PDF)
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=disease_by_gender.pdf")
            .body(baos.toByteArray());
    }

    // Thêm phương thức phụ trợ để tìm bệnh phổ biến nhất
    private String findMostCommonDisease(Map<String, Map<String, Integer>> diseaseData) {
        String mostCommonDisease = "Không có";
        int maxCases = 0;

        for (Map.Entry<String, Map<String, Integer>> entry : diseaseData.entrySet()) {
            int totalCases = entry.getValue().values().stream().mapToInt(Integer::intValue).sum();
            if (totalCases > maxCases) {
                maxCases = totalCases;
                mostCommonDisease = entry.getKey();
            }
        }

        return mostCommonDisease;
    }

    // Thêm phương thức phụ trợ để tính tổng số ca cho một bệnh
    private int calculateTotalCasesForDisease(Map<String, Map<String, Integer>> diseaseData, String diseaseName) {
        return diseaseData.getOrDefault(diseaseName, Collections.emptyMap()).values().stream().mapToInt(Integer::intValue).sum();
    }

    // Thêm phương thức phụ trợ để tìm giới tính bị ảnh hưởng nhiều nhất
    private String findMostAffectedGender(Map<String, Integer> totalsByGender) {
        return totalsByGender.entrySet().stream().max(Map.Entry.comparingByValue()).map(Map.Entry::getKey).orElse("Không có");
    }

    // Thêm phương thức tạo biểu đồ bệnh theo giới tính
    private Image createDiseaseByGenderChart(List<DiseaseByGender> data) throws Exception {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        // Nhóm dữ liệu theo bệnh và giới tính
        Map<String, Map<String, Integer>> diseaseData = new HashMap<>();

        for (DiseaseByGender record : data) {
            String diseaseName = record.getDiseaseName();
            String gender = record.getGender();
            Integer cases = record.getTotalCases();

            diseaseData.putIfAbsent(diseaseName, new HashMap<>());
            diseaseData.get(diseaseName).put(gender, cases);
        }

        // Thêm dữ liệu vào dataset cho biểu đồ (lấy 5 bệnh phổ biến nhất)
        List<String> topDiseases = diseaseData
            .entrySet()
            .stream()
            .sorted((e1, e2) -> {
                int sum1 = e1.getValue().values().stream().mapToInt(Integer::intValue).sum();
                int sum2 = e2.getValue().values().stream().mapToInt(Integer::intValue).sum();
                return Integer.compare(sum2, sum1); // Giảm dần
            })
            .limit(5)
            .map(Map.Entry::getKey)
            .collect(Collectors.toList());

        for (String disease : topDiseases) {
            for (Map.Entry<String, Integer> entry : diseaseData.get(disease).entrySet()) {
                dataset.addValue(entry.getValue(), entry.getKey(), disease);
            }
        }

        // Tạo biểu đồ
        JFreeChart chart = ChartFactory.createBarChart(
            "Top 5 bệnh phổ biến nhất", // Tiêu đề
            "Tên bệnh", // Label trục x
            "Số ca", // Label trục y
            dataset,
            PlotOrientation.VERTICAL,
            true, // Hiển thị legend
            true, // Hiển thị tooltip
            false // Hiển thị URLs
        );

        // Chuyển đổi biểu đồ thành hình ảnh
        BufferedImage bufferedImage = chart.createBufferedImage(600, 300);
        ByteArrayOutputStream chartImageStream = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, "png", chartImageStream);

        // Chuyển đổi từ byte array sang Image của iText
        return Image.getInstance(chartImageStream.toByteArray());
    }

    // Lớp nội bộ để quản lý header và footer
    class HeaderFooterPageEvent extends PdfPageEventHelper {

        BaseFont baseFont2;
        Font footerFont;

        public HeaderFooterPageEvent() {
            try {
                baseFont2 = BaseFont.createFont("src/main/resources/fonts/arial.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
                footerFont = new Font(baseFont2, 8, Font.ITALIC, BaseColor.GRAY);
            } catch (Exception e) {
                // Fall back to default fonts if there's an error
                footerFont = new Font(Font.FontFamily.HELVETICA, 8, Font.ITALIC, BaseColor.GRAY);
            }
        }

        @Override
        public void onEndPage(PdfWriter writer, Document document) {
            PdfContentByte cb = writer.getDirectContent();

            // Header - vẽ một đường kẻ ở đầu trang
            cb.setLineWidth(1);
            cb.setColorStroke(new BaseColor(0, 75, 155));
            cb.moveTo(document.left(), document.top() + 10);
            cb.lineTo(document.right(), document.top() + 10);
            cb.stroke();

            // Footer - vẽ một đường kẻ ở cuối trang và thêm số trang
            cb.moveTo(document.left(), document.bottom() - 10);
            cb.lineTo(document.right(), document.bottom() - 10);
            cb.stroke();

            // Thêm số trang
            ColumnText.showTextAligned(
                cb,
                Element.ALIGN_CENTER,
                new Phrase("Trang " + writer.getPageNumber(), footerFont),
                (document.right() - document.left()) / 2 + document.leftMargin(),
                document.bottom() - 30,
                0
            );

            // Thêm thông tin footer
            ColumnText.showTextAligned(
                cb,
                Element.ALIGN_LEFT,
                new Phrase("© Bệnh viện Đa khoa Quốc tế", footerFont),
                document.left(),
                document.bottom() - 30,
                0
            );

            ColumnText.showTextAligned(
                cb,
                Element.ALIGN_RIGHT,
                new Phrase("Ngày xuất: " + LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")), footerFont),
                document.right(),
                document.bottom() - 30,
                0
            );
        }
    }
}
