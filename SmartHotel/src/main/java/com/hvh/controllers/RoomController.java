/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hvh.controllers;

import com.hvh.pojo.Room;
import com.hvh.service.RoomService;
import com.hvh.service.RoomTypeService;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 *
 * @author 03358
 */
@Controller
@RequestMapping("/admin")
public class RoomController {

    @Autowired
    private RoomService roomService;

    @Autowired
    private RoomTypeService typeService;

    @GetMapping("/rooms")
    public String listRooms(Model model,
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "kw", required = false) String kw,
            @RequestParam(name = "status", required = false) String status,
            @RequestParam(name = "typeId", required = false) String typeId) {

        Map<String, String> params = new HashMap<>();
        params.put("page", String.valueOf(page));
        if (kw != null && !kw.isBlank()) params.put("kw", kw);
        if (status != null && !status.isBlank()) params.put("status", status);
        if (typeId != null && !typeId.isBlank()) params.put("typeId", typeId);

        model.addAttribute("rooms", this.roomService.getRooms(params));
        model.addAttribute("room", new Room());
        model.addAttribute("page", page);
        model.addAttribute("kw", kw);
        model.addAttribute("status", status);
        model.addAttribute("typeId", typeId);
        return "rooms";
    }

    @PostMapping("/rooms")
    public String addRoom(Model model, @ModelAttribute(value = "room") Room r) {
        try {
            this.roomService.addOrUpdateRoom(r);
            return "redirect:/admin/rooms";
        } catch (Exception ex) {
            model.addAttribute("errMsg", "Lỗi: Phòng tên '" + r.getName() + "' đã tồn tại!");
            model.addAttribute("rooms", this.roomService.getRooms(new HashMap<>()));
            return "rooms";
        }
    }

    @GetMapping("/rooms/{roomId}")
    public String editRoom(Model model, @PathVariable(value = "roomId") long id) {
        Map<String, String> params = new HashMap<>();
        params.put("page", "1");
        model.addAttribute("room", this.roomService.getRoomById(id));
        model.addAttribute("rooms", this.roomService.getRooms(params));
        model.addAttribute("page", 1);
        model.addAttribute("kw", null);
        model.addAttribute("status", null);
        model.addAttribute("typeId", null);
        return "rooms";
    }

    @PostMapping("/rooms/{roomId}")
    public String updateRoom(Model model,
            @PathVariable(value = "roomId") long id,
            @ModelAttribute(value = "room") Room r) {
        try {
            r.setId(id);
            this.roomService.addOrUpdateRoom(r);
            return "redirect:/admin/rooms";
        } catch (Exception ex) {
            model.addAttribute("errMsg", "Lỗi cập nhật phòng!");
            model.addAttribute("rooms", this.roomService.getRooms(new HashMap<>()));
            return "rooms";
        }
    }
}