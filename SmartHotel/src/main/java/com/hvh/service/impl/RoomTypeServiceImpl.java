/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hvh.service.impl;

import com.hvh.pojo.RoomType;
import com.hvh.repository.RoomTypeRepository;
import com.hvh.service.RoomTypeService;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        rt.setActive(true);
        this.typeRepo.addOrUpdate(rt);
    }

    @Override
    public RoomType getRoomTypeById(Long id) {
        return this.typeRepo.getById(id);
    }

    @Override
    @Transactional
    public void deleteRoomType(Long id) {
        this.typeRepo.delete(id);
    }

    @Override
    @Transactional
    public void updateRoomType(Long id, Map<String, Object> body) {
        RoomType rt = this.typeRepo.getById(id);
        if (rt == null) {
            throw new RuntimeException("Không tìm thấy loại phòng");
        }
        if (body.get("name") != null && !body.get("name").toString().trim().isEmpty()) {
            rt.setName(body.get("name").toString().trim());
        }
        if (body.get("capacity") != null && !body.get("capacity").toString().trim().isEmpty()) {
            rt.setCapacity(Integer.parseInt(body.get("capacity").toString().trim()));
        }
        if (body.get("description") != null) {
            rt.setDescription(body.get("description").toString());
        }
        this.typeRepo.addOrUpdate(rt);
    }
}