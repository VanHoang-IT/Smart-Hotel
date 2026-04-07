/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hvh.service;

import com.hvh.pojo.Rooms;
import java.util.List;
import java.util.Map;

/**
 *
 * @author 03358
 */
public interface RoomService {
    List<Rooms> getRooms(Map<String, String> params);
    void addOrUpdateRoom(Rooms r);
    public Rooms getRoomById(int id);
    public void deleteRoom(int id);
}
