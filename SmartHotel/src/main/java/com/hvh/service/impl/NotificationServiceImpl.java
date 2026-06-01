/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hvh.service.impl;

/**
 *
 * @author 03358
 */

import com.hvh.service.NotificationService;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class NotificationServiceImpl implements NotificationService {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Override
    public void sendReservationNotification(Long reservationId, String status, Long userId) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("reservationId", reservationId);
        payload.put("status", status);
        payload.put("message", buildMessage(status, reservationId));

        // Gửi tới user cụ thể
        messagingTemplate.convertAndSend("/topic/notifications/" + userId, payload);
    }

    private String buildMessage(String status, Long reservationId) {
        switch (status) {
            case "CONFIRMED": return "Đơn #" + reservationId + " đã được xác nhận!";
            case "CHECKED_IN": return "Đơn #" + reservationId + " đã check-in!";
            case "CHECKED_OUT": return "Đơn #" + reservationId + " đã check-out!";
            case "CANCELLED": return "Đơn #" + reservationId + " đã bị hủy!";
            default: return "Đơn #" + reservationId + " cập nhật trạng thái: " + status;
        }
    }
}