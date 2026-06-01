/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hvh.controllers;
/**
 *
 * @author 0335
 */
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.hvh.pojo.RoomType;
import com.hvh.pojo.User;
import com.hvh.service.HousekeepingTaskService;
import com.hvh.service.RoomImagesService;
import com.hvh.service.RoomService;
import com.hvh.service.RoomTypeService;
import com.hvh.service.StatisticService;
import com.hvh.service.UserService;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/secure/admin")
@CrossOrigin
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
public class ApiAdminController {

    @Autowired
    private UserService userService;
    @Autowired
    private RoomService roomService;
    @Autowired
    private RoomTypeService roomTypeService;
    @Autowired
    private HousekeepingTaskService housekeepingService;
    @Autowired
    private RoomImagesService roomImagesService;
    @Autowired
    private Cloudinary cloudinary;
    @Autowired
    private StatisticService statisticService;
    
    //UPLOAD ẢNH
    @PostMapping("/upload-image")
    public ResponseEntity<?> uploadImage(@RequestParam("file") MultipartFile file) {
        try {
            Map result = this.cloudinary.uploader().upload(
                    file.getBytes(),
                    ObjectUtils.asMap("resource_type", "auto")
            );
            return new ResponseEntity<>(
                    Collections.singletonMap("url", result.get("secure_url")),
                    HttpStatus.OK
            );
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
    
    //QUẢN LÝ USER
    @GetMapping("/users")
    public ResponseEntity<List<User>> getUsers(@RequestParam(name = "page", required = false) Integer page) {
        if (page != null) {
            return new ResponseEntity<>(this.userService.getUsers(page), HttpStatus.OK);
        }
        return new ResponseEntity<>(this.userService.getUsers(), HttpStatus.OK);
    }

    @PatchMapping("/users/{id}/role")
    public ResponseEntity<String> updateRole(
            @PathVariable("id") Long id,
            @RequestBody Map<String, String> body) {
        String role = body.get("role");
        try {
            this.userService.updateRole(id, role);
            return new ResponseEntity<>(role, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }
    
    //QUẢN LÝ PHÒNG
    @PostMapping("/rooms")
    public ResponseEntity<?> addRoom(@RequestBody Map<String, Object> body) {
        try {
            this.roomService.createRoom(body);
            return new ResponseEntity<>(HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/rooms/{id}")
    public ResponseEntity<?> updateRoom(@PathVariable("id") Long id, @RequestBody Map<String, Object> body) {
        try {
            this.roomService.updateRoom(id, body);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/rooms/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteRoom(@PathVariable("id") Long id) {
        this.roomService.deleteRoom(id);
    }

    @GetMapping("/rooms/{roomId}/images")
    public ResponseEntity<List<Map<String, Object>>> getRoomImages(@PathVariable("roomId") Long roomId) {
        return new ResponseEntity<>(this.roomImagesService.getImagesByRoomId(roomId), HttpStatus.OK);
    }

    @PostMapping("/rooms/{roomId}/images")
    public ResponseEntity<?> addRoomImage(
            @PathVariable("roomId") Long roomId,
            @RequestParam("file") MultipartFile file) {
        try {
            Map result = this.cloudinary.uploader().upload(
                    file.getBytes(),
                    ObjectUtils.asMap("resource_type", "auto")
            );
            String url = result.get("secure_url").toString();
            this.roomImagesService.addImage(roomId, url);
            return new ResponseEntity<>(
                    Collections.singletonMap("imageUrl", url),
                    HttpStatus.CREATED
            );
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/room-images/{imageId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteRoomImage(@PathVariable("imageId") Long imageId) {
        this.roomImagesService.deleteImage(imageId);
    }
    
    //QUẢN LÝ LOẠI PHÒNG
    @PostMapping("/room-types")
    public ResponseEntity<?> addRoomType(@RequestBody RoomType rt) {
        try {
            this.roomTypeService.addOrUpdate(rt);
            return new ResponseEntity<>(rt, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/room-types/{id}")
    public ResponseEntity<?> updateRoomType(@PathVariable("id") Long id, @RequestBody Map<String, Object> body) {
        try {
            this.roomTypeService.updateRoomType(id, body);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/room-types/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteRoomType(@PathVariable("id") Long id) {
        this.roomTypeService.deleteRoomType(id);
    }
    
    //QUẢN LÝ VIỆC CỦA STAFF
    @GetMapping("/housekeeping")
    public ResponseEntity<List<Map<String, Object>>> getTasks(@RequestParam(name = "page", required = false) Integer page) {
        if (page != null) {
            return new ResponseEntity<>(this.housekeepingService.getAll(page), HttpStatus.OK);
        }
        return new ResponseEntity<>(this.housekeepingService.getAll(), HttpStatus.OK);
    }

    @PostMapping("/housekeeping")
    public ResponseEntity<?> addTask(@RequestBody Map<String, Object> body) {
        try {
            this.housekeepingService.addOrUpdate(body);
            return new ResponseEntity<>(HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PatchMapping("/housekeeping/{id}/status")
    public ResponseEntity<String> updateTaskStatus(
            @PathVariable("id") Long id,
            @RequestBody Map<String, String> body) {
        String status = body.get("status");
        try {
            this.housekeepingService.updateStatus(id, status);
            return new ResponseEntity<>(status, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/housekeeping/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTask(@PathVariable("id") Long id) {
        this.housekeepingService.delete(id);
    }

    @GetMapping("/staff")
    public ResponseEntity<List<User>> getStaff() {
        List<User> all = this.userService.getUsers();
        List<User> staff = all.stream()
                .filter(u -> u.getRole() != null && u.getRole().contains("STAFF"))
                .collect(Collectors.toList());
        return new ResponseEntity<>(staff, HttpStatus.OK);
    }

    //THỐNG KÊ
    @GetMapping("/revenue/monthly")
    public ResponseEntity<List<Object[]>> getMonthlyRevenue() {
        return new ResponseEntity<>(this.statisticService.getMonthlyRevenue(), HttpStatus.OK);
    }
}