/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hvh.repository;

import com.hvh.pojo.RoomType;
import java.util.List;

/**
 *
 * @author 03358
 */
public interface RoomTypeRepository {
    public List<RoomType> getType();
    void addOrUpdate(RoomType rt);
    RoomType getById(Long id);
    void delete(Long id);
}
