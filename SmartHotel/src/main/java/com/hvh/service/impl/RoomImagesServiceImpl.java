package com.hvh.service.impl;

import com.hvh.pojo.Room;
import com.hvh.pojo.RoomImages;
import com.hvh.repository.RoomImagesRepository;
import com.hvh.service.RoomImagesService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RoomImagesServiceImpl implements RoomImagesService {

    @Autowired
    private RoomImagesRepository roomImagesRepo;

    @Override
    public List<Map<String, Object>> getImagesByRoomId(Long roomId) {
        List<RoomImages> list = roomImagesRepo.getByRoomId(roomId);
        List<Map<String, Object>> result = new ArrayList<>();
        for (RoomImages img : list) {
            Map<String, Object> m = new HashMap<>();
            m.put("id", img.getId());
            m.put("imageUrl", img.getImageUrl());
            result.add(m);
        }
        return result;
    }

    @Override
    @Transactional
    public void addImage(Long roomId, String imageUrl) {
        RoomImages img = new RoomImages();
        img.setImageUrl(imageUrl);
        Room room = new Room(roomId);
        img.setRoomId(room);
        roomImagesRepo.addImage(img);
    }

    @Override
    @Transactional
    public void deleteImage(Long imageId) {
        roomImagesRepo.delete(imageId);
    }
}
