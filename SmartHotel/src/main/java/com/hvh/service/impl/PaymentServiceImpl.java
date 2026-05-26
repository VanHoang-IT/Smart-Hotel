/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hvh.service.impl;

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
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
    private PaymentRepository paymentRepo;
    @Autowired
    private ReservationRepository reservationRepo;
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private RoomRepository roomRepo;
    @Autowired
    private MailService mailService;
    @Autowired
    private ServiceOrderService serviceOrderService;
    
    private final String partnerCode = "MOMO";
    private final String accessKey = "F8BBA842ECF85";
    private final String secretKey = "K951B6PE1waDMi640xX08PD3vg6EkVlz";
    private final String endpoint = "https://test-payment.momo.vn/v2/gateway/api/create";

    @Override
    @Transactional
    public void addPayment(Map<String, Object> payload) {
        Long resId = Long.valueOf(payload.get("reservationId").toString());
        String method = payload.get("method").toString();
        Reservation res = this.reservationRepo.getReservationById(resId);
        if (res != null) {
            BigDecimal amount = this.serviceOrderService.getTotalAmountByReservation(resId);
            Set<ReservationRoom> reservationRooms = res.getReservationRoomSet();

            res.setStatus("PENDING");
            this.reservationRepo.addOrUpdateReservation(res);

            if (reservationRooms != null && isToday(res.getCheckIn())) {
                for (ReservationRoom rr : reservationRooms) {
                    Room room = rr.getRoomId();
                    room.setStatus("OCCUPIED");
                    this.roomRepo.addOrUpdateRoom(room);
                }
            }
            Payment payment = new Payment();
            payment.setTotalAmount(amount);
            payment.setMethod(method);
            payment.setStatus("PENDING"); 
            payment.setCreatedAt(new Date());
            payment.setReservationId(res);
            this.paymentRepo.addPayment(payment);
            this.mailService.sendInvoiceEmail(res, payment);
        }
    }

    @Override
    public List<Payment> getPaymentsByReservation(long resId) {
        return this.paymentRepo.getPaymentsByReservation(resId);
    }

    @Override
    public Map<String, Object> createMoMoPayment(Long reservationId, Long amount) throws Exception {
        BigDecimal calculatedAmount = this.serviceOrderService.getTotalAmountByReservation(reservationId);
        Long payableAmount = calculatedAmount.longValue();
        String orderId = reservationId + "_" + System.currentTimeMillis();
        String requestId = String.valueOf(System.currentTimeMillis());
        String orderInfo = "Thanh toán SmartHotel. Đơn: " + reservationId;
        String redirectUrl = "http://localhost:3000/cart";
        String ipnUrl = "https://why-embody-playful.ngrok-free.dev/SmartHotel/api/public/payments/momo-callback";
        String requestType = "captureWallet";
        String extraData = "";

        String rawHash = "accessKey=" + accessKey
                + "&amount=" + payableAmount
                + "&extraData=" + extraData
                + "&ipnUrl=" + ipnUrl
                + "&orderId=" + orderId
                + "&orderInfo=" + orderInfo
                + "&partnerCode=" + partnerCode
                + "&redirectUrl=" + redirectUrl
                + "&requestId=" + requestId
                + "&requestType=" + requestType;

        String signature = MoMoSecurity.signHmacSHA256(rawHash, secretKey);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("partnerCode", partnerCode);
        requestBody.put("requestId", requestId);
        requestBody.put("amount", payableAmount);
        requestBody.put("orderId", orderId);
        requestBody.put("orderInfo", orderInfo);
        requestBody.put("redirectUrl", redirectUrl);
        requestBody.put("ipnUrl", ipnUrl);
        requestBody.put("extraData", extraData);
        requestBody.put("requestType", requestType);
        requestBody.put("signature", signature);
        requestBody.put("lang", "vi");

        return restTemplate.postForObject(endpoint, requestBody, Map.class);
    }

    @Override
    @Transactional
    public void processMoMoPayment(Map<String, Object> callbackData) {
        if ("0".equals(String.valueOf(callbackData.get("resultCode")))) {
            String orderId = callbackData.get("orderId").toString();
            Long resId = Long.parseLong(orderId.split("_")[0]);
            Reservation res = this.reservationRepo.getReservationById(resId);
            if (res != null) {
                java.util.Set<ReservationRoom> reservationRooms = res.getReservationRoomSet();

                res.setStatus("CONFIRMED");
                this.reservationRepo.addOrUpdateReservation(res);
                if (reservationRooms != null && isToday(res.getCheckIn())) {
                    for (ReservationRoom rr : reservationRooms) {
                        Room room = rr.getRoomId();
                        room.setStatus("OCCUPIED");
                        this.roomRepo.addOrUpdateRoom(room);
                    }
                }
                Payment p = new Payment();
                p.setReservationId(res);
                p.setTotalAmount(new BigDecimal(callbackData.get("amount").toString()));
                p.setMethod("E_WALLET");
                p.setTransactionId(callbackData.get("transId").toString());
                p.setStatus("COMPLETED");
                p.setPaidAt(new Date());
                p.setCreatedAt(new Date());

                this.paymentRepo.addPayment(p);
                this.mailService.sendInvoiceEmail(res, p);
            }
        }
    }

    private boolean isToday(Date date) {
        if (date == null) return false;
        LocalDate checkInDate;
        if (date instanceof java.sql.Date) {
            checkInDate = ((java.sql.Date) date).toLocalDate();
        } else {
            checkInDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        }
        return LocalDate.now().equals(checkInDate);
    }

    @Override
    @Transactional
    public void updateStatus(long id, String status) {
        Payment p = this.paymentRepo.getById(id);
        if (p == null) {
            throw new RuntimeException("Không tìm thấy payment với ID: " + id);
        }
        p.setStatus(status);
        if ("COMPLETED".equals(status)) {
            p.setPaidAt(new Date());
        }
        this.paymentRepo.updatePayment(p);
    }
}
