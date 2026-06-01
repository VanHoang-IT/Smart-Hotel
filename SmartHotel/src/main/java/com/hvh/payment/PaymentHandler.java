package com.hvh.payment;

import com.hvh.pojo.Payment;
import com.hvh.pojo.Reservation;
import com.hvh.pojo.ReservationRoom;
import com.hvh.pojo.Room;
import com.hvh.repository.PaymentRepository;
import com.hvh.repository.ReservationRepository;
import com.hvh.repository.RoomRepository;
import com.hvh.service.MailService;
import com.hvh.service.ServiceOrderService;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

public abstract class PaymentHandler {

    @Autowired
    protected PaymentRepository paymentRepo;
    @Autowired
    protected ReservationRepository reservationRepo;
    @Autowired
    protected RoomRepository roomRepo;
    @Autowired
    protected MailService mailService;
    @Autowired
    protected ServiceOrderService serviceOrderService;

    public abstract String getMethod();

    public abstract Map<String, Object> createPaymentUrl(Long reservationId) throws Exception;

    public void processCallback(Map<String, Object> callbackData) {
        throw new UnsupportedOperationException("Không hỗ trợ callback");
    }

    public void processReturn(Map<String, String> params) {
        throw new UnsupportedOperationException("Không hỗ trợ return");
    }

    @Transactional
    protected void confirmReservation(Long reservationId, BigDecimal amount, String method, String transactionId) {
        Reservation res = reservationRepo.getReservationById(reservationId);
        if (res != null) {
            res.setStatus("CONFIRMED");
            reservationRepo.addOrUpdateReservation(res);

            Set<ReservationRoom> rooms = res.getReservationRoomSet();
            if (rooms != null && isToday(res.getCheckIn())) {
                for (ReservationRoom rr : rooms) {
                    Room room = rr.getRoomId();
                    room.setStatus("OCCUPIED");
                    roomRepo.addOrUpdateRoom(room);
                }
            }

            Payment p = new Payment();
            p.setReservationId(res);
            p.setTotalAmount(amount);
            p.setMethod(method);
            p.setTransactionId(transactionId);
            p.setStatus("COMPLETED");
            p.setPaidAt(new Date());
            p.setCreatedAt(new Date());
            paymentRepo.addPayment(p);
            mailService.sendInvoiceEmail(res, p);
        }
    }

    protected boolean isToday(Date date) {
        if (date == null) return false;
        LocalDate checkInDate;
        if (date instanceof java.sql.Date) {
            checkInDate = ((java.sql.Date) date).toLocalDate();
        } else {
            checkInDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        }
        return LocalDate.now().equals(checkInDate);
    }
}