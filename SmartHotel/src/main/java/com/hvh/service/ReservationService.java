/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hvh.service;

import com.hvh.pojo.Reservation;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Smart Hotel
 */
public interface ReservationService {
    List<Reservation> getReservations(Map<String, String> params);
    void addOrUpdateReservation(Reservation res);
    public Reservation getReservationById(long id);
}
