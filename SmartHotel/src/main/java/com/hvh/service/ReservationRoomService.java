/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hvh.service;

/**
 *
 * @author 03358
 */
import com.hvh.pojo.ReservationRoom;
import java.util.List;
import java.util.Map;

public interface ReservationRoomService {

    List<ReservationRoom> getReservationRooms(
            Map<String, String> params
    );

    void addOrUpdateReservationRoom(
            ReservationRoom rr
    );

    ReservationRoom getReservationRoomById(long id);
}
