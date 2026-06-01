/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hvh.facade;

import java.util.Map;

public interface BookingFacade {
    void processPayment(Map<String, Object> payload);
    Map<String, Object> processMoMoPayment(Long reservationId, Long amount) throws Exception;
    Map<String, Object> processVNPayPayment(Long reservationId) throws Exception;
    void confirmVNPayManual(Long reservationId);
    void processMoMoCallback(Map<String, Object> callbackData);
    void processVNPayReturn(Map<String, String> params);
    void updatePaymentStatus(long id, String status);
}
