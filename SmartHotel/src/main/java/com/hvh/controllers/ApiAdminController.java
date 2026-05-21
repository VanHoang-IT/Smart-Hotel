package com.hvh.controllers;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.hvh.pojo.Room;
import com.hvh.pojo.RoomType;
import com.hvh.pojo.User;
import com.hvh.service.HousekeepingTaskService;
import com.hvh.service.RoomImagesService;
import com.hvh.service.RoomService;
import com.hvh.service.RoomTypeService;
import com.hvh.service.UserService;
import java.math.BigDecimal;
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

    @GetMapping("/users")
    public ResponseEntity<List<User>> getUsers() {
        return new ResponseEntity<>(this.userService.getUsers(), HttpStatus.OK);
    }

    @PatchMapping("/users/{id}/role")
    public ResponseEntity<String> updateRole(
            @PathVariable("id") Long id,
            @RequestBody Map<String, String> body) {
        String role = body.get("role");
        if (role == null || role.isBlank()) {
            return new ResponseEntity<>("Role is required", HttpStatus.BAD_REQUEST);
        }
        try {
            this.userService.updateRole(id, role);
            return new ResponseEntity<>(role, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/rooms")
    public ResponseEntity<?> addRoom(@RequestBody Map<String, Object> body) {
        try {
            String name = body.get("name") != null ? body.get("name").toString().trim() : "";
            String price = body.get("price") != null ? body.get("price").toString().trim() : "";
            String roomTypeIdStr = body.get("roomTypeId") != null ? body.get("roomTypeId").toString().trim() : "";

            if (name.isEmpty()) {
                return new ResponseEntity<>("Tên phòng không được trống", HttpStatus.BAD_REQUEST);
            }
            if (price.isEmpty()) {
                return new ResponseEntity<>("Giá không được trống", HttpStatus.BAD_REQUEST);
            }
            if (roomTypeIdStr.isEmpty()) {
                return new ResponseEntity<>("Loại phòng không được trống", HttpStatus.BAD_REQUEST);
            }

            Room r = new Room();
            r.setName(name);
            r.setPrice(new BigDecimal(price));
            r.setStatus(body.getOrDefault("status", "AVAILABLE").toString());
            r.setNote(body.get("note") != null ? body.get("note").toString() : null);
            r.setMainImage(body.get("mainImage") != null ? body.get("mainImage").toString() : null);
            if (body.get("floor") != null && !body.get("floor").toString().trim().isEmpty()) {
                r.setFloor(Integer.parseInt(body.get("floor").toString().trim()));
            }

            RoomType rt = new RoomType(Long.valueOf(roomTypeIdStr));
            r.setRoomTypeId(rt);

            this.roomService.addOrUpdateRoomJson(r);
            return new ResponseEntity<>(HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/rooms/{id}")
    public ResponseEntity<?> updateRoom(@PathVariable("id") Long id, @RequestBody Map<String, Object> body) {
        try {
            Room r = this.roomService.getRoomById(id);
            if (r == null) {
                return new ResponseEntity<>("Không tìm thấy phòng", HttpStatus.NOT_FOUND);
            }

            if (body.get("name") != null && !body.get("name").toString().trim().isEmpty()) {
                r.setName(body.get("name").toString().trim());
            }
            if (body.get("price") != null && !body.get("price").toString().trim().isEmpty()) {
                r.setPrice(new BigDecimal(body.get("price").toString().trim()));
            }
            if (body.get("status") != null) {
                r.setStatus(body.get("status").toString());
            }
            if (body.get("note") != null) {
                r.setNote(body.get("note").toString());
            }
            if (body.get("floor") != null && !body.get("floor").toString().trim().isEmpty()) {
                r.setFloor(Integer.parseInt(body.get("floor").toString().trim()));
            }
            if (body.get("mainImage") != null && !body.get("mainImage").toString().isEmpty()) {
                r.setMainImage(body.get("mainImage").toString());
            }
            if (body.get("roomTypeId") != null && !body.get("roomTypeId").toString().trim().isEmpty()) {
                r.setRoomTypeId(new RoomType(Long.valueOf(body.get("roomTypeId").toString().trim())));
            }

            this.roomService.addOrUpdateRoomJson(r);
            return new ResponseEntity<>(HttpStatus.OK);
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

    @PostMapping("/room-types")
    public ResponseEntity<?> addRoomType(@RequestBody RoomType rt) {
        try {
            rt.setActive(true);
            this.roomTypeService.addOrUpdate(rt);
            return new ResponseEntity<>(rt, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/room-types/{id}")
    public ResponseEntity<?> updateRoomType(@PathVariable("id") Long id, @RequestBody Map<String, Object> body) {
        try {
            RoomType rt = this.roomTypeService.getRoomTypeById(id);
            if (rt == null) {
                return new ResponseEntity<>("Không tìm thấy loại phòng", HttpStatus.NOT_FOUND);
            }
            if (body.get("name") != null && !body.get("name").toString().trim().isEmpty()) {
                rt.setName(body.get("name").toString().trim());
            }
            if (body.get("capacity") != null && !body.get("capacity").toString().trim().isEmpty()) {
                rt.setCapacity(Integer.parseInt(body.get("capacity").toString().trim()));
            }
            if (body.get("description") != null) {
                rt.setDescription(body.get("description").toString());
            }
            this.roomTypeService.addOrUpdate(rt);
            return new ResponseEntity<>(rt, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/room-types/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteRoomType(@PathVariable("id") Long id) {
        this.roomTypeService.deleteRoomType(id);
    }

    @GetMapping("/housekeeping")
    public ResponseEntity<List<Map<String, Object>>> getTasks() {
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
        if (status == null || status.isBlank()) {
            return new ResponseEntity<>("Status is required", HttpStatus.BAD_REQUEST);
        }
        try {
            this.housekeepingService.updateStatus(id, status);
            return new ResponseEntity<>(status, HttpStatus.OK);
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
}
