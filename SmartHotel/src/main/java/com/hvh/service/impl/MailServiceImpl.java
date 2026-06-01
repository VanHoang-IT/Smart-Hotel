package com.hvh.service.impl;

import com.hvh.pojo.Payment;
import com.hvh.pojo.Reservation;
import com.hvh.pojo.ReservationRoom;
import com.hvh.pojo.ServiceOrder;
import com.hvh.service.MailService;
import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import java.text.SimpleDateFormat;
import java.util.Properties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

@Service("mailServiceImpl")
@PropertySource("classpath:configs.properties")
public class MailServiceImpl implements MailService {

    @Value("${mail.smtp.host}")
    private String host;
    @Value("${mail.smtp.port}")
    private String port;
    @Value("${mail.username}")
    private String username;
    @Value("${mail.password}")
    private String password;
    @Value("${mail.from}")
    private String from;
    @Value("${mail.from.name}")
    private String fromName;

    @Override
    public void sendInvoiceEmail(Reservation res, Payment payment) {
        // Build dữ liệu TRONG main thread
        try {
            if (res.getCreatedBy() == null
                    || res.getCreatedBy().getEmail() == null
                    || res.getCreatedBy().getEmail().isEmpty()) {
                System.err.println("Bỏ qua gửi mail: email người tạo đơn rỗng (resId=" + res.getId() + ")");
                return;
            }

            String toEmail = res.getCreatedBy().getEmail();
            String subject = "Hóa đơn đặt phòng SmartHotel #" + res.getId();
            String htmlBody = buildHtml(res, payment);

            // Gửi mail trong thread riêng để không block request
            new Thread(() -> doSend(toEmail, subject, htmlBody)).start();
        } catch (Exception ex) {
            System.err.println("Lỗi build nội dung mail: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void doSend(String toEmail, String subject, String htmlBody) {
        try {
            Properties props = new Properties();
            props.put("mail.smtp.host", host);
            props.put("mail.smtp.port", port);
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");

            Session session = Session.getInstance(props, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(username, password);
                }
            });

            MimeMessage msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress(from, fromName, "UTF-8"));
            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            msg.setSubject(subject, "UTF-8");
            msg.setContent(htmlBody, "text/html; charset=UTF-8");

            Transport.send(msg);
        } catch (Exception ex) {
            System.err.println("Gửi mail thất bại: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private String buildHtml(Reservation res, Payment payment) {
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        String checkInStr = df.format(res.getCheckIn());
        String checkOutStr = df.format(res.getCheckOut());

        StringBuilder sb = new StringBuilder();
        sb.append("<div style='font-family:Arial,sans-serif;max-width:600px;margin:auto;'>");
        sb.append("<h2 style='color:#0d6efd;'>HÓA ĐƠN ĐẶT PHÒNG - SmartHotel</h2>");
        sb.append("<p><b>Mã đơn:</b> #").append(res.getId()).append("</p>");
        sb.append("<p><b>Khách hàng:</b> ").append(res.getCreatedBy().getFullName()).append("</p>");
        sb.append("<p><b>Ngày Check-in:</b> ").append(checkInStr).append("</p>");
        sb.append("<p><b>Ngày Check-out:</b> ").append(checkOutStr).append("</p>");

        // Phòng đã đặt
        sb.append("<h3>Phòng đã đặt</h3><ul>");
        if (res.getReservationRoomSet() != null) {
            for (ReservationRoom rr : res.getReservationRoomSet()) {
                String roomName = rr.getRoomId() != null ? rr.getRoomId().getName() : "";
                sb.append("<li>")
                  .append(roomName)
                  .append(" &mdash; ")
                  .append(rr.getPricePerNight())
                  .append(" VNĐ/đêm</li>");
            }
        }
        sb.append("</ul>");

        if (res.getServiceOrderSet() != null && !res.getServiceOrderSet().isEmpty()) {
            sb.append("<h3>Dịch vụ đã đăng ký</h3><ul>");
            for (ServiceOrder so : res.getServiceOrderSet()) {
                String svcName = so.getServiceId() != null ? so.getServiceId().getName() : "";
                sb.append("<li>")
                  .append(svcName)
                  .append(" x ").append(so.getQty())
                  .append(" = ").append(so.getAmount())
                  .append(" VNĐ</li>");
            }
            sb.append("</ul>");
        }

        sb.append("<hr>");
        sb.append("<p><b>Tổng tiền:</b> <span style='color:#dc3545;font-size:18px;'>")
          .append(payment.getTotalAmount()).append(" VNĐ</span></p>");
        sb.append("<p><b>Phương thức:</b> ").append(payment.getMethod()).append("</p>");

        if ("CASH".equalsIgnoreCase(payment.getMethod())) {
            sb.append("<p style='color:#dc3545;font-weight:bold;'>")
              .append("Vui lòng đến quầy lễ tân để thanh toán và nhận phòng vào ngày ")
              .append(checkInStr).append(".</p>");
        } else if ("E_WALLET".equalsIgnoreCase(payment.getMethod())) {
            sb.append("<p style='color:#198754;font-weight:bold;'>")
              .append("Cảm ơn quý khách đã đặt, hãy đến nhận phòng vào ngày ")
              .append(checkInStr).append(".</p>");
        }

        sb.append("</div>");
        return sb.toString();
    }
}
