/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hvh.controllers;

import com.hvh.pojo.Room;
import com.hvh.service.RoomService;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author 03358
 */
@RestController
@RequestMapping("/api")
@CrossOrigin
public class ApiRoomController {

    @Autowired
    private RoomService roomService;

    @GetMapping("/rooms")
    public ResponseEntity<List<Room>> list(@RequestParam Map<String, String> params) {
        return new ResponseEntity<>(this.roomService.getRooms(params), HttpStatus.OK);
    }

    @GetMapping(value = "/rooms/{roomId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Room> getRoomDetails(@PathVariable("roomId") int id) {
        return new ResponseEntity<>(this.roomService.getRoomById(id), HttpStatus.OK);
    }

    @DeleteMapping("/secure/rooms/{roomId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable(value = "roomId") int id) {
        this.roomService.deleteRoom(id);
    }

    @GetMapping("/rooms/available")
    public ResponseEntity<List<Room>> listRoomAvailable() {
        return new ResponseEntity<>(this.roomService.getRoomAvailable(), HttpStatus.OK);
    }
}