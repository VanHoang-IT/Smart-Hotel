/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hvh.controllers;

import com.hvh.pojo.Reservation;
import com.hvh.pojo.ServiceOrder;
import com.hvh.service.ReservationService;
import com.hvh.service.ServiceOrderService;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
public class ApiReservationController {
    @Autowired
    private ReservationService resService;
    
    @Autowired
    private ServiceOrderService serOrderService;
    
    @GetMapping("/reservations")
    public ResponseEntity<List<Reservation>> list(@RequestParam Map<String, String> params) {
        return new ResponseEntity<>(this.resService.getReservations(params), HttpStatus.OK);
    } 
    
    @PatchMapping("/reservations/{id}/cancel")
    public ResponseEntity<String> cancel(@PathVariable long id) {
        Reservation r = this.resService.getReservationById(id);
        if (r != null) {
            r.setStatus("CANCELED");
            this.resService.addOrUpdateReservation(r);
            return new ResponseEntity<>("Canceled", HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    
    @PostMapping("/service-orders")
    @ResponseStatus(HttpStatus.CREATED)
    public void createServiceOrder(@RequestBody ServiceOrder order){
        this.serOrderService.addOrUpdate(order);
    }
}
