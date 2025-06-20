package com.hospital.web.rest;

import com.hospital.domain.AppointmentsByCalendarMonth;
import com.hospital.service.ReportService;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
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
        logo.scaleToFit(100, 100);
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
