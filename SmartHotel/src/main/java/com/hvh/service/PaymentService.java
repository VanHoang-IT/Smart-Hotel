/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hvh.service;

import com.hvh.pojo.Payment;
import java.util.List;
import java.util.Map;

/**
 *
 * @author 03358
 */
public interface PaymentService {
    void addPayment(Map<String, Object> payload);
    List<Payment> getPaymentsByReservation(long resId);
    Map<String, Object> createMoMoPayment(Long reservationId, Long amount) throws Exception;
    void processMoMoPayment(Map<String, Object> callbackData);
}
