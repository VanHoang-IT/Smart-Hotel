/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hvh.service.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.hvh.pojo.Room;
import com.hvh.repository.RoomRepository;
import com.hvh.service.RoomService;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 *
 * @author 03358
 */
@Service
public class RoomServiceImpl implements RoomService {

    @Autowired
    private BCryptPasswordEncoder PasswordEncoder;

    @Autowired
    private RoomRepository roomRepo;

    @Autowired
    private Cloudinary cloudinary;

    @Override
    public List<Room> getRooms(Map<String, String> params) {
        return this.roomRepo.getRoom(params);
    }

    @Override
    public void addOrUpdateRoom(Room r) {
        if (!r.getFile().isEmpty()) {
            try {
                Map res = this.cloudinary.uploader().upload(r.getFile().getBytes(),
                        ObjectUtils.asMap("resource_type", "auto"));
                r.setMainImage((String) res.get("secure_url"));
            } catch (IOException ex) {
                Logger.getLogger(RoomServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        this.roomRepo.addOrUpdateRoom(r);
    }

    @Override
    public Room getRoomById(long id) {
        return this.roomRepo.getRoomById(id);
    }

    @Override
    public void deleteRoom(long id) {
        this.roomRepo.deleteRoom(id);
    }

    @Override
    public List<Room> getRoomAvailable() {
        return this.roomRepo.getRoomAvailable();
    }

}
