/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hvh.service;

import com.hvh.pojo.Payment;
import java.util.List;

/**
 *
 * @author 03358
 */
public interface PaymentService {
    void addPayment(Payment p);
    List<Payment> getPaymentsByReservation(long resId);
}
