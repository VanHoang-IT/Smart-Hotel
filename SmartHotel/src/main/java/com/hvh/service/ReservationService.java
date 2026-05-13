/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hvh.service;

import com.hvh.dto.ReservationRequestDTO;
import com.hvh.dto.ReservationResponseDTO;
import com.hvh.pojo.Reservation;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Smart Hotel
 */
public interface ReservationService {

    List<ReservationResponseDTO> getReservations(Map<String, String> params);
    
    Reservation addOrUpdateReservation(ReservationRequestDTO resDto);
    
    ReservationResponseDTO getReservationById(long id);
}
