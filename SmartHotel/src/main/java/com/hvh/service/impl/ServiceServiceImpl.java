/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hvh.service.impl;

import com.hvh.service.ServiceService;
import com.hvh.pojo.Services;
import com.hvh.repository.ServiceRepository;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 *
 * @author 03358
 */
@Repository
public class ServiceServiceImpl implements ServiceService{
    @Autowired
    private ServiceRepository serviceRepo;
    
    
    @Override
    public List<Services> getServices(Map<String, String> params) {
        return this.serviceRepo.getServices(params);
    }

    @Override
    public void addOrUpdate(Services s) {
        this.serviceRepo.addOrUpdate(s);
    }

    @Override
    public Services getServiceById(Long id) {
        return this.serviceRepo.getServiceById(id);
    }

    @Override
    public void deleteService(Long id) {
        this.serviceRepo.deleteService(id);
    }
    
}
