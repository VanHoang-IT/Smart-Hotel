package com.hvh.repository;

import com.hvh.pojo.RoomImages;
import java.util.List;

public interface RoomImagesRepository {
    List<RoomImages> getByRoomId(Long roomId);
    void addImage(RoomImages img);
    RoomImages getById(Long id);
    void delete(Long id);
}
