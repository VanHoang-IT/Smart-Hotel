/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hvh.service;

/**
 *
 * @author 03358
 */
import java.util.Map;

public interface NotificationService {
    void sendReservationNotification(Long reservationId, String status, Long userId);
}