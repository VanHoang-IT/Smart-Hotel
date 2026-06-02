/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hvh.service;

import com.hvh.pojo.Room;
import java.util.List;
import java.util.Map;

/**
 *
 * @author 03358
 */
public interface RoomService {
    List<Room> getRooms(Map<String, String> params);
    void addOrUpdateRoom(Room r);
    public Room getRoomById(long id);
    public void deleteRoom(long id);
    List<Room> getRoomAvailable(String checkIn, String checkOut);
    List<Map<String, Object>> getRoomBookings(int roomId);
    void createRoom(Map<String, Object> body);
    void updateRoom(Long id, Map<String, Object> body);
}