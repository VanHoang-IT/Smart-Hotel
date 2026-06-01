package com.hvh.controllers;

import com.hvh.facade.BookingFacade;
import jakarta.servlet.http.HttpServletResponse;
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
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@CrossOrigin
public class ApiPaymentController {

    @Autowired
    private BookingFacade bookingFacade;

    @PostMapping("/secure/payments")
    @PreAuthorize("hasAnyAuthority('ROLE_CUSTOMER', 'ROLE_STAFF', 'ROLE_ADMIN', 'RECEPTIONIST')")
    public ResponseEntity<?> createPayment(@RequestBody Map<String, Object> payload) {
        try {
            bookingFacade.processPayment(payload);
            return new ResponseEntity<>(HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/secure/payments/momo-link")
    @PreAuthorize("hasAnyAuthority('ROLE_CUSTOMER', 'ROLE_STAFF', 'ROLE_ADMIN', 'RECEPTIONIST')")
    public ResponseEntity<?> getMoMoLink(@RequestBody Map<String, Object> params) {
        try {
            Long resId = Long.parseLong(params.get("reservationId").toString());
            Long amount = Long.parseLong(params.get("amount").toString());
            Map<String, Object> result = bookingFacade.processMoMoPayment(resId, amount);
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/public/payments/momo-callback")
    public ResponseEntity<Void> momoCallback(@RequestBody Map<String, Object> body) {
        try {
            bookingFacade.processMoMoCallback(body);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/secure/payments/vnpay-link")
    @PreAuthorize("hasAnyAuthority('ROLE_CUSTOMER', 'ROLE_STAFF', 'ROLE_ADMIN', 'RECEPTIONIST')")
    public ResponseEntity<?> getVNPayLink(@RequestBody Map<String, Object> params) {
        try {
            Long resId = Long.parseLong(params.get("reservationId").toString());
            Map<String, Object> result = bookingFacade.processVNPayPayment(resId);
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/public/payments/vnpay-return")
    public void vnpayReturn(
            @RequestParam Map<String, String> params,
            HttpServletResponse response) throws Exception {
        bookingFacade.processVNPayReturn(params);
        response.sendRedirect("http://localhost:3000/cart?payment=vnpay-success");
    }

    @GetMapping("/public/payments/vnpay-ipn")
    public ResponseEntity<String> vnpayIpn(@RequestParam Map<String, String> params) {
        try {
            bookingFacade.processVNPayReturn(params);
            return new ResponseEntity<>("00", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("99", HttpStatus.BAD_REQUEST);
        }
    }

    @PatchMapping("/secure/payments/{id}/status")
    @PreAuthorize("hasAuthority('RECEPTIONIST')")
    public ResponseEntity<String> updatePaymentStatus(
            @PathVariable("id") long id,
            @RequestBody Map<String, String> body) {
        String status = body.get("status");
        if (status == null || status.isBlank()) {
            return new ResponseEntity<>("Status is required", HttpStatus.BAD_REQUEST);
        }
        try {
            bookingFacade.updatePaymentStatus(id, status);
            return new ResponseEntity<>(status, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/secure/payments/vnpay-confirm")
    @PreAuthorize("hasAnyAuthority('ROLE_CUSTOMER', 'ROLE_STAFF', 'ROLE_ADMIN', 'RECEPTIONIST')")
    public ResponseEntity<?> confirmVNPay(@RequestBody Map<String, Object> params) {
        try {
            Long resId = Long.parseLong(params.get("reservationId").toString());
            bookingFacade.confirmVNPayManual(resId);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}
