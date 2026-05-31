/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hvh.service.impl;
import com.hvh.pojo.User;
import com.hvh.repository.ChatRepository;
import com.hvh.repository.UserRepository;
import com.hvh.service.ChatService;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
/**
 *
 * @author 03358
 */ 
@Service
public class ChatServiceImpl implements ChatService {
 
    @Autowired
    private ChatRepository chatRepository;
 
    @Autowired
    private UserRepository userRepository;
 
    @Override
    public Map<String, String> getOrCreateRoom(String username) {
        User user = userRepository.getUserByUsername(username);
        String guestName = user.getFullName() != null && !user.getFullName().isBlank()
                ? user.getFullName()
                : user.getUsername();
        String roomId = chatRepository.getOrCreateRoom(user.getId(), guestName);
        Map<String, String> result = new HashMap<>();
        result.put("room_id", roomId);
        return result;
    }
 
    @Override
    public void sendMessage(String roomId, String username, String text) {
        User user = userRepository.getUserByUsername(username);
        String senderName = user.getFullName() != null && !user.getFullName().isBlank()
                ? user.getFullName()
                : user.getUsername();
        chatRepository.sendMessage(roomId, user.getId(), user.getRole(), senderName, text);
    }
 
    @Override
    public List<Map<String, Object>> getMessages(String roomId, int limit) {
        return chatRepository.getMessages(roomId, limit);
    }
 
    @Override
    public List<Map<String, Object>> getAllRooms() {
        return chatRepository.getAllRooms();
    }
 
    @Override
    public void markAsSeen(String roomId) {
        chatRepository.markAsSeen(roomId);
    }
}