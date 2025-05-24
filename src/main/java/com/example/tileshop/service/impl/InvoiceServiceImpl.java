package com.example.tileshop.service.impl;

import com.example.tileshop.entity.Order;
import com.example.tileshop.entity.OrderItem;
import com.example.tileshop.service.InvoiceService;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.OutputStream;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class InvoiceServiceImpl implements InvoiceService {
    private static BaseFont baseFont;
    private static Font normalFontSmall;
    private static Font normalFontMedium;
    private static Font normalFontLarge;
    private static Font boldFontSmall;
    private static Font boldFontMedium;
    private static Font boldFontLarge;
    private static Font italicFontSmall;
    private static Font italicFontMedium;
    private static Font italicFontLarge;
    private static Font boldItalicFontSmall;
    private static Font boldItalicFontMedium;
    private static Font boldItalicFontLarge;
    private static Font headerFont;
    private static Font subHeaderFont;

    static {
        String FONT_PATH = "src/main/resources/fonts/NotoSans-Regular.ttf";

        try {
            baseFont = BaseFont.createFont(FONT_PATH, BaseFont.IDENTITY_H, BaseFont.EMBEDDED);

            // Font thông thường
            normalFontSmall = new Font(baseFont, 10, Font.NORMAL);
            normalFontMedium = new Font(baseFont, 12, Font.NORMAL);
            normalFontLarge = new Font(baseFont, 14, Font.NORMAL);

            // Font đậm
            boldFontSmall = new Font(baseFont, 10, Font.BOLD);
            boldFontMedium = new Font(baseFont, 12, Font.BOLD);
            boldFontLarge = new Font(baseFont, 14, Font.BOLD);

            // Font nghiêng
            italicFontSmall = new Font(baseFont, 10, Font.ITALIC);
            italicFontMedium = new Font(baseFont, 12, Font.ITALIC);
            italicFontLarge = new Font(baseFont, 14, Font.ITALIC);

            // Font vừa đậm vừa nghiêng
            boldItalicFontSmall = new Font(baseFont, 10, Font.BOLDITALIC);
            boldItalicFontMedium = new Font(baseFont, 12, Font.BOLDITALIC);
            boldItalicFontLarge = new Font(baseFont, 14, Font.BOLDITALIC);

            // Font tiêu đề
            headerFont = new Font(baseFont, 16, Font.BOLD);
            subHeaderFont = new Font(baseFont, 14, Font.BOLD);
        } catch (DocumentException | IOException e) {
            log.error("Failed to create font: {}", FONT_PATH);
        }
    }

    @Override
    public void generateInvoice(Order order, OutputStream outputStream) {
        Document document = new Document(PageSize.A4, 50, 50, 50, 50);

        try {
            PdfWriter.getInstance(document, outputStream);
            document.open();

            // ========== PHẦN TIÊU ĐỀ CỬA HÀNG ==========
            PdfPTable headerTable = new PdfPTable(2);
            headerTable.setWidthPercentage(100);

            // Ô bên trái - Tên cửa hàng và thông tin
            PdfPCell shopInfoCell = new PdfPCell();
            shopInfoCell.setBorder(Rectangle.NO_BORDER);

            Paragraph shopName = new Paragraph("GẠCH MEN CAO CẤP HÙNG HƯƠNG", boldFontLarge);
            shopName.setAlignment(Element.ALIGN_LEFT);

            Paragraph shopAddress = new Paragraph("Địa chỉ: 308 Tây Tựu, Bắc Từ Liêm, Hà Nội", normalFontMedium);
            Paragraph shopPhone = new Paragraph("Điện thoại: 0988 027 222", normalFontMedium);

            shopInfoCell.addElement(shopName);
            shopInfoCell.addElement(shopAddress);
            shopInfoCell.addElement(shopPhone);

            // Ô bên phải - Logo
            PdfPCell logoCell = new PdfPCell();
            logoCell.setBorder(Rectangle.NO_BORDER);
            try {
                String logoPath = "src/main/resources/static/images/logo.png";
                Image logo = Image.getInstance(logoPath);
                logo.scaleToFit(100, 100);
                logo.setAlignment(Image.ALIGN_RIGHT);

                logoCell.addElement(logo);
            } catch (Exception e) {
                log.warn("Không thể tải logo, bỏ qua phần logo");
            }

            headerTable.addCell(shopInfoCell);
            headerTable.addCell(logoCell);
            document.add(headerTable);

            document.add(Chunk.NEWLINE);
            // ========== END PHẦN TIÊU ĐỀ ==========

            // Title
            Paragraph title = new Paragraph("HÓA ĐƠN MUA HÀNG", headerFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(20);
            document.add(title);

            // Order Info
            document.add(new Paragraph("Mã hóa đơn: " + order.getId(), boldFontMedium));
            document.add(new Paragraph("Ngày đặt: " + order.getCreatedDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")), normalFontMedium));
            document.add(new Paragraph("Khách hàng: " + order.getRecipientName(), normalFontMedium));
            document.add(new Paragraph("SĐT: " + order.getRecipientPhone(), normalFontMedium));
            document.add(new Paragraph("Địa chỉ: " + order.getShippingAddress(), normalFontMedium));
            document.add(new Paragraph("Phương thức thanh toán: " + order.getPaymentMethod(), normalFontMedium));
            document.add(new Paragraph("Ghi chú: " + (order.getNote() != null ? order.getNote() : ""), normalFontMedium));

            document.add(Chunk.NEWLINE);

            // Table setup
            PdfPTable table = new PdfPTable(5);
            table.setWidthPercentage(100);
            table.setWidths(new float[]{0.8f, 3.5f, 1.2f, 1.5f, 1.5f});

            // Table Header
            addTableHeader(table, "STT", boldFontMedium);
            addTableHeader(table, "Sản phẩm", boldFontMedium);
            addTableHeader(table, "Số lượng", boldFontMedium);
            addTableHeader(table, "Đơn giá", boldFontMedium);
            addTableHeader(table, "Thành tiền", boldFontMedium);

            // Table content
            List<OrderItem> orderItems = order.getOrderItems();
            for (int i = 0; i < orderItems.size(); i++) {
                OrderItem item = orderItems.get(i);

                // Cột STT - căn giữa
                PdfPCell sttCell = new PdfPCell(new Phrase(String.valueOf(i + 1), normalFontMedium));
                sttCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(sttCell);

                // Cột tên sản phẩm - căn trái
                PdfPCell productCell = new PdfPCell(new Phrase(item.getProduct().getName(), normalFontMedium));
                productCell.setHorizontalAlignment(Element.ALIGN_LEFT);
                table.addCell(productCell);

                // Cột số lượng - căn giữa
                PdfPCell qtyCell = new PdfPCell(new Phrase(String.valueOf(item.getQuantity()), normalFontMedium));
                qtyCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(qtyCell);

                // Cột đơn giá - căn phải
                PdfPCell priceCell = new PdfPCell(new Phrase(String.format("%,.0f", item.getPriceAtTimeOfOrder()), normalFontMedium));
                priceCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                table.addCell(priceCell);

                // Cột thành tiền - căn phải
                PdfPCell totalCell = new PdfPCell(new Phrase(String.format("%,.0f", item.getQuantity() * item.getPriceAtTimeOfOrder()), normalFontMedium));
                totalCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                table.addCell(totalCell);
            }

            document.add(table);

            document.add(Chunk.NEWLINE);

            // Total
            Paragraph total = new Paragraph("Tổng tiền: " + String.format("%,.0f VND", order.getTotalAmount()), boldFontLarge);
            total.setAlignment(Element.ALIGN_RIGHT);
            total.setSpacingAfter(20);
            document.add(total);

            // Date
            Paragraph dateParagraph = new Paragraph();
            dateParagraph.setAlignment(Element.ALIGN_RIGHT);
            dateParagraph.add(new Chunk("Ngày ", normalFontMedium));
            dateParagraph.add(new Chunk(order.getCreatedDate().format(DateTimeFormatter.ofPattern("dd")), boldFontMedium));
            dateParagraph.add(new Chunk(" tháng ", normalFontMedium));
            dateParagraph.add(new Chunk(order.getCreatedDate().format(DateTimeFormatter.ofPattern("MM")), boldFontMedium));
            dateParagraph.add(new Chunk(" năm ", normalFontMedium));
            dateParagraph.add(new Chunk(order.getCreatedDate().format(DateTimeFormatter.ofPattern("yyyy")), boldFontMedium));
            dateParagraph.setSpacingAfter(30f);
            document.add(dateParagraph);

            // Sign
            PdfPTable signTable = new PdfPTable(2);
            signTable.setWidthPercentage(100);
            signTable.setSpacingAfter(60f);

            Paragraph customerSign = new Paragraph("Khách hàng", italicFontMedium);
            customerSign.setAlignment(Element.ALIGN_CENTER);

            Paragraph sellerSign = new Paragraph("Người bán", italicFontMedium);
            sellerSign.setAlignment(Element.ALIGN_CENTER);

            signTable.addCell(createSignCell(customerSign));
            signTable.addCell(createSignCell(sellerSign));

            document.add(signTable);

            // Footer
            Paragraph footer = new Paragraph("Cảm ơn quý khách đã sử dụng dịch vụ của chúng tôi!", italicFontMedium);
            footer.setAlignment(Element.ALIGN_CENTER);
            document.add(footer);

            document.close();
        } catch (Exception e) {
            log.error("Unexpected error while generating invoice for order ID: {}. Error: {}", order.getId(), e.getMessage(), e);
        }
    }

    @Override
    public void generateCompanyInvoice(Order order, OutputStream outputStream, String companyName, String companyAddress, String taxCode) {
        Document document = new Document(PageSize.A4, 50, 50, 50, 50);

        try {
            PdfWriter.getInstance(document, outputStream);
            document.open();

            // Header cửa hàng y như cũ
            PdfPTable headerTable = new PdfPTable(2);
            headerTable.setWidthPercentage(100);

            PdfPCell shopInfoCell = new PdfPCell();
            shopInfoCell.setBorder(Rectangle.NO_BORDER);

            Paragraph shopName = new Paragraph("GẠCH MEN CAO CẤP HÙNG HƯƠNG", boldFontLarge);
            shopName.setAlignment(Element.ALIGN_LEFT);

            Paragraph shopAddress = new Paragraph("Địa chỉ: 308 Tây Tựu, Bắc Từ Liêm, Hà Nội", normalFontMedium);
            Paragraph shopPhone = new Paragraph("Điện thoại: 0988 027 222", normalFontMedium);

            shopInfoCell.addElement(shopName);
            shopInfoCell.addElement(shopAddress);
            shopInfoCell.addElement(shopPhone);

            PdfPCell logoCell = new PdfPCell();
            logoCell.setBorder(Rectangle.NO_BORDER);
            try {
                String logoPath = "src/main/resources/static/images/logo.png";
                Image logo = Image.getInstance(logoPath);
                logo.scaleToFit(100, 100);
                logo.setAlignment(Image.ALIGN_RIGHT);
                logoCell.addElement(logo);
            } catch (Exception e) {
                log.warn("Không thể tải logo, bỏ qua phần logo");
            }

            headerTable.addCell(shopInfoCell);
            headerTable.addCell(logoCell);
            document.add(headerTable);

            document.add(Chunk.NEWLINE);

            // Title
            Paragraph title = new Paragraph("HÓA ĐƠN GTGT", headerFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(20);
            document.add(title);

            // Company Info
            document.add(new Paragraph("Tên công ty: " + companyName, boldFontMedium));
            document.add(new Paragraph("Địa chỉ: " + companyAddress, normalFontMedium));
            document.add(new Paragraph("Mã số thuế: " + taxCode, normalFontMedium));
            document.add(new Paragraph("Mã hóa đơn: " + order.getId(), normalFontMedium));
            document.add(new Paragraph("Ngày đặt: " + order.getCreatedDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")), normalFontMedium));

            document.add(Chunk.NEWLINE);

            // Table setup
            PdfPTable table = new PdfPTable(5);
            table.setWidthPercentage(100);
            table.setWidths(new float[]{0.8f, 3.5f, 1.2f, 1.5f, 1.5f});

            // Table Header
            addTableHeader(table, "STT", boldFontMedium);
            addTableHeader(table, "Sản phẩm", boldFontMedium);
            addTableHeader(table, "Số lượng", boldFontMedium);
            addTableHeader(table, "Đơn giá", boldFontMedium);
            addTableHeader(table, "Thành tiền", boldFontMedium);

            // Table content
            List<OrderItem> orderItems = order.getOrderItems();
            for (int i = 0; i < orderItems.size(); i++) {
                OrderItem item = orderItems.get(i);

                // Cột STT - căn giữa
                PdfPCell sttCell = new PdfPCell(new Phrase(String.valueOf(i + 1), normalFontMedium));
                sttCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(sttCell);

                // Cột tên sản phẩm - căn trái
                PdfPCell productCell = new PdfPCell(new Phrase(item.getProduct().getName(), normalFontMedium));
                productCell.setHorizontalAlignment(Element.ALIGN_LEFT);
                table.addCell(productCell);

                // Cột số lượng - căn giữa
                PdfPCell qtyCell = new PdfPCell(new Phrase(String.valueOf(item.getQuantity()), normalFontMedium));
                qtyCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(qtyCell);

                // Cột đơn giá - căn phải
                PdfPCell priceCell = new PdfPCell(new Phrase(String.format("%,.0f", item.getPriceAtTimeOfOrder()), normalFontMedium));
                priceCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                table.addCell(priceCell);

                // Cột thành tiền - căn phải
                PdfPCell totalCell = new PdfPCell(new Phrase(String.format("%,.0f", item.getQuantity() * item.getPriceAtTimeOfOrder()), normalFontMedium));
                totalCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                table.addCell(totalCell);
            }

            document.add(table);

            document.add(Chunk.NEWLINE);

            // Total
            Paragraph total = new Paragraph("Tổng tiền: " + String.format("%,.0f VND", order.getTotalAmount()), boldFontLarge);
            total.setAlignment(Element.ALIGN_RIGHT);
            total.setSpacingAfter(20);
            document.add(total);

            // Date
            Paragraph dateParagraph = new Paragraph();
            dateParagraph.setAlignment(Element.ALIGN_RIGHT);
            dateParagraph.add(new Chunk("Ngày ", normalFontMedium));
            dateParagraph.add(new Chunk(order.getCreatedDate().format(DateTimeFormatter.ofPattern("dd")), boldFontMedium));
            dateParagraph.add(new Chunk(" tháng ", normalFontMedium));
            dateParagraph.add(new Chunk(order.getCreatedDate().format(DateTimeFormatter.ofPattern("MM")), boldFontMedium));
            dateParagraph.add(new Chunk(" năm ", normalFontMedium));
            dateParagraph.add(new Chunk(order.getCreatedDate().format(DateTimeFormatter.ofPattern("yyyy")), boldFontMedium));
            dateParagraph.setSpacingAfter(30f);
            document.add(dateParagraph);

            // Sign
            PdfPTable signTable = new PdfPTable(2);
            signTable.setWidthPercentage(100);
            signTable.setSpacingAfter(60f);

            Paragraph customerSign = new Paragraph("Đại diện bên mua", italicFontMedium);
            customerSign.setAlignment(Element.ALIGN_CENTER);

            Paragraph sellerSign = new Paragraph("Đại diện bên bán", italicFontMedium);
            sellerSign.setAlignment(Element.ALIGN_CENTER);

            signTable.addCell(createSignCell(customerSign));
            signTable.addCell(createSignCell(sellerSign));

            document.add(signTable);

            // Footer
            Paragraph footer = new Paragraph("Cảm ơn Quý công ty đã tin tưởng và hợp tác!", italicFontMedium);
            footer.setAlignment(Element.ALIGN_CENTER);
            document.add(footer);

            document.close();
        } catch (Exception e) {
            log.error("Unexpected error while generating company invoice for order ID: {}. Error: {}", order.getId(), e.getMessage(), e);
        }
    }

    private void addTableHeader(PdfPTable table, String headerTitle, Font font) {
        PdfPCell header = new PdfPCell();
        header.setBackgroundColor(BaseColor.LIGHT_GRAY);
        header.setPhrase(new Phrase(headerTitle, font));
        header.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(header);
    }

    private PdfPCell createSignCell(Paragraph content) {
        PdfPCell cell = new PdfPCell();
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setPaddingTop(30);
        cell.addElement(content);

        Paragraph signatureLabel = new Paragraph("(Ký, ghi rõ họ tên)", italicFontSmall);
        signatureLabel.setAlignment(Element.ALIGN_CENTER);
        cell.addElement(signatureLabel);

        return cell;
    }
}