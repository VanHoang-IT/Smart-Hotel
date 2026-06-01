/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hvh.service.impl;

import com.hvh.payment.PaymentFactory;
import com.hvh.pojo.Payment;
import com.hvh.pojo.Reservation;
import com.hvh.pojo.ReservationRoom;
import com.hvh.pojo.Room;
import com.hvh.repository.PaymentRepository;
import com.hvh.repository.ReservationRepository;
import com.hvh.repository.RoomRepository;
import com.hvh.service.MailService;
import com.hvh.service.PaymentService;
import com.hvh.service.ServiceOrderService;
import com.hvh.utils.MoMoSecurity;
import com.hvh.utils.VNPaySecurity;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

/**
 *
 * @author 03358
 */
@Service
public class PaymentServiceImpl implements PaymentService {

    @Autowired
    private PaymentFactory paymentFactory;
    
    @Autowired
    private PaymentRepository paymentRepo;

    @Override
    public void addPayment(Map<String, Object> payload) {
        String method = payload.get("method").toString();
        paymentFactory.getHandler(method).processCallback(payload);
    }

    @Override
    public Map<String, Object> createMoMoPayment(Long reservationId, Long amount) throws Exception {
        return paymentFactory.getHandler("MOMO").createPaymentUrl(reservationId);
    }

    @Override
    public void processMoMoPayment(Map<String, Object> callbackData) {
        paymentFactory.getHandler("MOMO").processCallback(callbackData);
    }

    @Override
    public Map<String, Object> createVNPayPayment(Long reservationId) throws Exception {
        return paymentFactory.getHandler("VNPAY").createPaymentUrl(reservationId);
    }

    @Override
    public void processVNPayReturn(Map<String, String> params) {
        paymentFactory.getHandler("VNPAY").processReturn(params);
    }

    @Override
    public void confirmVNPayManual(Long reservationId) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("reservationId", reservationId);
        paymentFactory.getHandler("VNPAY").processCallback(payload);
    }

    @Override
    public List<Payment> getPaymentsByReservation(long resId) {
        return paymentRepo.getPaymentsByReservation(resId);
    }

    @Override
    public void updateStatus(long id, String status) {
        Payment p = paymentRepo.getById(id);
        if (p == null) throw new RuntimeException("Không tìm thấy payment với ID: " + id);
        p.setStatus(status);
        if ("COMPLETED".equals(status)) p.setPaidAt(new Date());
        paymentRepo.updatePayment(p);
    }
}