/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hvh.service;

import com.hvh.dto.ReservationRequestDTO;
import com.hvh.dto.ReservationResponseDTO;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Smart Hotel
 */
public interface ReservationService {

    List<ReservationResponseDTO> getReservations(Map<String, String> params);

    ReservationResponseDTO createReservation(ReservationRequestDTO dto);

    ReservationResponseDTO updateReservation(ReservationRequestDTO dto);

    ReservationResponseDTO getReservationById(long id);
    
    ReservationResponseDTO cancelReservation(long id);
}
