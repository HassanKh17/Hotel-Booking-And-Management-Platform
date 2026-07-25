package com.hotel.management.system.service;

import com.hotel.management.system.model.Booking;
import com.hotel.management.system.model.BookingItem;
import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.Properties;

@Service
public class BookingEmailService {

    private static final Logger LOGGER = LoggerFactory.getLogger(BookingEmailService.class);
    private static final String BRAND_NAME = "Booking Hotel";
    private static final DateTimeFormatter CALENDAR_DATE_FORMATTER = DateTimeFormatter.BASIC_ISO_DATE;
    private static final DateTimeFormatter CALENDAR_TIMESTAMP_FORMATTER =
            DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss'Z'").withZone(ZoneOffset.UTC);

    private final boolean enabled;
    private final String transport;
    private final String fromAddress;
    private final String elasticEmailApiKey;
    private final String elasticEmailApiUrl;
    private final String host;
    private final int port;
    private final String username;
    private final String password;
    private final boolean auth;
    private final boolean startTlsEnabled;

    public BookingEmailService(
            @Value("${booking.email.enabled:false}") boolean enabled,
            @Value("${booking.email.transport:smtp}") String transport,
            @Value("${booking.email.from}") String fromAddress,
            @Value("${elastic.email.api-key:}") String elasticEmailApiKey,
            @Value("${elastic.email.api-url}") String elasticEmailApiUrl,
            @Value("${spring.mail.host}") String host,
            @Value("${spring.mail.port}") int port,
            @Value("${spring.mail.username:}") String username,
            @Value("${spring.mail.password:}") String password,
            @Value("${spring.mail.properties.mail.smtp.auth:false}") boolean auth,
            @Value("${spring.mail.properties.mail.smtp.starttls.enable:false}") boolean startTlsEnabled) {
        this.enabled = enabled;
        this.transport = transport;
        this.fromAddress = fromAddress;
        this.elasticEmailApiKey = elasticEmailApiKey;
        this.elasticEmailApiUrl = elasticEmailApiUrl;
        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;
        this.auth = auth;
        this.startTlsEnabled = startTlsEnabled;
    }

    public void sendBookingConfirmation(Booking booking) {
        if (!enabled) {
            LOGGER.info("Booking confirmation email skipped for booking {} because booking.email.enabled is false",
                    booking.getId());
            return;
        }

        try {
            if ("elastic-api".equalsIgnoreCase(transport)) {
                sendWithElasticEmailApi(booking);
                return;
            }

            MimeMessage message = new MimeMessage(createMailSession());
            message.setFrom(new InternetAddress(fromAddress));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(booking.getCustomer().getEmail()));
            message.setSubject("Booking confirmation #" + booking.getId());
            message.setContent(buildSmtpContent(booking));

            Transport.send(message);
        } catch (Exception ex) {
            LOGGER.warn("Booking confirmation email could not be sent for booking {}: {}",
                    booking.getId(),
                    ex.getMessage());
            LOGGER.debug("Booking confirmation email failure details", ex);
        }
    }

    private void sendWithElasticEmailApi(Booking booking) throws Exception {
        if (elasticEmailApiKey == null || elasticEmailApiKey.isBlank()) {
            throw new IllegalStateException("Elastic Email API key is missing");
        }

        String requestBody = """
                {
                  "Recipients": {
                    "To": ["%s"]
                  },
                  "Content": {
                    "From": "%s",
                    "Subject": "%s",
                    "Body": [
                      {
                        "ContentType": "HTML",
                        "Charset": "utf-8",
                        "Content": "%s"
                      },
                      {
                        "ContentType": "PlainText",
                        "Charset": "utf-8",
                        "Content": "%s"
                      }
                    ],
                    "Attachments": [
                      {
                        "BinaryContent": "%s",
                        "Name": "%s",
                        "ContentType": "text/calendar; charset=UTF-8; method=PUBLISH"
                      }
                    ]
                  }
                }
                """.formatted(
                escapeJson(booking.getCustomer().getEmail()),
                escapeJson(fromAddress),
                escapeJson("Booking confirmation #" + booking.getId()),
                escapeJson(buildHtmlEmailBody(booking)),
                escapeJson(buildEmailBody(booking)),
                escapeJson(Base64.getEncoder().encodeToString(buildCalendarInvite(booking)
                        .getBytes(StandardCharsets.UTF_8))),
                escapeJson("booking-" + booking.getId() + ".ics"));

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(elasticEmailApiUrl))
                .header("Content-Type", "application/json")
                .header("X-ElasticEmail-ApiKey", elasticEmailApiKey)
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        HttpResponse<String> response = HttpClient.newHttpClient()
                .send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() < 200 || response.statusCode() >= 300) {
            throw new IllegalStateException("Elastic Email API returned HTTP "
                    + response.statusCode()
                    + ": "
                    + response.body());
        }

        LOGGER.info("Booking confirmation email submitted through Elastic Email API for booking {}",
                booking.getId());
    }

    private MimeMultipart buildSmtpContent(Booking booking) throws Exception {
        MimeMultipart mixed = new MimeMultipart("mixed");
        MimeMultipart alternative = new MimeMultipart("alternative");

        MimeBodyPart plainTextPart = new MimeBodyPart();
        plainTextPart.setText(buildEmailBody(booking), StandardCharsets.UTF_8.name());
        alternative.addBodyPart(plainTextPart);

        MimeBodyPart htmlPart = new MimeBodyPart();
        htmlPart.setContent(buildHtmlEmailBody(booking), "text/html; charset=UTF-8");
        alternative.addBodyPart(htmlPart);

        MimeBodyPart alternativePart = new MimeBodyPart();
        alternativePart.setContent(alternative);
        mixed.addBodyPart(alternativePart);

        MimeBodyPart calendarPart = new MimeBodyPart();
        calendarPart.setFileName("booking-" + booking.getId() + ".ics");
        calendarPart.setContent(buildCalendarInvite(booking), "text/calendar; charset=UTF-8; method=PUBLISH");
        mixed.addBodyPart(calendarPart);

        return mixed;
    }

    private Session createMailSession() {
        Properties properties = new Properties();
        properties.put("mail.smtp.host", host);
        properties.put("mail.smtp.port", String.valueOf(port));
        properties.put("mail.smtp.auth", String.valueOf(auth));
        properties.put("mail.smtp.starttls.enable", String.valueOf(startTlsEnabled));
        properties.put("mail.smtp.connectiontimeout", "5000");
        properties.put("mail.smtp.timeout", "5000");
        properties.put("mail.smtp.writetimeout", "5000");

        if (!auth) {
            return Session.getInstance(properties);
        }

        return Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });
    }

    private String buildEmailBody(Booking booking) {
        StringBuilder body = new StringBuilder();
        body.append("Hello ").append(booking.getCustomer().getUsername()).append(",\n\n");
        body.append("Your booking has been confirmed.\n\n");
        body.append("Booking ID: ").append(booking.getId()).append("\n");
        body.append("Created: ").append(booking.getCreatedAt()).append("\n\n");
        body.append("Booked rooms:\n");

        for (BookingItem item : booking.getItems()) {
            long nights = ChronoUnit.DAYS.between(item.getCheckIn(), item.getCheckOut());
            body.append("- ")
                    .append(item.getRoom().getHotel().getName())
                    .append(", room #")
                    .append(item.getRoom().getRoomId())
                    .append(", ")
                    .append(item.getCheckIn())
                    .append(" to ")
                    .append(item.getCheckOut())
                    .append(" (")
                    .append(nights)
                    .append(nights == 1 ? " night" : " nights")
                    .append("): GBP ")
                    .append(item.getPrice())
                    .append("\n");
        }

        body.append("\nTotal paid: GBP ").append(booking.getTotalCost()).append("\n\n");
        body.append("Thank you for booking with us.");

        return body.toString();
    }

    private String buildHtmlEmailBody(Booking booking) {
        StringBuilder roomCards = new StringBuilder();
        String heroImage = "";

        for (BookingItem item : booking.getItems()) {
            if (heroImage.isBlank() && isSafeImageUrl(item.getRoom().getHotel().getPhotoUrl())) {
                heroImage = """
                        <img src="%s" alt="%s" style="display:block;width:100%%;max-height:220px;object-fit:cover;border-radius:0 0 22px 22px;">
                        """.formatted(
                        htmlEscape(item.getRoom().getHotel().getPhotoUrl()),
                        htmlEscape(item.getRoom().getHotel().getName()));
            }

            long nights = ChronoUnit.DAYS.between(item.getCheckIn(), item.getCheckOut());
            roomCards.append("""
                    <tr>
                      <td style="padding:0 0 14px 0;">
                        <table role="presentation" width="100%%" cellspacing="0" cellpadding="0" style="border:1px solid #e7edf7;border-radius:18px;background:#ffffff;">
                          <tr>
                            <td style="padding:18px 20px;">
                              <div style="font-size:20px;line-height:26px;font-weight:800;color:#17213a;">%s</div>
                              <div style="margin-top:6px;font-size:14px;line-height:20px;color:#65738a;">Room #%s | %s %s</div>
                              <table role="presentation" width="100%%" cellspacing="0" cellpadding="0" style="margin-top:16px;">
                                <tr>
                                  <td style="width:33.33%%;padding:10px 10px 10px 0;border-top:1px solid #eef2f8;">
                                    <div style="font-size:12px;text-transform:uppercase;letter-spacing:.08em;color:#8490a5;">Check-in</div>
                                    <div style="margin-top:4px;font-size:15px;font-weight:700;color:#17213a;">%s</div>
                                  </td>
                                  <td style="width:33.33%%;padding:10px;border-top:1px solid #eef2f8;">
                                    <div style="font-size:12px;text-transform:uppercase;letter-spacing:.08em;color:#8490a5;">Check-out</div>
                                    <div style="margin-top:4px;font-size:15px;font-weight:700;color:#17213a;">%s</div>
                                  </td>
                                  <td style="width:33.33%%;padding:10px 0 10px 10px;border-top:1px solid #eef2f8;text-align:right;">
                                    <div style="font-size:12px;text-transform:uppercase;letter-spacing:.08em;color:#8490a5;">Room total</div>
                                    <div style="margin-top:4px;font-size:18px;font-weight:800;color:#176b59;">GBP %s</div>
                                  </td>
                                </tr>
                              </table>
                            </td>
                          </tr>
                        </table>
                      </td>
                    </tr>
                    """.formatted(
                    htmlEscape(item.getRoom().getHotel().getName()),
                    htmlEscape(String.valueOf(item.getRoom().getRoomId())),
                    nights,
                    nights == 1 ? "night" : "nights",
                    htmlEscape(String.valueOf(item.getCheckIn())),
                    htmlEscape(String.valueOf(item.getCheckOut())),
                    htmlEscape(formatMoney(item.getPrice()))));
        }

        return """
                <!doctype html>
                <html>
                  <body style="margin:0;padding:0;background:#f4f7fb;font-family:Arial,Helvetica,sans-serif;color:#1c2536;">
                    <table role="presentation" width="100%%" cellspacing="0" cellpadding="0" style="background:#f4f7fb;padding:28px 12px;">
                      <tr>
                        <td align="center">
                          <table role="presentation" width="100%%" cellspacing="0" cellpadding="0" style="max-width:680px;background:#ffffff;border-radius:24px;overflow:hidden;box-shadow:0 18px 45px rgba(20,38,78,.12);">
                            <tr>
                              <td style="background:#17324d;background-image:linear-gradient(135deg,#17324d,#245b7a 52%%,#14a085);padding:34px 34px 28px 34px;color:#ffffff;">
                                <div style="font-size:14px;font-weight:800;letter-spacing:.12em;text-transform:uppercase;opacity:.9;">%s</div>
                                <h1 style="margin:18px 0 8px 0;font-size:38px;line-height:44px;font-weight:900;">Booking confirmed</h1>
                                <p style="margin:0;font-size:17px;line-height:25px;color:#e4fbf5;">Your stay is reserved. We have attached a calendar invite so the dates are ready to save.</p>
                              </td>
                            </tr>
                            %s
                            <tr>
                              <td style="padding:30px 34px 34px 34px;">
                                <p style="margin:0 0 18px 0;font-size:18px;line-height:28px;">Hello <strong>%s</strong>,</p>
                                <p style="margin:0 0 24px 0;font-size:16px;line-height:25px;color:#4d5d73;">Thank you for booking with us. Here are the details for your confirmed reservation.</p>
                                <table role="presentation" width="100%%" cellspacing="0" cellpadding="0" style="margin:0 0 24px 0;">
                                  <tr>
                                    <td style="padding:16px;border-radius:16px;background:#f0f7ff;">
                                      <div style="font-size:12px;text-transform:uppercase;letter-spacing:.08em;color:#55708f;">Booking ID</div>
                                      <div style="margin-top:5px;font-size:24px;font-weight:900;color:#17324d;">#%s</div>
                                    </td>
                                    <td style="width:14px;"></td>
                                    <td style="padding:16px;border-radius:16px;background:#ecfbf6;">
                                      <div style="font-size:12px;text-transform:uppercase;letter-spacing:.08em;color:#587c72;">Created</div>
                                      <div style="margin-top:5px;font-size:18px;font-weight:800;color:#176b59;">%s</div>
                                    </td>
                                  </tr>
                                </table>
                                <h2 style="margin:0 0 14px 0;font-size:24px;line-height:30px;color:#17213a;">Your room details</h2>
                                <table role="presentation" width="100%%" cellspacing="0" cellpadding="0">
                                  %s
                                </table>
                                <table role="presentation" width="100%%" cellspacing="0" cellpadding="0" style="margin-top:12px;border-radius:20px;background:#17213a;color:#ffffff;">
                                  <tr>
                                    <td style="padding:22px 24px;">
                                      <div style="font-size:13px;text-transform:uppercase;letter-spacing:.1em;color:#99ead9;">Total paid</div>
                                      <div style="margin-top:6px;font-size:32px;line-height:38px;font-weight:900;">GBP %s</div>
                                    </td>
                                  </tr>
                                </table>
                                <table role="presentation" width="100%%" cellspacing="0" cellpadding="0" style="margin-top:28px;border-top:1px solid #e7edf7;">
                                  <tr>
                                    <td style="padding-top:22px;">
                                      <div style="font-size:18px;font-weight:900;color:#17324d;">%s</div>
                                      <div style="margin-top:6px;font-size:14px;line-height:22px;color:#65738a;">Comfortable stays, clear bookings, and support when you need it.</div>
                                      <div style="margin-top:14px;font-size:13px;line-height:20px;color:#8793a6;">This email confirms your reservation and includes a calendar invite attachment for your stay.</div>
                                    </td>
                                  </tr>
                                </table>
                              </td>
                            </tr>
                          </table>
                        </td>
                      </tr>
                    </table>
                  </body>
                </html>
                """.formatted(
                htmlEscape(BRAND_NAME),
                heroImage.isBlank() ? "" : "<tr><td>" + heroImage + "</td></tr>",
                htmlEscape(booking.getCustomer().getUsername()),
                htmlEscape(String.valueOf(booking.getId())),
                htmlEscape(String.valueOf(booking.getCreatedAt())),
                roomCards,
                htmlEscape(formatMoney(booking.getTotalCost())),
                htmlEscape(BRAND_NAME));
    }

    private String buildCalendarInvite(Booking booking) {
        BookingItem firstItem = booking.getItems().iterator().next();
        String hotelName = firstItem.getRoom().getHotel().getName();
        LocalDate checkIn = firstItem.getCheckIn();
        LocalDate checkOut = firstItem.getCheckOut();
        String timestamp = CALENDAR_TIMESTAMP_FORMATTER.format(Instant.now());

        StringBuilder description = new StringBuilder();
        description.append("Booking ID: ").append(booking.getId()).append("\n");
        description.append("Total paid: GBP ").append(formatMoney(booking.getTotalCost())).append("\n\n");
        description.append("Booked rooms:\n");

        for (BookingItem item : booking.getItems()) {
            long nights = ChronoUnit.DAYS.between(item.getCheckIn(), item.getCheckOut());
            description.append("- ")
                    .append(item.getRoom().getHotel().getName())
                    .append(", room #")
                    .append(item.getRoom().getRoomId())
                    .append(", ")
                    .append(item.getCheckIn())
                    .append(" to ")
                    .append(item.getCheckOut())
                    .append(" (")
                    .append(nights)
                    .append(nights == 1 ? " night" : " nights")
                    .append("): GBP ")
                    .append(formatMoney(item.getPrice()))
                    .append("\n");
        }

        return """
                BEGIN:VCALENDAR
                VERSION:2.0
                PRODID:-//Booking Hotel//Booking Confirmation//EN
                CALSCALE:GREGORIAN
                METHOD:PUBLISH
                BEGIN:VEVENT
                UID:booking-%s@booking-hotel.local
                DTSTAMP:%s
                DTSTART;VALUE=DATE:%s
                DTEND;VALUE=DATE:%s
                SUMMARY:%s
                LOCATION:%s
                DESCRIPTION:%s
                END:VEVENT
                END:VCALENDAR
                """.formatted(
                escapeCalendarText(String.valueOf(booking.getId())),
                timestamp,
                CALENDAR_DATE_FORMATTER.format(checkIn),
                CALENDAR_DATE_FORMATTER.format(checkOut),
                escapeCalendarText("Hotel stay: " + hotelName),
                escapeCalendarText(buildAddress(firstItem)),
                escapeCalendarText(description.toString()))
                .replace("\n", "\r\n");
    }

    private String buildAddress(BookingItem item) {
        if (item.getRoom().getHotel().getAddress() == null) {
            return item.getRoom().getHotel().getName();
        }

        StringBuilder address = new StringBuilder();
        appendAddressPart(address, item.getRoom().getHotel().getAddress().getStreet());
        appendAddressPart(address, item.getRoom().getHotel().getAddress().getCity());
        appendAddressPart(address, item.getRoom().getHotel().getAddress().getPostcode());

        return address.isEmpty() ? item.getRoom().getHotel().getName() : address.toString();
    }

    private void appendAddressPart(StringBuilder address, String part) {
        if (part == null || part.isBlank()) {
            return;
        }

        if (!address.isEmpty()) {
            address.append(", ");
        }

        address.append(part);
    }

    private boolean isSafeImageUrl(String url) {
        if (url == null || url.isBlank()) {
            return false;
        }

        String normalizedUrl = url.toLowerCase();
        return normalizedUrl.startsWith("https://") || normalizedUrl.startsWith("http://");
    }

    private String formatMoney(BigDecimal value) {
        return value == null ? "0.00" : value.setScale(2, RoundingMode.HALF_UP).toPlainString();
    }

    private String htmlEscape(String value) {
        if (value == null) {
            return "";
        }

        return value
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }

    private String escapeCalendarText(String value) {
        if (value == null) {
            return "";
        }

        return value
                .replace("\\", "\\\\")
                .replace(";", "\\;")
                .replace(",", "\\,")
                .replace("\r\n", "\\n")
                .replace("\n", "\\n")
                .replace("\r", "\\n");
    }

    private String escapeJson(String value) {
        if (value == null) {
            return "";
        }

        StringBuilder escaped = new StringBuilder();
        for (int i = 0; i < value.length(); i++) {
            char character = value.charAt(i);
            switch (character) {
                case '"' -> escaped.append("\\\"");
                case '\\' -> escaped.append("\\\\");
                case '\b' -> escaped.append("\\b");
                case '\f' -> escaped.append("\\f");
                case '\n' -> escaped.append("\\n");
                case '\r' -> escaped.append("\\r");
                case '\t' -> escaped.append("\\t");
                default -> {
                    if (character < 0x20) {
                        escaped.append(String.format("\\u%04x", (int) character));
                    } else {
                        escaped.append(character);
                    }
                }
            }
        }

        return escaped.toString();
    }
}
