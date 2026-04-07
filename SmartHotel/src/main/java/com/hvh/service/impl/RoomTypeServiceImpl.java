/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hvh.service.impl;

import com.hvh.pojo.RoomTypes;
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
    public List<RoomTypes> getTypes() {
        return this.typeRepo.getTypes();
    }
    
}
