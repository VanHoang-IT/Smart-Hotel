/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hvh.controllers;

import com.hvh.pojo.Services;
import com.hvh.service.ServiceService;
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
public class ApiServiceController {
    @Autowired
    private ServiceService serviceService;
    
    @GetMapping("/services")
    public ResponseEntity<List<Services>> list(@RequestParam Map<String, String> params){
        return new ResponseEntity<>(this.serviceService.getServices(params), HttpStatus.OK);
    }
    
    @GetMapping(value = "/services/{servicesId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Services> getById(@PathVariable(value = "servicesId") Long id) {
        Services s = this.serviceService.getServiceById(id);
        if (s != null) {
            return new ResponseEntity<>(s, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    
    @PostMapping(path = "/secure/services", 
                 produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_STAFF')")
    public ResponseEntity<Services> addOrUpdate(@RequestBody Services s) {
        try {
            this.serviceService.addOrUpdate(s);
            return new ResponseEntity<>(s, HttpStatus.CREATED);
        } catch (Exception ex) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
    
    @DeleteMapping("/secure/services/{servicesId}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable(value = "servicesId") long id){
        this.serviceService.deleteService(id);
    }
}
