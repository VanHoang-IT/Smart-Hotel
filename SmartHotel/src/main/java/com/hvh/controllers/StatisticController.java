/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hvh.controllers;

import com.hvh.service.StatisticService;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 *
 * @author 03358
 */
@Controller
@RequestMapping("/admin/statistics")
public class StatisticController {

    @Autowired
    private StatisticService statisticService;

    @GetMapping
    public String statistics(Model model,
            @RequestParam(name = "year", required = false) Integer year) {

        List<Integer> availableYears = statisticService.getAvailableYears();

        if (year != null && year > 0) {
            model.addAttribute("summary", statisticService.getSummary(year));
            model.addAttribute("monthlyRevenue", statisticService.getMonthlyRevenue(year));
            model.addAttribute("topRooms", statisticService.getTopRoomsByRevenue(5, year));
            model.addAttribute("topServices", statisticService.getTopServicesByRevenue(5, year));
            model.addAttribute("byStatus", statisticService.getReservationsByStatus(year));
        } else {
            model.addAttribute("summary", statisticService.getSummary());
            model.addAttribute("monthlyRevenue", statisticService.getMonthlyRevenue());
            model.addAttribute("topRooms", statisticService.getTopRoomsByRevenue(5));
            model.addAttribute("topServices", statisticService.getTopServicesByRevenue(5));
            model.addAttribute("byStatus", statisticService.getReservationsByStatus());
        }

        model.addAttribute("availableYears", availableYears);
        model.addAttribute("selectedYear", year);
        return "statistics";
    }
}