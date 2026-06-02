/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author 03358
 */
package com.hvh.repository.impl;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.hvh.repository.ChatRepository;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.springframework.stereotype.Repository;
 
@Repository
public class ChatRepositoryImpl implements ChatRepository {
 
    private DatabaseReference db() {
        return FirebaseDatabase.getInstance(
            "https://smarthotel-fc975-default-rtdb.asia-southeast1.firebasedatabase.app"
        ).getReference();
    }
 
    @Override
    public String getOrCreateRoom(Long guestId, String guestName) {
        String roomId = "guest_" + guestId;
        DatabaseReference infoRef = db().child("chats").child(roomId).child("info");
 
        CountDownLatch latch = new CountDownLatch(1);
        infoRef.addListenerForSingleValueEvent(new com.google.firebase.database.ValueEventListener() {
            @Override
            public void onDataChange(com.google.firebase.database.DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    Map<String, Object> info = new HashMap<>();
                    info.put("guest_id", guestId);
                    info.put("guest_name", guestName);
                    info.put("last_message", "");
                    info.put("updated_at", System.currentTimeMillis());
                    infoRef.setValueAsync(info);
                }
                latch.countDown();
            }
 
            @Override
            public void onCancelled(com.google.firebase.database.DatabaseError error) {
                latch.countDown();
            }
        });
 
        try { latch.await(3, TimeUnit.SECONDS); } catch (InterruptedException ignored) {}
        return roomId;
    }
 
    @Override
    public void sendMessage(String roomId, Long senderId, String senderRole, String senderName, String text) {
        Map<String, Object> msg = new HashMap<>();
        msg.put("sender_id", senderId);
        msg.put("sender_role", senderRole);
        msg.put("sender_name", senderName);
        msg.put("text", text);
        msg.put("is_seen", false);
        msg.put("timestamp", System.currentTimeMillis());
 
        db().child("chats").child(roomId).child("messages").push().setValueAsync(msg);
 
        Map<String, Object> info = new HashMap<>();
        info.put("last_message", text);
        info.put("updated_at", System.currentTimeMillis());
        db().child("chats").child(roomId).child("info").updateChildrenAsync(info);
    }
 
    @Override
    public List<Map<String, Object>> getMessages(String roomId, int limit) {
        List<Map<String, Object>> result = new ArrayList<>();
        CountDownLatch latch = new CountDownLatch(1);
 
        db().child("chats").child(roomId).child("messages")
                .orderByChild("timestamp")
                .limitToLast(limit)
                .addListenerForSingleValueEvent(new com.google.firebase.database.ValueEventListener() {
                    @Override
                    public void onDataChange(com.google.firebase.database.DataSnapshot snapshot) {
                        for (com.google.firebase.database.DataSnapshot child : snapshot.getChildren()) {
                            Map<String, Object> msg = new HashMap<>();
                            msg.put("id", child.getKey());
                            msg.putAll((Map<String, Object>) child.getValue());
                            result.add(msg);
                        }
                        latch.countDown();
                    }
 
                    @Override
                    public void onCancelled(com.google.firebase.database.DatabaseError error) {
                        latch.countDown();
                    }
                });
 
        try { latch.await(5, TimeUnit.SECONDS); } catch (InterruptedException ignored) {}
        return result;
    }
 
    @Override
    public List<Map<String, Object>> getAllRooms() {
        List<Map<String, Object>> result = new ArrayList<>();
        CountDownLatch latch = new CountDownLatch(1);
 
        db().child("chats").addListenerForSingleValueEvent(new com.google.firebase.database.ValueEventListener() {
            @Override
            public void onDataChange(com.google.firebase.database.DataSnapshot snapshot) {
                for (com.google.firebase.database.DataSnapshot room : snapshot.getChildren()) {
                    com.google.firebase.database.DataSnapshot infoSnap = room.child("info");
                    if (infoSnap.exists()) {
                        Map<String, Object> info = new HashMap<>();
                        info.put("room_id", room.getKey());
                        info.putAll((Map<String, Object>) infoSnap.getValue());
                        result.add(info);
                    }
                }
                latch.countDown();
            }
 
            @Override
            public void onCancelled(com.google.firebase.database.DatabaseError error) {
                latch.countDown();
            }
        });
 
        try { latch.await(5, TimeUnit.SECONDS); } catch (InterruptedException ignored) {}
        return result;
    }
 
    @Override
    public void markAsSeen(String roomId) {
        CountDownLatch latch = new CountDownLatch(1);
        db().child("chats").child(roomId).child("messages")
                .addListenerForSingleValueEvent(new com.google.firebase.database.ValueEventListener() {
                    @Override
                    public void onDataChange(com.google.firebase.database.DataSnapshot snapshot) {
                        for (com.google.firebase.database.DataSnapshot msg : snapshot.getChildren()) {
                            Object seen = msg.child("is_seen").getValue();
                            if (Boolean.FALSE.equals(seen)) {
                                msg.getRef().child("is_seen").setValueAsync(true);
                            }
                        }
                        latch.countDown();
                    }
 
                    @Override
                    public void onCancelled(com.google.firebase.database.DatabaseError error) {
                        latch.countDown();
                    }
                });
        try { latch.await(5, TimeUnit.SECONDS); } catch (InterruptedException ignored) {}
    }
}