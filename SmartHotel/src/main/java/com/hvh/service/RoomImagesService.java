package com.hvh.service;

import java.util.List;
import java.util.Map;

public interface RoomImagesService {
    List<Map<String, Object>> getImagesByRoomId(Long roomId);
    void addImage(Long roomId, String imageUrl);
    void deleteImage(Long imageId);
}
