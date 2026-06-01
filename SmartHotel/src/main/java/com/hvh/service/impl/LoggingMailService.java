package com.hvh.service.impl;

import com.hvh.pojo.Payment;
import com.hvh.pojo.Reservation;
import com.hvh.service.MailService;
import java.util.Date;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

@Service
@Primary
public class LoggingMailService implements MailService {

    private static final Logger logger = Logger.getLogger(LoggingMailService.class.getName());

    @Autowired
    @Qualifier("mailServiceImpl")
    private MailService delegate;

    @Override
    public void sendInvoiceEmail(Reservation reservation, Payment payment) {
        logger.info("========================================");
        logger.info("[EMAIL] Bắt đầu gửi email hóa đơn");
        logger.info("[EMAIL] Thời gian: " + new Date());
        logger.info("[EMAIL] Reservation ID: " + reservation.getId());
        logger.info("[EMAIL] Khách hàng: " + reservation.getCreatedBy().getFullName());
        logger.info("[EMAIL] Phương thức: " + payment.getMethod());
        logger.info("[EMAIL] Số tiền: " + payment.getTotalAmount() + " VNĐ");
        logger.info("========================================");

        long start = System.currentTimeMillis();
        try {
            delegate.sendInvoiceEmail(reservation, payment);
            long elapsed = System.currentTimeMillis() - start;
            logger.info("[EMAIL] Gửi THÀNH CÔNG sau " + elapsed + "ms");
        } catch (Exception e) {
            logger.severe("[EMAIL] Gửi THẤT BẠI: " + e.getMessage());
            throw e;
        }
    }
}