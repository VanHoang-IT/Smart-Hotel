/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hvh.controllers;
import com.hvh.service.ChatService;
import java.security.Principal;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
/**
 *
 * @author 03358
 */ 

@RestController
@RequestMapping("/api/secure/chat")
@CrossOrigin
public class ApiChatController {
 
    @Autowired
    private ChatService chatService;
 
    @PostMapping(value = "/room", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, String>> getOrCreateRoom(Principal principal) {
        Map<String, String> result = chatService.getOrCreateRoom(principal.getName());
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
 
    @PostMapping(value = "/send", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> sendMessage(@RequestBody Map<String, String> body, Principal principal) {
        String roomId = body.get("room_id");
        String text = body.get("text");
        if (roomId == null || roomId.isBlank() || text == null || text.isBlank()) {
            return new ResponseEntity<>(Map.of("error", "Thiếu room_id hoặc text"), HttpStatus.BAD_REQUEST);
        }
        chatService.sendMessage(roomId, principal.getName(), text);
        return new ResponseEntity<>(Map.of("status", "sent"), HttpStatus.CREATED);
    }
 
    @GetMapping(value = "/messages", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<Map<String, Object>>> getMessages(
            @RequestParam("room_id") String roomId,
            @RequestParam(value = "limit", defaultValue = "50") int limit) {
        return new ResponseEntity<>(chatService.getMessages(roomId, limit), HttpStatus.OK);
    }
 
    @GetMapping(value = "/rooms", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('RECEPTIONIST')")
    public ResponseEntity<List<Map<String, Object>>> getAllRooms() {
        return new ResponseEntity<>(chatService.getAllRooms(), HttpStatus.OK);
    }
 
    @PatchMapping(value = "/rooms/{roomId}/seen", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('RECEPTIONIST')")
    public ResponseEntity<?> markAsSeen(@PathVariable String roomId) {
        chatService.markAsSeen(roomId);
        return new ResponseEntity<>(Map.of("status", "ok"), HttpStatus.OK);
    }
}
