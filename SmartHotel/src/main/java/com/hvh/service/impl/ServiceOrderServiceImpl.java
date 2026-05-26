/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hvh.service.impl;

import com.hvh.dto.ServiceOrderRequestDTO;
import com.hvh.dto.ServiceOrderResponseDTO;
import com.hvh.pojo.Payment;
import com.hvh.pojo.Reservation;
import com.hvh.pojo.ServiceOrder;
import com.hvh.pojo.Services;
import com.hvh.repository.PaymentRepository;
import com.hvh.repository.ReservationRepository;
import com.hvh.repository.ServiceOrderRepository;
import com.hvh.repository.ServiceRepository;
import com.hvh.service.ServiceOrderService;
import java.math.BigDecimal;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HashMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ServiceOrderServiceImpl implements ServiceOrderService {

    @Autowired
    private ServiceOrderRepository serviceOrderRepo;

    @Autowired
    private ServiceRepository serviceRepo;

    @Autowired
    private ReservationRepository resRepo;

    @Autowired
    private PaymentRepository paymentRepo;

    @Override
    @Transactional(readOnly = true)
    public List<ServiceOrderResponseDTO> getServiceOrders(Map<String, String> params) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isPrivileged = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("STAFF") || a.getAuthority().equals("ADMIN"));

        if (!isPrivileged) {
            params.put("currentUsername", auth.getName());
        }

        List<ServiceOrder> orders = this.serviceOrderRepo.getServiceOrders(params);
        return orders.stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void addOrUpdate(ServiceOrderRequestDTO dto) {
        ServiceOrder order;

        if (dto.getId() != null) {
            order = this.serviceOrderRepo.getById(dto.getId());
            if (order == null) {
                throw new RuntimeException("Service Order không tồn tại!");
            }
        } else {
            order = new ServiceOrder();
            order.setOrderedAt(new Date());
            if (order.getStatus() == null) {
                order.setStatus("PENDING");
            }
        }

        if (dto.getReservationId() != null) {
            order.setReservationId(new Reservation(dto.getReservationId()));
        }

        if (dto.getServiceId() != null) {
            Services s = this.serviceRepo.getServiceById(dto.getServiceId());
            if (s != null) {
                order.setServiceId(s);
                order.setUnitPrice(s.getPrice());

                if (dto.getQty() != null) {
                    BigDecimal quantity = new BigDecimal(dto.getQty());
                    order.setAmount(s.getPrice().multiply(quantity));
                }
            }
        }

        if (dto.getQty() != null) {
            order.setQty(dto.getQty());
        }
        if (dto.getNotes() != null) {
            order.setNotes(dto.getNotes());
        }
        if (dto.getStatus() != null) {
            order.setStatus(dto.getStatus());
        }

        order.setUpdatedAt(new Date());
        this.serviceOrderRepo.addOrUpdate(order);

        if (dto.getReservationId() != null) {
            BigDecimal newTotal = getTotalAmountByReservation(dto.getReservationId());
            List<Payment> payments = this.paymentRepo.getPaymentsByReservation(dto.getReservationId());
            if (!payments.isEmpty()) {
                Payment payment = payments.get(0);
                payment.setTotalAmount(newTotal);
                this.paymentRepo.updatePayment(payment);
            }
        }
    }

    @Override
    @Transactional(readOnly = true)
    public ServiceOrderResponseDTO getById(Long id) {
        ServiceOrder order = this.serviceOrderRepo.getById(id);
        return (order != null) ? toResponseDTO(order) : null;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ServiceOrderResponseDTO> getPendingOrders() {
        Map<String, String> params = new HashMap<>();
        params.put("status", "PENDING");
        return this.getServiceOrders(params);
    }

    @Override
    @Transactional
    public void updateStatus(Long id, String status) {
        ServiceOrder order = this.serviceOrderRepo.getById(id);
        if (order != null) {
            order.setStatus(status);
            order.setUpdatedAt(new Date());
            this.serviceOrderRepo.addOrUpdate(order);

            Long resId = order.getReservationId().getId();
            BigDecimal newTotal = getTotalAmountByReservation(resId);
            List<Payment> payments = this.paymentRepo.getPaymentsByReservation(resId);
            if (!payments.isEmpty()) {
                Payment payment = payments.get(0);
                payment.setTotalAmount(newTotal);
                this.paymentRepo.updatePayment(payment);
            }
        } else {
            throw new RuntimeException("Không tìm thấy đơn dịch vụ với ID: " + id);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal getTotalAmountByReservation(Long resId) {

        Reservation res = this.resRepo.getReservationById(resId);
        if (res == null) {
            return BigDecimal.ZERO;
        }

        long diffInMillies = res.getCheckOut().getTime() - res.getCheckIn().getTime();
        long calculatedNights = java.util.concurrent.TimeUnit.DAYS.convert(diffInMillies, java.util.concurrent.TimeUnit.MILLISECONDS);

        final long nights = Math.max(calculatedNights, 1);

        BigDecimal roomTotal = res.getReservationRoomSet().stream()
                .map(rr -> rr.getPricePerNight().multiply(new BigDecimal(nights)))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Map<String, String> params = new HashMap<>();
        params.put("reservationId", resId.toString());
        List<ServiceOrder> orders = this.serviceOrderRepo.getServiceOrders(params);

        BigDecimal serviceTotal = orders.stream()
                .filter(o -> !"CANCELED".equals(o.getStatus()))
                .map(ServiceOrder::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return roomTotal.add(serviceTotal);
    }

    @Override
    @Transactional
    public void cancelOrder(Long id) {
        ServiceOrder order = this.serviceOrderRepo.getById(id);
        if (order != null) {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            boolean isStaffOrAdmin = auth.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("STAFF") || a.getAuthority().equals("ADMIN"));

            if (!isStaffOrAdmin && !"PENDING".equals(order.getStatus())) {
                throw new RuntimeException("Không thể hủy dịch vụ đã thực hiện!");
            }

            order.setStatus("CANCELED");
            order.setUpdatedAt(new Date());
            this.serviceOrderRepo.addOrUpdate(order);
        }
    }

    private ServiceOrderResponseDTO toResponseDTO(ServiceOrder order) {
        ServiceOrderResponseDTO dto = new ServiceOrderResponseDTO();
        dto.setId(order.getId());
        dto.setQty(order.getQty());
        dto.setUnitPrice(order.getUnitPrice());
        dto.setAmount(order.getAmount());
        dto.setOrderedAt(order.getOrderedAt());
        dto.setNotes(order.getNotes());
        dto.setStatus(order.getStatus());

        if (order.getServiceId() != null) {
            dto.setServiceName(order.getServiceId().getName());
        }

        return dto;
    }
}
