package com.hvh.controllers;


import com.hvh.pojo.Payment;
import com.hvh.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
    @ResponseStatus (HttpStatus.CREATED)
    public void createPayment(@RequestBody Payment payment){
        this.paymentService.addPayment(payment);
    }
}
