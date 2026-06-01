package com.hvh.payment;

import com.hvh.pojo.Payment;
import com.hvh.pojo.Reservation;
import com.hvh.pojo.ReservationRoom;
import com.hvh.pojo.Room;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class CashPaymentHandler extends PaymentHandler {

    @Override
    public String getMethod() { return "CASH"; }

    @Override
    public Map<String, Object> createPaymentUrl(Long reservationId) throws Exception {
        throw new UnsupportedOperationException("CASH không cần URL");
    }

    @Override
    @Transactional
    public void processCallback(Map<String, Object> payload) {
        Long resId = Long.valueOf(payload.get("reservationId").toString());
        Reservation res = reservationRepo.getReservationById(resId);
        if (res != null) {
            BigDecimal amount = serviceOrderService.getTotalAmountByReservation(resId);
            Set<ReservationRoom> reservationRooms = res.getReservationRoomSet();

            res.setStatus("PENDING");
            reservationRepo.addOrUpdateReservation(res);

            if (reservationRooms != null && isToday(res.getCheckIn())) {
                for (ReservationRoom rr : reservationRooms) {
                    Room room = rr.getRoomId();
                    room.setStatus("OCCUPIED");
                    roomRepo.addOrUpdateRoom(room);
                }
            }

            Payment payment = new Payment();
            payment.setTotalAmount(amount);
            payment.setMethod("CASH");
            payment.setStatus("PENDING");
            payment.setCreatedAt(new Date());
            payment.setReservationId(res);
            paymentRepo.addPayment(payment);
            mailService.sendInvoiceEmail(res, payment);
        }
    }
}