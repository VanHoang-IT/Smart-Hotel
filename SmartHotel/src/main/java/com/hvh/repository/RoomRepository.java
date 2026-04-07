/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hvh.repository;

import com.hvh.pojo.Rooms;
import java.util.List;
import java.util.Map;

/**
 *
 * @author 03358
 */
public interface RoomRepository {
    List<Rooms> getRooms(Map<String, String> params);
    void addOrUpdateRoom(Rooms r);
    Rooms getRoomById(int id);
    void deleteRoom(int id);
}
