/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hvh.controllers;
/**
 *
 * @author 0335
 */
import com.hvh.service.ChatService;
import java.security.Principal;
import java.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/api")
@CrossOrigin
public class ApiChatController {

    @Autowired
    private ChatService chatService;

    @Value("${gemini.api.key}")
    private String geminiApiKey;

    @PostMapping(value = "/secure/chat/room", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, String>> getOrCreateRoom(Principal principal) {
        Map<String, String> result = chatService.getOrCreateRoom(principal.getName());
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PostMapping(value = "/secure/chat/send", produces = MediaType.APPLICATION_JSON_VALUE)
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

    @GetMapping(value = "/secure/chat/messages", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<Map<String, Object>>> getMessages(
            @RequestParam("room_id") String roomId,
            @RequestParam(value = "limit", defaultValue = "50") int limit) {
        return new ResponseEntity<>(chatService.getMessages(roomId, limit), HttpStatus.OK);
    }

    @GetMapping(value = "/secure/chat/rooms", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('RECEPTIONIST')")
    public ResponseEntity<List<Map<String, Object>>> getAllRooms() {
        return new ResponseEntity<>(chatService.getAllRooms(), HttpStatus.OK);
    }

    @PatchMapping(value = "/secure/chat/rooms/{roomId}/seen", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('RECEPTIONIST')")
    public ResponseEntity<?> markAsSeen(@PathVariable String roomId) {
        chatService.markAsSeen(roomId);
        return new ResponseEntity<>(Map.of("status", "ok"), HttpStatus.OK);
    }

    @PostMapping("/public/ai-chat")
    public ResponseEntity<?> aiChat(@RequestBody Map<String, Object> payload) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            String url = "https://api.freetheai.xyz/v1/chat/completions";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + geminiApiKey);

            List<Map<String, Object>> messages = (List<Map<String, Object>>) payload.get("messages");
            String systemPrompt = (String) payload.get("system");

            List<Map<String, Object>> chatMessages = new ArrayList<>();
            if (systemPrompt != null) {
                Map<String, Object> sysMsg = new HashMap<>();
                sysMsg.put("role", "system");
                sysMsg.put("content", systemPrompt);
                chatMessages.add(sysMsg);
            }
            for (Map<String, Object> msg : messages) {
                Map<String, Object> m = new HashMap<>();
                m.put("role", "user".equals(msg.get("role")) ? "user" : "assistant");
                m.put("content", msg.get("content"));
                chatMessages.add(m);
            }

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", "opc/deepseek-v4-flash-free");
            requestBody.put("messages", chatMessages);

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
            ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);

            Map<String, Object> body = response.getBody();
            List<Map> choices = (List<Map>) body.get("choices");
            Map message = (Map) choices.get(0).get("message");
            String text = (String) message.get("content");
            return new ResponseEntity<>(Map.of("text", text), HttpStatus.OK);

        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}