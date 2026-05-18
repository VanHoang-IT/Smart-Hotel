package com.hvh.controllers;

import com.hvh.pojo.Payment;
import com.hvh.service.PaymentService;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
/**
 *
 * @author 03358
 */
@RestController
@RequestMapping("/api")
@CrossOrigin
public class ApiPaymentController {

    @Autowired
    private PaymentService paymentService;

    @PostMapping("/secure/payments")
    @PreAuthorize("hasAnyAuthority('ROLE_STAFF', 'ROLE_ADMIN', 'RECEPTIONIST')")
    public ResponseEntity<?> createPayment(@RequestBody Map<String, Object> payload) {
        try {
            this.paymentService.addPayment(payload);
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
            Map<String, Object> result = this.paymentService.createMoMoPayment(resId, amount);

            return new ResponseEntity<>(result, HttpStatus.OK);
            
        } catch (org.springframework.web.client.HttpStatusCodeException e) {
            String momoError = e.getResponseBodyAsString();
            return new ResponseEntity<>(momoError, HttpStatus.BAD_REQUEST);
            
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/public/payments/momo-callback")
    public ResponseEntity<Void> momoCallback(@RequestBody Map<String, Object> body) {
        try {
            this.paymentService.processMoMoPayment(body);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ResponseEntity.noContent().build();
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
            this.paymentService.updateStatus(id, status);
            return new ResponseEntity<>(status, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }
}
