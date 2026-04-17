/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hvh.service.impl;

import com.hvh.pojo.Payment;
import com.hvh.repository.PaymentRepository;
import com.hvh.service.PaymentService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author 03358
 */
@Service
public class PaymentServiceImpl implements PaymentService{
    @Autowired
    private PaymentRepository paymentRepo;
    
    
    @Override
    public void addPayment(Payment p) {
        this.paymentRepo.addPayment(p);
    }

    @Override
    public List<Payment> getPaymentsByReservation(long resId) {
        return this.getPaymentsByReservation(resId);
    }
    
}
