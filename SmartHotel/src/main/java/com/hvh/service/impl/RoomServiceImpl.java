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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    @Transactional(readOnly = true)
    public List<Room> getRooms(Map<String, String> params) {
        List<Room> rooms = this.roomRepo.getRoom(params);

        if (rooms != null) {
            rooms.forEach(r -> {
                if (r.getRoomImagesSet() != null) {
                    r.getRoomImagesSet().size();
                }
            });
        }
        return rooms;
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

    @Transactional
    @Override
    public Room getRoomById(long id) {
        Room r = this.roomRepo.getRoomById(id);

        if (r != null) {
            if (r.getRoomImagesSet() != null) {
                r.getRoomImagesSet().size();
            }
        }

        return r;
    }

    @Override
    public void deleteRoom(long id) {
        this.roomRepo.deleteRoom(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Room> getRoomAvailable(String checkIn, String checkOut) {
        List<Room> rooms = this.roomRepo.getRoomAvailable(checkIn, checkOut);
        if (rooms != null) {
            rooms.forEach(r -> {
                if (r.getRoomImagesSet() != null) {
                    r.getRoomImagesSet().size();
                }
            });
        }
        return rooms;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getRoomBookings(int roomId) {
        return this.roomRepo.getRoomBookings(roomId);
    }

    @Override
    @Transactional
    public void createRoom(Map<String, Object> body) {
        String name = body.get("name") != null ? body.get("name").toString().trim() : null;
        String price = body.get("price") != null ? body.get("price").toString().trim() : null;
        String roomTypeIdStr = body.get("roomTypeId") != null ? body.get("roomTypeId").toString().trim() : null;

        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Tên phòng không được trống");
        }
        if (price == null || price.isEmpty()) {
            throw new IllegalArgumentException("Giá không được trống");
        }
        if (roomTypeIdStr == null || roomTypeIdStr.isEmpty()) {
            throw new IllegalArgumentException("Loại phòng không được trống");
        }

        Room r = new Room();
        r.setName(name);
        r.setPrice(new java.math.BigDecimal(price));
        r.setStatus(body.getOrDefault("status", "AVAILABLE").toString());
        r.setNote(body.get("note") != null ? body.get("note").toString() : null);
        r.setMainImage(body.get("mainImage") != null ? body.get("mainImage").toString() : null);
        if (body.get("floor") != null && !body.get("floor").toString().trim().isEmpty()) {
            r.setFloor(Integer.parseInt(body.get("floor").toString().trim()));
        }
        r.setRoomTypeId(new com.hvh.pojo.RoomType(Long.valueOf(roomTypeIdStr)));
        this.roomRepo.addOrUpdateRoom(r);
    }

    @Override
    @Transactional
    public void updateRoom(Long id, Map<String, Object> body) {
        Room r = this.roomRepo.getRoomById(id);
        if (r == null) {
            throw new RuntimeException("Không tìm thấy phòng");
        }
        if (body.get("name") != null && !body.get("name").toString().trim().isEmpty()) {
            r.setName(body.get("name").toString().trim());
        }
        if (body.get("price") != null && !body.get("price").toString().trim().isEmpty()) {
            r.setPrice(new java.math.BigDecimal(body.get("price").toString().trim()));
        }
        if (body.get("status") != null) {
            r.setStatus(body.get("status").toString());
        }
        if (body.get("note") != null) {
            r.setNote(body.get("note").toString());
        }
        if (body.get("floor") != null && !body.get("floor").toString().trim().isEmpty()) {
            r.setFloor(Integer.parseInt(body.get("floor").toString().trim()));
        }
        if (body.get("mainImage") != null && !body.get("mainImage").toString().isEmpty()) {
            r.setMainImage(body.get("mainImage").toString());
        }
        if (body.get("roomTypeId") != null && !body.get("roomTypeId").toString().trim().isEmpty()) {
            r.setRoomTypeId(new com.hvh.pojo.RoomType(Long.valueOf(body.get("roomTypeId").toString().trim())));
        }
        this.roomRepo.addOrUpdateRoom(r);
    }

    private void validateRoom(Room r) {
        if (r.getName() == null || r.getName().isBlank()) {
            throw new IllegalArgumentException("Tên phòng không được trống");
        }
        if (r.getPrice() == null) {
            throw new IllegalArgumentException("Giá không được trống");
        }
        if (r.getRoomTypeId() == null) {
            throw new IllegalArgumentException("Loại phòng không được trống");
        }
    }

}