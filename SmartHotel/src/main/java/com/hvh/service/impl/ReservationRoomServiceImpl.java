/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hvh.service.impl;

import com.hvh.pojo.ReservationRoom;
import com.hvh.repository.ReservationRoomRepository;
import com.hvh.service.ReservationRoomService;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReservationRoomServiceImpl
        implements ReservationRoomService {

    @Autowired
    private ReservationRoomRepository rrRepo;

    @Override
    @Transactional(readOnly = true)
    public List<ReservationRoom> getReservationRooms(
            Map<String, String> params
    ) {

        return this.rrRepo
                .getReservationRooms(params);
    }

    @Override
    @Transactional
    public void addOrUpdateReservationRoom(
            ReservationRoom rr
    ) {

        this.rrRepo
                .addOrUpdateReservationRoom(rr);
    }

    @Override
    @Transactional(readOnly = true)
    public ReservationRoom getReservationRoomById(
            long id
    ) {

        return this.rrRepo
                .getReservationRoomById(id);
    }
}
