/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hvh.controllers;

import com.hvh.dto.ReservationDetailDTO;
import com.hvh.dto.ReservationRequestDTO;
import com.hvh.dto.ReservationResponseDTO;
import com.hvh.dto.ReviewRequestDTO;
import com.hvh.dto.ReviewResponseDTO;
import com.hvh.dto.ServiceOrderRequestDTO;
import com.hvh.dto.ServiceOrderResponseDTO;
import com.hvh.pojo.Reservation;
import com.hvh.pojo.User;
import com.hvh.service.ReservationService;
import com.hvh.service.ReviewService;
import com.hvh.service.ServiceOrderService;
import com.hvh.service.UserService;
import org.springframework.security.core.Authentication;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@CrossOrigin
public class ApiReservationController {

    @Autowired
    private ReservationService resService;

    @Autowired
    private ServiceOrderService serOrderService;

    @Autowired
    private UserService userService;

    @Autowired
    private ReviewService reviewService;

    @PostMapping("/secure/reservations")
    @PreAuthorize("hasAnyAuthority('ROLE_STAFF', 'ROLE_ADMIN', 'ROLE_CUSTOMER', 'RECEPTIONIST')")
    public ResponseEntity<?> createReservation(@RequestBody ReservationRequestDTO dto, Authentication auth) {
        User currentUser = this.userService.getUserByUsername(auth.getName());
        if ("ROLE_CUSTOMER".equals(currentUser.getRole())) {
            dto.setCustomerId(currentUser.getId());
        }
        dto.setCreatedBy(currentUser.getId());
        try {
            Reservation newReservation = this.resService.addOrUpdateReservation(dto);
            Map<String, Long> response = new HashMap<>();
            response.put("id", newReservation.getId());
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (IllegalStateException e) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
    }

    @GetMapping("/secure/reservations")
    @PreAuthorize("hasAnyAuthority('ROLE_STAFF', 'ROLE_ADMIN', 'RECEPTIONIST')")
    public ResponseEntity<List<ReservationResponseDTO>> list(@RequestParam Map<String, String> params) {
        return new ResponseEntity<>(this.resService.getReservations(params), HttpStatus.OK);
    }

    @GetMapping("/secure/reservations/my")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<ReservationResponseDTO>> getMyReservations(Authentication auth) {
        User currentUser = this.userService.getUserByUsername(auth.getName());
        if (currentUser == null || currentUser.getCustomerProfile() == null) {
            return new ResponseEntity<>(Collections.emptyList(), HttpStatus.OK);
        }
        Map<String, String> params = new HashMap<>();
        params.put("customerId", String.valueOf(currentUser.getCustomerProfile().getId()));
        return new ResponseEntity<>(this.resService.getReservations(params), HttpStatus.OK);
    }

    @GetMapping("/secure/reservations/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_CUSTOMER', 'ROLE_STAFF', 'ROLE_ADMIN', 'RECEPTIONIST')")
    public ResponseEntity<ReservationDetailDTO> getById(@PathVariable("id") long id) {
        ReservationDetailDTO r = this.resService.getReservationDetailById(id);
        if (r == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(r, HttpStatus.OK);
    }

    @PatchMapping("/secure/reservations/{id}/status")
    @PreAuthorize("hasAuthority('RECEPTIONIST')")
    public ResponseEntity<?> updateReservationStatus(
            @PathVariable("id") long id,
            @RequestBody Map<String, String> body) {
        String status = body.get("status");
        if (status == null || status.isBlank()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        try {
            this.resService.updateStatus(id, status);
            return new ResponseEntity<>(status, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/secure/reservations/{id}/service-orders")
    @ResponseStatus(HttpStatus.CREATED)
    public void createServiceOrder(@PathVariable("id") Long reservationId, @RequestBody ServiceOrderRequestDTO orderDto) {
        orderDto.setReservationId(reservationId);
        this.serOrderService.addOrUpdate(orderDto);
    }

    @GetMapping("/secure/reservations/{id}/service-orders")
    public ResponseEntity<List<ServiceOrderResponseDTO>> getOrdersByReservation(@PathVariable("id") Long resId) {
        Map<String, String> params = new HashMap<>();
        params.put("reservationId", resId.toString());
        return new ResponseEntity<>(this.serOrderService.getServiceOrders(params), HttpStatus.OK);
    }

    @GetMapping("/secure/reservations/{id}/service-total")
    @PreAuthorize("hasAnyAuthority('ROLE_STAFF', 'ROLE_ADMIN', 'RECEPTIONIST')")
    public ResponseEntity<BigDecimal> getTotalServiceAmount(@PathVariable("id") Long resId) {
        return new ResponseEntity<>(this.serOrderService.getTotalAmountByReservation(resId), HttpStatus.OK);
    }

    @DeleteMapping("/secure/reservations/{id}")
    @PreAuthorize("hasAuthority('RECEPTIONIST')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteReservation(@PathVariable("id") long id) {
        this.resService.deleteReservation(id);
    }

    @PatchMapping("/secure/service-orders/{id}/status")
    @PreAuthorize("hasAuthority('RECEPTIONIST')")
    public ResponseEntity<?> updateServiceOrderStatus(
            @PathVariable("id") Long id,
            @RequestBody Map<String, String> body) {
        String status = body.get("status");
        if (status == null || status.isBlank()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        try {
            this.serOrderService.updateStatus(id, status);
            return new ResponseEntity<>(status, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/secure/reservations/{id}/review")
    @PreAuthorize("hasAuthority('ROLE_CUSTOMER')")
    public ResponseEntity<?> createReview(
            @PathVariable("id") Long reservationId,
            @RequestBody ReviewRequestDTO dto,
            Authentication auth) {
        User currentUser = this.userService.getUserByUsername(auth.getName());
        ReviewResponseDTO review = this.reviewService.addReview(reservationId, dto, currentUser);
        return new ResponseEntity<>(review, HttpStatus.CREATED);
    }
}