/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hvh.service;

import com.hvh.pojo.RoomType;
import java.util.List;
import java.util.Map;

/**
 *
 * @author 03358
 */
public interface RoomTypeService {
    List<RoomType> getType();
    void addOrUpdate(RoomType rt);
    RoomType getRoomTypeById(Long id);
    void deleteRoomType(Long id);
    void updateRoomType(Long id, Map<String, Object> body);
}