/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.hvh.service;
import java.util.List;
import java.util.Map;
/**
 *
 * @author 03358
 */
public interface ChatService {
    Map<String, String> getOrCreateRoom(String username);
    void sendMessage(String roomId, String username, String text);
    List<Map<String, Object>> getMessages(String roomId, int limit);
    List<Map<String, Object>> getAllRooms();
    void markAsSeen(String roomId);
}
