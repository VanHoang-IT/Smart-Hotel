/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hvh.controllers;

import com.hvh.dto.ReservationRequestDTO;
import com.hvh.dto.ReservationResponseDTO;
import com.hvh.dto.ServiceOrderRequestDTO;
import com.hvh.dto.ServiceOrderResponseDTO;
import com.hvh.service.ReservationService;
import com.hvh.service.ServiceOrderService;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author 03358
 */
@RestController
@RequestMapping("/api")
@CrossOrigin
public class ApiReservationController {

    @Autowired
    private ReservationService resService;

    @Autowired
    private ServiceOrderService serOrderService;

    @GetMapping("/reservations")
    public ResponseEntity<List<ReservationResponseDTO>> list(@RequestParam Map<String, String> params) {
        return new ResponseEntity<>(this.resService.getReservations(params), HttpStatus.OK);
    }

    @PatchMapping("/secure/reservations/{id}/cancel")
    @PreAuthorize("hasAnyAuthority('CUSTOMER', 'STAFF', 'ADMIN')")
    public ResponseEntity<?> cancel(@PathVariable("id") long id) {

        ReservationResponseDTO r = this.resService.getReservationById(id);

        if (r == null) {
            return new ResponseEntity<>("Reservation Not Found", HttpStatus.NOT_FOUND);
        }

        ReservationResponseDTO updated = this.resService.cancelReservation(id);

        return new ResponseEntity<>(updated, HttpStatus.OK);
    }

    @PostMapping("/secure/reservations")
    @PreAuthorize("hasAuthority('CUSTOMER')")
    public ResponseEntity<ReservationResponseDTO> create(@RequestBody ReservationRequestDTO dto) {
        ReservationResponseDTO created = this.resService.createReservation(dto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PostMapping("/secure/reservations/{id}/service-orders")
    @PreAuthorize("hasAuthority('CUSTOMER')")
    @ResponseStatus(HttpStatus.CREATED)
    public void createServiceOrder(@PathVariable("id") Long reservationId, @RequestBody ServiceOrderRequestDTO orderDto) {
        orderDto.setReservationId(reservationId);
        this.serOrderService.addOrUpdate(orderDto);
    }

    @GetMapping("/secure/reservations/{id}/service-orders")
    @PreAuthorize("hasAnyAuthority('CUSTOMER', 'STAFF', 'ADMIN')")
    public ResponseEntity<List<ServiceOrderResponseDTO>> getOrdersByReservation(@PathVariable("id") Long resId) {
        Map<String, String> params = new HashMap<>();
        params.put("reservationId", resId.toString());

        List<ServiceOrderResponseDTO> orders = this.serOrderService.getServiceOrders(params);
        return new ResponseEntity<>(orders, HttpStatus.OK);
    }

    @GetMapping("/secure/reservations/{id}/service-total")
    @PreAuthorize("hasAnyAuthority('STAFF', 'ADMIN')")
    public ResponseEntity<BigDecimal> getTotalServiceAmount(@PathVariable("id") Long resId) {
        BigDecimal total = this.serOrderService.getTotalAmountByReservation(resId);
        return new ResponseEntity<>(total, HttpStatus.OK);
    }
}
