/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.hvh.repository;
import java.util.List;
import java.util.Map;
/**
 *
 * @author 03358
 */

public interface ChatRepository {
    String getOrCreateRoom(Long guestId, String guestName);
    void sendMessage(String roomId, Long senderId, String senderRole, String senderName, String text);
    List<Map<String, Object>> getMessages(String roomId, int limit);
    List<Map<String, Object>> getAllRooms();
    void markAsSeen(String roomId);
}
