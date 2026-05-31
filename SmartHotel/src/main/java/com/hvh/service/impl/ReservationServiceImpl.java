package com.hvh.service.impl;

import com.hvh.dto.ReservationDetailDTO;
import com.hvh.dto.ReservationRequestDTO;
import com.hvh.dto.ReservationResponseDTO;
import com.hvh.pojo.CustomerProfile;
import com.hvh.pojo.Payment;
import com.hvh.pojo.Reservation;
import com.hvh.pojo.ReservationRoom;
import com.hvh.pojo.ServiceOrder;
import com.hvh.pojo.User;
import com.hvh.repository.CustomerRepository;
import com.hvh.repository.PaymentRepository;
import com.hvh.repository.ReservationRepository;
import com.hvh.repository.UserRepository;
import com.hvh.service.ReservationService;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.hvh.repository.ReviewRepository;

@Service
public class ReservationServiceImpl implements ReservationService {

    @Autowired
    private ReservationRepository resRepo;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private CustomerRepository customerRepo;

    @Autowired
    private PaymentRepository paymentRepo;

    @Autowired
    private ReviewRepository reviewRepo;

    @Override
    @Transactional(readOnly = true)
    public List<ReservationResponseDTO> getReservations(Map<String, String> params) {
        List<Reservation> reservations = this.resRepo.getReservations(params);
        return reservations.stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ReservationResponseDTO getReservationById(long id) {
        Reservation res = this.resRepo.getReservationById(id);
        return (res != null) ? toResponseDTO(res) : null;
    }

    @Override
    @Transactional
    public Reservation addOrUpdateReservation(ReservationRequestDTO dto) {
        Reservation savedReservation = null;
        if (dto.getId() != null) {
            Reservation res = this.resRepo.getReservationById(dto.getId());
            if (dto.getCheckIn() != null) {
                res.setCheckIn(dto.getCheckIn());
            }
            if (dto.getCheckOut() != null) {
                res.setCheckOut(dto.getCheckOut());
            }
            if (dto.getStatus() != null) {
                res.setStatus(dto.getStatus());
            }
            this.resRepo.addOrUpdateReservation(res);
            savedReservation = res;
        } else {
            if (dto.getCustomerId() == null) {
                throw new RuntimeException("CUSTOMER_ID_REQUIRED");
            }
            User user = this.userRepo.getUserById(dto.getCustomerId());

            if (user == null) {
                throw new RuntimeException("USER_NOT_FOUND");
            }

            CustomerProfile profile = this.customerRepo.getCustomerByUserId(user.getId());
            if (profile == null) {
                throw new IllegalStateException("CUSTOMER_PROFILE_REQUIRED");
            }

            dto.setCustomerId(profile.getId());
            savedReservation = this.resRepo.createReservation(dto);
        }

        return savedReservation;
    }

    @Override
    @Transactional(readOnly = true)
    public ReservationDetailDTO getReservationDetailById(long id) {
        Reservation res = this.resRepo.getReservationById(id);
        if (res == null) {
            return null;
        }

        ReservationDetailDTO dto = new ReservationDetailDTO();
        dto.setId(res.getId());
        dto.setCheckIn(res.getCheckIn());
        dto.setCheckOut(res.getCheckOut());
        dto.setStatus(res.getStatus());
        dto.setCreatedAt(res.getCreatedAt());

        if (res.getCustomerId() != null && res.getCustomerId().getUserId() != null) {
            dto.setCustomerName(res.getCustomerId().getUserId().getFullName());
        }
        if (res.getCreatedBy() != null) {
            dto.setCreatedByName(res.getCreatedBy().getFullName());
        }

        List<ReservationDetailDTO.RoomItem> rooms = new ArrayList<>();
        if (res.getReservationRoomSet() != null) {
            for (ReservationRoom rr : res.getReservationRoomSet()) {
                String roomName = rr.getRoomId() != null ? rr.getRoomId().getName() : null;
                Long roomId = rr.getRoomId() != null ? rr.getRoomId().getId() : null;
                rooms.add(new ReservationDetailDTO.RoomItem(
                        rr.getId(), roomId, roomName, rr.getPricePerNight()));
            }
        }
        dto.setRooms(rooms);

        List<ReservationDetailDTO.ServiceOrderItem> orders = new ArrayList<>();
        if (res.getServiceOrderSet() != null) {
            for (ServiceOrder so : res.getServiceOrderSet()) {
                String svcName = so.getServiceId() != null ? so.getServiceId().getName() : null;
                orders.add(new ReservationDetailDTO.ServiceOrderItem(
                        so.getId(), svcName, so.getQty(),
                        so.getUnitPrice(), so.getAmount(),
                        so.getStatus(), so.getOrderedAt()));
            }
        }
        dto.setServiceOrders(orders);

        List<Payment> payments = this.paymentRepo.getPaymentsByReservation(id);
        List<ReservationDetailDTO.PaymentItem> paymentItems = new ArrayList<>();
        for (Payment p : payments) {
            paymentItems.add(new ReservationDetailDTO.PaymentItem(
                    p.getId(), p.getTotalAmount(), p.getMethod(),
                    p.getStatus(), p.getTransactionId(),
                    p.getPaidAt(), p.getCreatedAt()));
        }
        dto.setPayments(paymentItems);

        return dto;
    }

    @Override
    @Transactional
    public void updateStatus(long id, String status) {
        Reservation res = this.resRepo.getReservationById(id);
        if (res == null) {
            throw new RuntimeException("Không tìm thấy reservation với ID: " + id);
        }
        res.setStatus(status);
        this.resRepo.addOrUpdateReservation(res);
    }

    private ReservationResponseDTO toResponseDTO(Reservation res) {
        ReservationResponseDTO dto = new ReservationResponseDTO();

        dto.setId(res.getId());
        dto.setCheckIn(res.getCheckIn());
        dto.setCheckOut(res.getCheckOut());
        dto.setStatus(res.getStatus());

        if (res.getCustomerId() != null
                && res.getCustomerId().getUserId() != null) {
            dto.setCustomerName(
                    res.getCustomerId()
                            .getUserId()
                            .getFullName());
        }

        if (res.getCreatedBy() != null) {
            dto.setCreatedByName(
                    res.getCreatedBy()
                            .getUsername());
        }

        dto.setReviewed(
                reviewRepo.existsByReservationId(
                        res.getId()));

        return dto;
    }

    @Override
    @Transactional
    public void deleteReservation(long id) {
        this.resRepo.deleteReservation(id);
    }
}
