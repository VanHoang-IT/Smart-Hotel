/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hvh.service.impl;

import com.hvh.pojo.RoomType;
import com.hvh.repository.RoomTypeRepository;
import com.hvh.service.RoomTypeService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author 03358
 */
@Service
public class RoomTypeServiceImpl implements RoomTypeService{
    @Autowired
    private RoomTypeRepository typeRepo;
    
    @Override
    public List<RoomType> getType() {
        return this.typeRepo.getType();
    }

    @Override
    @org.springframework.transaction.annotation.Transactional
    public void addOrUpdate(RoomType rt) {
        this.typeRepo.addOrUpdate(rt);
    }

    @Override
    public RoomType getRoomTypeById(Long id) {
        return this.typeRepo.getById(id);
    }

    @Override
    @org.springframework.transaction.annotation.Transactional
    public void deleteRoomType(Long id) {
        this.typeRepo.delete(id);
    }
}
