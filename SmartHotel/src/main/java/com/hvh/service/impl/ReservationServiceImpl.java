/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hvh.service.impl;

import com.hvh.pojo.Reservation;
import com.hvh.repository.ReservationRepository;
import com.hvh.service.ReservationService;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author 03358
 */
@Service
public class ReservationServiceImpl implements ReservationService{
    @Autowired
    private ReservationRepository ResRepo;
            
    @Override
    public List<Reservation> getReservations(Map<String, String> params) {
        return this.ResRepo.getReservations(params);
    }

    @Override
    public void addOrUpdateReservation(Reservation res) {
        this.ResRepo.addOrUpdateReservation(res);
    }

    @Override
    public Reservation getReservationById(long id) {
        return this.ResRepo.getReservationById(id);
    }
    
}
