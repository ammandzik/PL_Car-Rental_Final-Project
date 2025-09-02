package pl.coderslab.carrental.model;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.SecureRandom;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.Base64;
import java.util.Locale;

public class Utils {

    private Utils() {

    }

    private static final SecureRandom RNG = new SecureRandom();
    private static final Base64.Encoder B64URL = Base64.getUrlEncoder().withoutPadding();

    public static String randomBase64Url12() {

        byte[] buf = new byte[9];
        RNG.nextBytes(buf);
        return B64URL.encodeToString(buf);
    }

    public static byte[] buildSimpleInvoice(Long invoiceId, User user, Reservation reservation) throws IOException {

        try (PDDocument document = new PDDocument();
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);

            PDFont fontReg = PDType1Font.HELVETICA;
            PDFont fontBold = PDType1Font.HELVETICA_BOLD;

            float margin = 50f;
            float y = page.getMediaBox().getHeight() - 72f;
            float contentWidth = page.getMediaBox().getWidth() - 2 * margin;

            NumberFormat money = NumberFormat.getCurrencyInstance(new Locale("en", "PL"));
            DateTimeFormatter dateFmt = DateTimeFormatter.ofPattern("dd-MM-yyyy", Locale.ENGLISH);

            try (PDPageContentStream cs = new PDPageContentStream(document, page)) {

                String title = "Invoice #" + invoiceId;
                drawText(cs, fontBold, 18, margin, y, title);

                String issueDate = todayFormatted(dateFmt);
                drawRightAligned(cs, fontReg, 10, margin + contentWidth, y - 2, "Date " + issueDate);

                y -= 20;
                drawSeparator(cs, margin, y, margin + contentWidth);
                y -= 18;

                drawText(cs, fontBold, 12, margin, y, "User");
                y -= 16;

                y = drawKeyValue(cs, fontReg, 11, margin, y, 120,
                        "Name and surname", safe(user.getName()) + " " + safe(user.getSurname()));
                y = drawKeyValue(cs, fontReg, 11, margin, y, 120,
                        "Email", safe(user.getEmail()));
                y = drawKeyValue(cs, fontReg, 11, margin, y, 120,
                        "Phone number", safe(user.getPhone()));

                y -= 12;
                drawSeparator(cs, margin, y, margin + contentWidth);
                y -= 18;

                drawText(cs, fontBold, 12, margin, y, "Reservation");
                y -= 16;

                String dateFrom = formatMaybeTemporal(reservation.getDateFrom(), dateFmt);
                String dateTo = formatMaybeTemporal(reservation.getDateTo(), dateFmt);

                y = drawKeyValue(cs, fontReg, 11, margin, y, 120,
                        "Date", dateFrom + " – " + dateTo);
                y = drawKeyValue(cs, fontReg, 11, margin, y, 120,
                        "Car", safe(reservation.getCar().getBrand().getBrandName()) + " " + safe(reservation.getCar().getModel()));
                y = drawKeyValue(cs, fontBold, 12, margin, y, 120,
                        "Total price in PLN", money.format(reservation.getFinalPrice()));

                y -= 18;
                drawSeparator(cs, margin, y, margin + contentWidth);

                y -= 14;
                drawText(cs, fontReg, 12, margin, Math.max(y, 72),
                        "Thank you for using our service.");

            }

            document.save(outputStream);
            return outputStream.toByteArray();
        }
    }

    private static void drawText(PDPageContentStream cs, PDFont font, float size, float x, float y, String text) throws IOException {
        cs.beginText();
        cs.setFont(font, size);
        cs.newLineAtOffset(x, y);
        cs.showText(stripControls(text));
        cs.endText();
    }

    private static void drawRightAligned(PDPageContentStream cs, PDFont font, float size, float rightX, float y, String text) throws IOException {
        float x = rightX - stringWidth(font, size, text);
        drawText(cs, font, size, x, y, text);
    }

    private static float drawKeyValue(PDPageContentStream cs, PDFont font, float size,
                                      float x, float y, float labelWidth,
                                      String label, String value) throws IOException {
        drawText(cs, font, size, x, y, label + ":");
        drawText(cs, font, size, x + labelWidth, y, value);
        return y - (size + 3);
    }

    private static void drawSeparator(PDPageContentStream cs, float x1, float y, float x2) throws IOException {
        cs.moveTo(x1, y);
        cs.lineTo(x2, y);
        cs.stroke();
    }

    private static float stringWidth(PDFont font, float size, String text) throws IOException {
        return font.getStringWidth(stripControls(text)) / 1000f * size;
    }

    private static String stripControls(String s) {
        return s == null ? "" : s.replaceAll("[\\r\\n\\t\\f\\u0000-\\u001F]", " ");
    }

    private static String safe(Object o) {
        return o == null ? "-" : String.valueOf(o);
    }

    private static String todayFormatted(DateTimeFormatter fmt) {
        try {
            return java.time.LocalDate.now().format(fmt);
        } catch (Exception e) {
            return java.time.LocalDate.now().toString();
        }
    }

    private static String formatMaybeTemporal(Object value, DateTimeFormatter fmt) {
        if (value == null) return "-";
        try {
            if (value instanceof TemporalAccessor ta) return fmt.format(ta);
        } catch (Exception ignored) {
        }
        return String.valueOf(value);
    }
}
