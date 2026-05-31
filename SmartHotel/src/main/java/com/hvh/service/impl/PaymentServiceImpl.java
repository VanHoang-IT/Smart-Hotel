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

    private final String vnp_TmnCode = "FUWUS60W";
    private final String vnp_HashSecret = "N8VMEABXGLY2RDB48U1I0WIN9GKA8A1U";
    private final String vnp_Url = "https://sandbox.vnpayment.vn/paymentv2/vpcpay.html";
    private final String vnp_ReturnUrl = "http://localhost:3000/cart";

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
        if (date == null) {
            return false;
        }
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

    @Override
    public Map<String, Object> createVNPayPayment(Long reservationId) throws Exception {
        BigDecimal amount = this.serviceOrderService.getTotalAmountByReservation(reservationId);
        long vnpAmount = amount.longValue() * 100;
        String vnp_TxnRef = reservationId + "_" + System.currentTimeMillis();
        String vnp_OrderInfo = "Thanh toan don " + reservationId;

        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyyMMddHHmmss");
        sdf.setTimeZone(java.util.TimeZone.getTimeZone("Asia/Ho_Chi_Minh"));
        String vnp_CreateDate = sdf.format(new Date());

        Map<String, String> params = new TreeMap<>();
        params.put("vnp_Version", "2.1.0");
        params.put("vnp_Command", "pay");
        params.put("vnp_TmnCode", vnp_TmnCode);
        params.put("vnp_Amount", String.valueOf(vnpAmount));
        params.put("vnp_CurrCode", "VND");
        params.put("vnp_TxnRef", vnp_TxnRef);
        params.put("vnp_OrderInfo", vnp_OrderInfo);
        params.put("vnp_OrderType", "other");
        params.put("vnp_Locale", "vn");
        params.put("vnp_ReturnUrl", vnp_ReturnUrl);
        params.put("vnp_IpAddr", "127.0.0.1");
        params.put("vnp_CreateDate", vnp_CreateDate);

        String queryString = VNPaySecurity.buildQueryString(params, vnp_HashSecret);
        String paymentUrl = vnp_Url + "?" + queryString;

        Map<String, Object> result = new HashMap<>();
        result.put("payUrl", paymentUrl);
        return result;
    }

    @Override
    @Transactional
    public void processVNPayReturn(Map<String, String> params) {
        String vnp_ResponseCode = params.get("vnp_ResponseCode");
        if ("00".equals(vnp_ResponseCode)) {
            String txnRef = params.get("vnp_TxnRef");
            Long resId = Long.parseLong(txnRef.split("_")[0]);
            Reservation res = this.reservationRepo.getReservationById(resId);
            if (res != null) {
                res.setStatus("CONFIRMED");
                this.reservationRepo.addOrUpdateReservation(res);

                Set<ReservationRoom> reservationRooms = res.getReservationRoomSet();
                if (reservationRooms != null && isToday(res.getCheckIn())) {
                    for (ReservationRoom rr : reservationRooms) {
                        Room room = rr.getRoomId();
                        room.setStatus("OCCUPIED");
                        this.roomRepo.addOrUpdateRoom(room);
                    }
                }

                Payment p = new Payment();
                p.setReservationId(res);
                p.setTotalAmount(new BigDecimal(params.get("vnp_Amount"))
                        .divide(BigDecimal.valueOf(100)));
                p.setMethod("TRANSFER");
                p.setTransactionId(params.get("vnp_TransactionNo"));
                p.setStatus("COMPLETED");
                p.setPaidAt(new Date());
                p.setCreatedAt(new Date());
                this.paymentRepo.addPayment(p);
                this.mailService.sendInvoiceEmail(res, p);
            }
        }
    }

    @Override
    @Transactional
    public void confirmVNPayManual(Long reservationId) {
        Reservation res = this.reservationRepo.getReservationById(reservationId);
        if (res != null) {
            res.setStatus("CONFIRMED");
            this.reservationRepo.addOrUpdateReservation(res);

            Set<ReservationRoom> reservationRooms = res.getReservationRoomSet();
            if (reservationRooms != null && isToday(res.getCheckIn())) {
                for (ReservationRoom rr : reservationRooms) {
                    Room room = rr.getRoomId();
                    room.setStatus("OCCUPIED");
                    this.roomRepo.addOrUpdateRoom(room);
                }
            }

            BigDecimal amount = this.serviceOrderService.getTotalAmountByReservation(reservationId);
            Payment p = new Payment();
            p.setReservationId(res);
            p.setTotalAmount(amount);
            p.setMethod("TRANSFER");
            p.setTransactionId("VNPAY_" + System.currentTimeMillis());
            p.setStatus("COMPLETED");
            p.setPaidAt(new Date());
            p.setCreatedAt(new Date());
            this.paymentRepo.addPayment(p);
            this.mailService.sendInvoiceEmail(res, p);
        }
    }
}
