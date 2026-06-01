/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hvh.facade;

/**
 *
 * @author 03358
 */
import com.hvh.service.PaymentService;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class BookingFacadeImpl implements BookingFacade {

    @Autowired
    private PaymentService paymentService;

    @Override
    public void processPayment(Map<String, Object> payload) {
        paymentService.addPayment(payload);
    }

    @Override
    public Map<String, Object> processMoMoPayment(Long reservationId, Long amount) throws Exception {
        return paymentService.createMoMoPayment(reservationId, amount);
    }

    @Override
    public Map<String, Object> processVNPayPayment(Long reservationId) throws Exception {
        return paymentService.createVNPayPayment(reservationId);
    }

    @Override
    public void confirmVNPayManual(Long reservationId) {
        paymentService.confirmVNPayManual(reservationId);
    }

    @Override
    public void processMoMoCallback(Map<String, Object> callbackData) {
        paymentService.processMoMoPayment(callbackData);
    }

    @Override
    public void processVNPayReturn(Map<String, String> params) {
        paymentService.processVNPayReturn(params);
    }

    @Override
    public void updatePaymentStatus(long id, String status) {
        paymentService.updateStatus(id, status);
    }
}
