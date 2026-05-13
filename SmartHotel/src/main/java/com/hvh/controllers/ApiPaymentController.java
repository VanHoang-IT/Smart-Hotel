package com.hvh.controllers;

import com.hvh.pojo.Payment;
import com.hvh.service.PaymentService;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
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
    public ResponseEntity<?> createPayment(@RequestBody Map<String, Object> payload) {
        try {
            this.paymentService.addPayment(payload);
            return new ResponseEntity<>(HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/secure/payments/momo-link")
    public ResponseEntity<?> getMoMoLink(@RequestBody Map<String, Object> params) {
        try {
            Long resId = Long.parseLong(params.get("reservationId").toString());
            Long amount = Long.parseLong(params.get("amount").toString());
            Map<String, Object> result = this.paymentService.createMoMoPayment(resId, amount);

            return new ResponseEntity<>(result, HttpStatus.OK);
            
        } catch (org.springframework.web.client.HttpStatusCodeException e) {
            // ĐÂY LÀ LỖI DO MOMO TRẢ VỀ
            String momoError = e.getResponseBodyAsString();
            System.err.println("===== LỖI TỪ MOMO =====");
            System.err.println(momoError);
            System.err.println("=======================");
            return new ResponseEntity<>(momoError, HttpStatus.BAD_REQUEST);
            
        } catch (Exception e) {
            // Lỗi hệ thống nội bộ
            e.printStackTrace();
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/public/payments/momo-callback")
    public ResponseEntity<Void> momoCallback(@RequestBody Map<String, Object> body) {
        System.out.println("\n=========================================");
        System.out.println("🔔 MOMO VỪA GỌI VỀ IPN CALLBACK!");
        System.out.println("Dữ liệu MoMo gửi sang: " + body);
        System.out.println("=========================================\n");
        
        try {
            this.paymentService.processMoMoPayment(body);
            System.out.println("✅ ĐÃ LƯU BẢNG PAYMENT VÀO DATABASE THÀNH CÔNG!");
        } catch (Exception e) {
            System.err.println("❌ LỖI RỒI: KHÔNG THỂ LƯU PAYMENT:");
            e.printStackTrace(); // Lệnh này sẽ in ra nguyên nhân lỗi chi tiết
        }
        
        return ResponseEntity.noContent().build();
    }
}
