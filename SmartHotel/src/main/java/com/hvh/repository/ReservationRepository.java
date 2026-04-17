/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hvh.repository;

import com.hvh.pojo.Reservation;
import java.util.List;
import java.util.Map;

/**
 *
 * @author 03358
 */
public interface ReservationRepository {
    List<Reservation> getReservations(Map<String, String> params);
    void addOrUpdateReservation(Reservation res);
    public Reservation getReservationById(long id);
}
