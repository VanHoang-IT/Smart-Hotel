/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hvh.controllers;

import com.hvh.service.RoomService;
import com.hvh.service.RoomTypeService;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;


/**
 *
 * @author 03358
 */
@Controller
@ControllerAdvice
public class HomeController {
    @Autowired
    private RoomTypeService typeService;
    
    @Autowired
    private RoomService roomService;
    
    @ModelAttribute
    public void commonResponse(Model model){
        model.addAttribute("roomType", this.typeService.getType());
    }
    
    @RequestMapping("/")
    public String index(Model model, @RequestParam Map<String, String> params){
        model.addAttribute("roomType", this.typeService.getType());
        model.addAttribute("room", this.roomService.getRooms(params));
        return "index";
    }
}
