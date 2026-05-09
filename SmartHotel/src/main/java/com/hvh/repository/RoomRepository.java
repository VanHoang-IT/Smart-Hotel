/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hvh.repository;

import com.hvh.pojo.Room;
import java.util.List;
import java.util.Map;

/**
 *
 * @author 03358
 */
public interface RoomRepository {
    List<Room> getRoom(Map<String, String> params);
    void addOrUpdateRoom(Room r);
    Room getRoomById(long id);
    void deleteRoom(long id);
    List<Room> getRoomAvailable();
}