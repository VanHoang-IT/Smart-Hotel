/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hvh.controllers;

import com.hvh.pojo.Room;
import com.hvh.service.RoomService;
import com.hvh.service.RoomTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

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

    @GetMapping("/room")
    public String createView(Model model) {
        model.addAttribute("room", new Room());
        return "room";
    }

    @PostMapping("/room")
    public String createView(Model model, @ModelAttribute(value = "room") Room r) {
        try {
            this.roomService.addOrUpdateRoom(r);
            return "redirect:/";
        } catch (Exception ex) {
            model.addAttribute("errMsg", "Lỗi: Số phòng '" + r.getRoomNumber() + "' đã tồn tại trong hệ thống!");

            model.addAttribute("roomType", this.typeService.getType());
            return "room";
        }
    }
    
    @GetMapping("/room/{roomId}")
    public String updateView(Model model, @PathVariable(value = "roomId") int id){
       model.addAttribute("room", this.roomService.getRoomById(id));
       return "room";
    }
}
